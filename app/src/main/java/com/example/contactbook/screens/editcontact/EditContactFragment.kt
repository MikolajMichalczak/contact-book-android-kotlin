package com.example.contactbook.screens.contacts

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION
import android.media.AudioAttributes.USAGE_NOTIFICATION_RINGTONE
import android.media.RingtoneManager.TYPE_NOTIFICATION
import android.media.RingtoneManager.getDefaultUri
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.work.WorkInfo
import com.example.contactbook.R
import com.example.contactbook.database.entities.Contact
import com.example.contactbook.databinding.FragmentEditContactBinding
import com.example.contactbook.dialogs.DateAndTimeDialogFragment
import com.example.contactbook.screens.editcontact.EditContactViewModel
import com.example.contactbook.screens.editcontact.EditContactViewModelFactory
import com.example.contactbook.util.CircleTransform
import com.example.contactbook.util.hideKeyboard
import com.squareup.picasso.Picasso
import java.io.File
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*


class EditContactFragment : Fragment() {

    companion object{
        private const val TAG = "EditContactFragment"
        private const val STORAGEREQUEST_RESULT = 1
        private const val CAMERAREQUEST_RESULT = 0
        private const val CAMERA_REQUEST_CODE = 101
        private const val CALL_PHONE_REQUEST_CODE = 100
    }

