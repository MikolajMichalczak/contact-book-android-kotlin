package com.example.contactbook.screens.editcontact

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import androidx.work.*
import com.example.contactbook.database.ContactsRepository
import com.example.contactbook.database.ContactsRoomDatabase
import com.example.contactbook.database.entities.CallReminder
import com.example.contactbook.database.entities.Contact
import com.example.contactbook.database.entities.ContactExtras
import com.example.contactbook.database.entities.combined.ContactAndCallReminder
import com.example.contactbook.network.RepoApi
import com.example.contactbook.workers.NotifyWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit



class EditContactViewModel(application: Application, _contact: Contact) : AndroidViewModel(application) {

    companion object{
        private const val TAG = "EditContactViewModel"
        private const val ID = "worker_id_data"
        private const val NAME = "worker_name_data"
        private const val WORKER_REQUEST_TAG = "worker_notify_request"
    }

    private val app = application
    val workManager = WorkManager.getInstance(app)
    private val repository: ContactsRepository
    private val calendar = Calendar.getInstance()

    var contact = _contact
    var nameText:String = ""
    var numberText:String = ""

    private var _toContactsFragment = MutableLiveData<Boolean>()
    val toContactsFragment: LiveData<Boolean>
    get() = _toContactsFragment

    private var _invalidDate = MutableLiveData<Boolean>(false)
    val invalidDate: LiveData<Boolean>
        get() = _invalidDate

    var _imageUri = MutableLiveData<String>()
    val imageUri: LiveData<String>
        get() = _imageUri

    var callReminderAndContactData: LiveData<ContactAndCallReminder>
    val outputWorkInfos: LiveData<List<WorkInfo>>

    init {
        val database = ContactsRoomDatabase.getDatabase(app, viewModelScope)
        val service = RepoApi.retrofitService
        repository = ContactsRepository(database ,service)
        if(contact.name.isNotEmpty()) {
            nameText = _contact.name
            numberText = contact.number
            _imageUri.value = contact.imageUri
        }
        else{
            _imageUri.value = ""
        }
        callReminderAndContactData = repository.getContactAndCallReminderById(contact.contactId)
        outputWorkInfos = workManager.getWorkInfosByTagLiveData(WORKER_REQUEST_TAG)
    }

    fun updateContact(){
        Log.i(TAG, contact.toString())
        if(contact.name.isNotEmpty()) {
            if(nameText.isNotEmpty()&&numberText.isNotEmpty()) {
                contact.name = nameText
                contact.number = numberText
                contact.imageUri = _imageUri.value.toString()
                update(contact)
                toContactsFragment()
            }
            else
                toContactsFragment()
        }
        else{
            if(nameText.isNotEmpty()&&numberText.isNotEmpty()) {
                val newContact =
                    Contact(
                        0,
                        nameText,
                        numberText,
                        imageUri.value.toString(),
                        0
                    )
                insert(newContact)
                toContactsFragment()
            }
            else
                toContactsFragment()
        }
    }
    private fun update(contact: Contact)= viewModelScope.launch(Dispatchers.IO) {
        repository.updateContact(contact)
    }

    private fun insert(contact: Contact) = viewModelScope.launch(Dispatchers.IO) {
        val contactID =  repository.insertContact(contact)
        repository.insertExtras(ContactExtras(0, "","", contactID))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveReminder(dayTime: LocalDateTime){
        val zoneId = ZoneId.of("Europe/Warsaw")
        val pickerTime = dayTime.atZone(zoneId).toEpochSecond()*1000 //to millis
        val delay = pickerTime - calendar.timeInMillis
        val minutes = TimeUnit.MILLISECONDS.toMinutes(delay)
        Log.i(TAG, minutes.toString())
        if(minutes>0) {

            val newReminder = CallReminder(0, dayTime, contact.contactId)
            CoroutineScope(Dispatchers.IO).launch {
                repository.insertReminder(newReminder)
            }

            startNotifyWorker(minutes, contact.contactId, contact.name)
        }
        else
            _invalidDate.value = true
    }

    private fun startNotifyWorker(minutes: Long, contactId: Int, contactName: String){
        val data = Data.Builder()
        data.putInt(ID, contactId)
        data.putString(NAME, contactName)

        val notificationWorkRequest = OneTimeWorkRequest.Builder(NotifyWorker::class.java)
            .setInitialDelay(minutes, TimeUnit.MILLISECONDS)
            .addTag(contact.contactId.toString())
            .setInputData(data.build())
            .build()
            workManager.enqueueUniqueWork(contact.contactId.toString(), ExistingWorkPolicy.APPEND, notificationWorkRequest)
    }

    fun deleteReminder(){
        workManager.cancelAllWorkByTag(contact.contactId.toString())
        CoroutineScope(Dispatchers.IO).launch {
            repository.deleteReminderByContactId(contact.contactId)
        }
    }

    private fun toContactsFragment(){
        _toContactsFragment.value = true
    }

    fun endNavigateToContactsFragment(){
        _toContactsFragment.value = false
    }

    fun changeInvalidDateVariableState(){
        _invalidDate.value = false
    }

}

