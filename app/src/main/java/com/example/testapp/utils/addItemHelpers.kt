package com.example.testapp.utils

import android.content.Context
import android.net.Uri
import android.telephony.SmsManager
import android.widget.Toast
import com.google.firebase.storage.StorageReference
import java.util.UUID

fun handleAddItem(
    onAddItemClick: (item: HashMap<String, Any>) -> Unit,
    context: Context,
    item: HashMap<String, Any?>,
    storageRef: StorageReference,
    onComplete: () -> Unit
) {
    val name = item["name"] as? String
    val category = item["category"] as? String
    val description = item["description"] as? String
    val price = item["price"] as? String
    val picture = item["picture"] as? Uri
    val supporter = item["supporter"] as? Map<*, *>

    val isValidationError = validateItemFields(name, category, description, price, picture)

    if (isValidationError == null) {
        if (picture != null) {
            val uniqueImageName = UUID.randomUUID()
            val uploadImageTask = storageRef.child("$uniqueImageName").putFile(picture)

            uploadImageTask.addOnSuccessListener {
                storageRef.child("$uniqueImageName").downloadUrl.addOnSuccessListener { url ->
                    item["picture"] = url.toString()
                    item["pictureRef"] = uniqueImageName.toString()
                    item["id"] = UUID.randomUUID().toString()

                    @Suppress("UNCHECKED_CAST")
                    onAddItemClick(item as HashMap<String, Any>)
                    // Send SMS after successful upload
                    sendSmsToSupporter(supporter, name, context)

                    onComplete()
                }
            }.addOnFailureListener {
                onComplete()
                Toast.makeText(
                    context,
                    "Failed to upload image.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    } else {
        onComplete()
        Toast.makeText(
            context,
            isValidationError,
            Toast.LENGTH_LONG
        ).show()
    }
}

fun validateItemFields(
    itemName: String?,
    itemCategory: String?,
    itemDescription: String?,
    itemPrice: String?,
    picture: Uri?
): String? {
    return when {
        picture == null -> {
            "Item picture is required"
        }

        itemName.isNullOrBlank() || itemCategory.isNullOrBlank() || itemDescription.isNullOrBlank() -> {
            "The required fields cannot be empty"
        }

        itemPrice.isNullOrBlank() ->{
            "Price cannot be empty"
        }
        else -> {
            if (itemPrice.toFloatOrNull() == null) {
                "Price has to be a valid value"
            }else{
                if (itemPrice.toFloat() <= 0) {
                    "Price cannot be $itemPrice"
                }else {
                    null
                }
            }
        }
    }
}

private fun sendSmsToSupporter(supporter: Map<*, *>?, itemName: String?, context: Context) {
    supporter?.let {
        val contactNumber = it["contact"] as? String
        val supporterName = it["name"] as? String

        if (!contactNumber.isNullOrEmpty()) {
            try {
                val smsManager = SmsManager.getDefault()
                val message = "Hello $supporterName," +
                        "Help me purchase the item '$itemName'"

                smsManager.sendTextMessage(
                    contactNumber,
                    null,
                    message,
                    null,
                    null
                )

                Toast.makeText(
                    context,
                    "SMS notification sent to supporter",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Failed to send SMS: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}