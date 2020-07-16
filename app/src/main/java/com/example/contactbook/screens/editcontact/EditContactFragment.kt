package com.example.contactbook.screens.contacts

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.contactbook.R
import com.example.contactbook.database.entities.Contact
import com.example.contactbook.databinding.FragmentEditContactBinding
import com.example.contactbook.screens.editcontact.EditContactViewModel
import com.example.contactbook.screens.editcontact.EditContactViewModelFactory
import com.squareup.picasso.Picasso
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class EditContactFragment : Fragment() {

    companion object{
        private const val TAG = "EditContactFragment"
        private const val STORAGEREQUEST_RESULT = 1
        private const val CAMERAREQUEST_RESULT = 0
        private const val CAMERA_REQUEST_CODE = 101
    }

    private lateinit var viewModel: EditContactViewModel
    private lateinit var binding: FragmentEditContactBinding
    private lateinit var viewModelFactory: EditContactViewModelFactory
    private lateinit var contact: Contact


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_edit_contact, container, false)

            setupPermissions()

        contact = arguments?.getParcelable("contact")!!

        viewModelFactory =
            EditContactViewModelFactory(
                requireActivity().application,
                contact
            )

        viewModel = ViewModelProvider(this, viewModelFactory).get(EditContactViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.editTextName.setText(contact?.name)
        binding.editTextNumber.setText((contact?.number))


        viewModel.toContactsFragment.observe(viewLifecycleOwner, Observer {state ->
            navigateToContactsFragment(state)
        })

        viewModel.imageUri.observe(viewLifecycleOwner, Observer {uri ->
            if(uri.isBlank())
                binding.contactImageView.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.person_icon_24))
            else
                Picasso.get().load(uri).placeholder(R.drawable.person_icon_24).error(R.drawable.person_icon_24).into(binding.contactImageView)
        })

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Where to choose a photo?")
        builder.setPositiveButton("Camera") { dialog, which ->
            dispatchTakePictureIntent()
        }

        builder.setNegativeButton("Choose file") { dialog, which ->
            val takePictureIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            takePictureIntent.type = "image/*"
            startActivityForResult(
                takePictureIntent,
                1
            )
        }

        binding.contactImageView.setOnClickListener(){
            builder.show()
        }

        return binding.root
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied")
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission has been denied by user")
                } else {
                    Log.i(TAG, "Permission has been granted by user")
                }
            }
        }
    }


    var selectedPhotoUri: Uri? = null
    private var currentPhotoPath: String = ""

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERAREQUEST_RESULT) {
            Log.i(TAG, currentPhotoPath)
            selectedPhotoUri = Uri.fromFile(File(currentPhotoPath))
            Picasso.get().load(selectedPhotoUri).into(binding.contactImageView)
            viewModel._imageUri.value = selectedPhotoUri.toString()
        }

        if(requestCode == STORAGEREQUEST_RESULT && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            //Log.i(TAG, selectedPhotoUri.toString())
            Picasso.get().load(selectedPhotoUri).fit().centerCrop()
                .placeholder(R.drawable.person_icon_24).into(binding.contactImageView)
            viewModel._imageUri.value = selectedPhotoUri.toString()
        }
        else if(resultCode == Activity.RESULT_CANCELED ){
            Picasso.get().load(Uri.parse(contact.imageUri)).fit().centerCrop()
                .placeholder(R.drawable.person_icon_24).into(binding.contactImageView)
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
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                createImageFile()
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: Exception) {
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
                    startActivityForResult(takePictureIntent, 0)
                }
            }
        }
    }

    private fun navigateToContactsFragment(_state: Boolean) {
        if(_state) {
            findNavController().navigate(R.id.action_editContactFragment_to_pageContainerFragment)
            viewModel.endNavigateToContactsFragment()
        }
    }
}
