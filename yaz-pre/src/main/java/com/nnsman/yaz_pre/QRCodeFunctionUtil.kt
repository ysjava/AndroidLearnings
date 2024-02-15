package com.nnsman.yaz_pre

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.telephony.SmsManager
import android.widget.Toast


fun Activity.callPhone(phoneNumber: String) {
    val data = Uri.parse("tel:$phoneNumber")
    call(Intent.ACTION_DIAL, data)
}

fun Activity.accessWebpage(url: String) {
    val data = Uri.parse(url)
    call(Intent.ACTION_VIEW, data)
}

fun Activity.connectWifi(ssid: String, password: String, wifiCapability: WifiUtil.WifiCapability) {
    WifiUtil.connectWifi(this, ssid, password, wifiCapability)
}

fun Activity.sendEmail(subject: String, content: String, address: Array<String>) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "message/rfc822"
    intent.putExtra(Intent.EXTRA_SUBJECT, subject)
    intent.putExtra(Intent.EXTRA_EMAIL, address)
    intent.putExtra(Intent.EXTRA_TEXT, content)
    val chooseIntent = Intent.createChooser(intent, "")
    startActivity(chooseIntent)
}


data class Contacts(
    val name: String, val phone: String,
    val address: String, val email: String, val company: String,
    val website: String, val remark: String
)

fun Activity.addContacts(contacts: Contacts) {
    val data = ArrayList<ContentValues>()
    val uri = Uri.withAppendedPath(Uri.parse("content://com.android.contacts"), "contacts")
    val intent = Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI)

    intent.setDataAndType(uri, "vnd.android.cursor.dir/person")
    val website = ContentValues()
    website.put(
        ContactsContract.Data.MIMETYPE,
        ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE
    )
    website.put(ContactsContract.CommonDataKinds.Website.URL, contacts.website);
    data.add(website)
    val address = ContentValues()
    address.put(
        ContactsContract.Data.MIMETYPE,
        ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE
    )
    address.put(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, contacts.address)
    address.put(
        ContactsContract.CommonDataKinds.StructuredPostal.TYPE,
        ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME
    )

    data.add(address)
    intent.putExtra(ContactsContract.Intents.Insert.NAME, contacts.name)
    intent.putExtra(ContactsContract.Intents.Insert.COMPANY, contacts.company)
    intent.putExtra(ContactsContract.Intents.Insert.EMAIL, contacts.email)
    intent.putExtra(ContactsContract.Intents.Insert.PHONE, contacts.phone)
    intent.putExtra(ContactsContract.Intents.Insert.NOTES, contacts.remark)
    intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data)

    startActivity(intent)
}

fun Activity.sendSms(phone: String, text: String) {
    val smsManager = getSystemService(SmsManager::class.java)
    smsManager.sendTextMessage(phone, null, text, null, null)
}

fun Activity.viewMap(lat: String, lon: String) {
    val gmmIntentUri = Uri.parse("geo:$lat,$lon")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    mapIntent.setPackage("com.google.android.apps.maps")

    val apps = mapIntent.resolveActivity(packageManager)
    if (apps == null) {
        Toast.makeText(this, "No application can handle the request.", Toast.LENGTH_SHORT).show()
        return
    }
    startActivity(mapIntent)
}

private fun Activity.call(action: String, data: Uri) {
    val intent = Intent(action)
    intent.data = data
    startActivity(intent)
}