    private lateinit var viewModel: EditContactViewModel
    private lateinit var binding: FragmentEditContactBinding
    private lateinit var viewModelFactory: EditContactViewModelFactory
    private lateinit var contact: Contact


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_edit_contact, container, false)

        contact = arguments?.getParcelable("contact")!!

        viewModelFactory =
            EditContactViewModelFactory(
                requireActivity().application,
                contact
            )

        viewModel = ViewModelProvider(this, viewModelFactory).get(EditContactViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        if(contact.name.isNotEmpty())
            binding.reminderLinearLayout.visibility = View.VISIBLE


        binding.editTextName.setText(contact?.name)
        binding.editTextNumber.setText((contact?.number))

        viewModel.workManager.getWorkInfosByTagLiveData(contact.contactId.toString())
            .observe(viewLifecycleOwner, Observer { workInfo ->
                // Check if the current work's state is "successfully finished"
                if (workInfo == WorkInfo.State.SUCCEEDED) {
                    Log.i(TAG, "Sukces")
                }
            })

        binding.editTextName.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(view, context!!)
            }
        }

        binding.editTextNumber.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(view, context!!)
            }
        }

        viewModel.toContactsFragment.observe(viewLifecycleOwner, Observer {state ->
            navigateToContactsFragment(state)
        })

        viewModel.invalidDate.observe(viewLifecycleOwner, Observer {state ->
            if(state) {
                Toast.makeText(activity, R.string.invalid_date, Toast.LENGTH_SHORT).show()
                viewModel.changeInvalidDateVariableState()
            }
        })

        viewModel.imageUri.observe(viewLifecycleOwner, Observer {uri ->
            if(uri.isBlank())
                binding.contactImageView.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.person_icon_24))
            else {
                Log.i(TAG, uri.toString())
                Picasso.get().load(uri).placeholder(R.drawable.person_icon_24)
                    .error(R.drawable.person_icon_24) .transform(CircleTransform()).into(binding.contactImageView)
            }
        })

        viewModel.callButtonVisibility.observe(viewLifecycleOwner, Observer {state ->
            if(state)
                binding.phoneCallImageView.visibility = View.VISIBLE
        })

        viewModel.callReminderAndContactData.observe(viewLifecycleOwner, Observer { contactAndReminder ->
            if(contactAndReminder == null) {
                binding.callReminderTextView.text = resources.getString(R.string.set_reminder)
                binding.callReminderBtn.visibility = View.VISIBLE
                binding.deleteCallReminderBtn.visibility = View.GONE
            }
            else{
                if(contactAndReminder.callReminder?.dayTime != null){
                    Log.i(TAG, contactAndReminder.toString())
                    binding.callReminderTextView.text = "Reminder set to \n${DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(contactAndReminder.callReminder?.dayTime)}"
                    binding.callReminderBtn.visibility = View.GONE
                    binding.deleteCallReminderBtn.visibility = View.VISIBLE
                }
                else{
                    binding.callReminderTextView.text = resources.getString(R.string.set_reminder)
                    binding.callReminderBtn.visibility = View.VISIBLE
                    binding.deleteCallReminderBtn.visibility = View.GONE
                }
            }
        })


        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Where to choose a photo?")
        builder.setPositiveButton("Camera") { dialog, which ->
            if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), CAMERA_REQUEST_CODE)
            } else {
                dispatchTakePictureIntent()
            }
        }

        builder.setNegativeButton("Choose file") { dialog, which ->
            val takePictureIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            takePictureIntent.type = "image/*"
//            takePictureIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
//            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivityForResult(
                takePictureIntent,
                1
            )
        }

        binding.contactImageView.setOnClickListener(){
            builder.show()
        }

        binding.callReminderBtn.setOnClickListener {
            val dialogFragment = DateAndTimeDialogFragment { dateTime -> Log.i(
                TAG, dateTime.toString())
                viewModel.saveReminder(dateTime) }
            dialogFragment.show(activity!!.supportFragmentManager, DateAndTimeDialogFragment.TAG)
        }

        binding.deleteCallReminderBtn.setOnClickListener {
            viewModel.deleteReminder()
        }

        createChannel(getString(R.string.call_notification_channel_id), getString(R.string.call_notification_channel_name))

        binding.phoneCallImageView.setOnClickListener {
            makePhoneCall(contact.number)
        }

        return binding.root
    }

    var selectedPhotoUri: Uri? = null
    private var currentPhotoPath: String = ""

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERAREQUEST_RESULT) {
            Log.i(TAG, currentPhotoPath)
            selectedPhotoUri = Uri.fromFile(File(currentPhotoPath))
            Picasso.get().load(selectedPhotoUri).transform(CircleTransform()).into(binding.contactImageView)
            viewModel._imageUri.value = selectedPhotoUri.toString()
        }

        if(requestCode == STORAGEREQUEST_RESULT && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            val takeFlags = data.flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            activity!!.contentResolver.takePersistableUriPermission(selectedPhotoUri!!, takeFlags)
            //Log.i(TAG, selectedPhotoUri.toString())
            Picasso.get().load(selectedPhotoUri).fit().centerCrop()
                .placeholder(R.drawable.person_icon_24).transform(CircleTransform()).into(binding.contactImageView)
            viewModel._imageUri.value = selectedPhotoUri.toString()
        }
        else if(resultCode == Activity.RESULT_CANCELED ){
            Picasso.get().load(Uri.parse(contact.imageUri)).fit().centerCrop()
                .placeholder(R.drawable.person_icon_24).transform(CircleTransform()).into(binding.contactImageView)
            viewModel._imageUri.value = contact.imageUri
        }
    }

    private fun createImageFile(): File {
    println("test")
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(activity!!.packageManager).also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: Exception) {
                    Log.i(TAG, ex.toString())
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.example.android.provider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CAMERAREQUEST_RESULT)
                }
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            val ringtoneManager = getDefaultUri(TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder().setUsage(USAGE_NOTIFICATION_RINGTONE)
                .setContentType(CONTENT_TYPE_SONIFICATION).build()

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Time for call"
            notificationChannel.setSound(ringtoneManager, audioAttributes)

            val notificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun makePhoneCall(number: String){

        val phoneIntent = Intent(Intent.ACTION_CALL)
        phoneIntent.data = Uri.parse(
            "tel:$number"
        )
        if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), CALL_PHONE_REQUEST_CODE
            )
        } else {
            startActivity(phoneIntent)
        }
    }

    private fun navigateToContactsFragment(_state: Boolean) {
        if(_state) {
            findNavController().navigate(R.id.action_editContactFragment_to_pageContainerFragment)
            viewModel.endNavigateToContactsFragment()
        }
    }
}
