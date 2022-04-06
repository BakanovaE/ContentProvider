package org.martellina.contentprovider

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 123

    private lateinit var recyclerView: RecyclerView
    var list = arrayListOf<Contact>()
    private val adapter = MyAdapter(list)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val permissionStatus =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            list = getAll(this)!!
            Log.d("TAG", "${list.size}")
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_CONTACTS),
                PERMISSION_REQUEST_CODE
            )
        }

        recyclerView = findViewById(R.id.recyclerview)

        initRecyclerView()

        adapter.updateList(list)
    }

    private val CONTACT_ID = ContactsContract.Contacts._ID;
    private val DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
    private val HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    private val PHONE_NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
    private val PHONE_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;

    @SuppressLint("Range")
    fun getAll(context: Context): ArrayList<Contact>? {
        val contentResolver: ContentResolver = context.contentResolver
        val arrayPhones = arrayOf(PHONE_NUMBER, PHONE_CONTACT_ID)

        val phoneCursor: Cursor? = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, arrayPhones, null,null,null)
        if(phoneCursor != null){
            if(phoneCursor.count > 0) {
                val phonesMap = hashMapOf<Int, ArrayList<String>>()
                while (phoneCursor.moveToNext()) {
                    val contactId: Int = phoneCursor.getInt(phoneCursor.getColumnIndex(PHONE_CONTACT_ID));
                    var phones = ArrayList<String>()
                    if (phonesMap.containsKey(contactId)) {
                        phones = phonesMap[contactId]!!
                    }
                    phones.add(phoneCursor.getString(0))
                    phonesMap[contactId] = phones
                }
                val array = arrayOf(CONTACT_ID, DISPLAY_NAME, HAS_PHONE_NUMBER)
                val cursor: Cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, array,
                    "$HAS_PHONE_NUMBER > 0", null, "$DISPLAY_NAME ASC")!!
                if (cursor.count > 0) {
                    val contacts = ArrayList<Contact>()
                    while (cursor.moveToNext()) {
                        val id = cursor.getInt(cursor.getColumnIndex(CONTACT_ID))
                        if(phonesMap.containsKey(id)) {
                            val contact =
                                phonesMap[id]?.let { TextUtils.join(",", it.toArray()) }?.let {
                                    Contact(cursor.getString(cursor.getColumnIndex(DISPLAY_NAME)), it, id)
                                }
                            if (contact != null) {
                                contacts.add(contact)
                            }
                        }
                    }
                    return contacts
                }
                cursor.close()
            }
            phoneCursor.close()
        }
        return null
    }


    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            recyclerView.adapter = adapter
    }

}