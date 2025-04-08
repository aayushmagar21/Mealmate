package com.example.testapp.utils

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.google.firebase.storage.StorageReference
import java.util.UUID

fun handleAddRecipe(
    onAddItemClick: (item: HashMap<String, Any>) -> Unit,
    context: Context,
    item: HashMap<String, Any?>,
    storageRef: StorageReference,
    onComplete: () -> Unit
) {
    val name = item["name"] as? String
    val description = item["description"] as? String
    val preparationTime = item["preparationTime"] as? String
    val picture = item["picture"] as? Uri
    val ingredients = item["ingredients"] as? String
    val recipe = item["recipe"] as? String

    val isValidationError = validateFields(name, description,recipe, preparationTime, ingredients, picture)

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

fun validateFields(
    itemName: String?,
    itemDescription: String?,
    recipe: String?,
    preparationTime: String?,
    ingredients: String?,
    picture: Uri?
): String? {
    return when {
        picture == null -> {
            "Item picture is required"
        }

        itemName.isNullOrBlank() || ingredients.isNullOrBlank() || itemDescription.isNullOrBlank() || recipe.isNullOrBlank() -> {
            "The required fields cannot be empty"
        }

        preparationTime.isNullOrBlank() ->{
            "Preparation time cannot be empty"
        }
        else -> {
            if (preparationTime.toFloatOrNull() == null) {
                "Preparation time has to be a valid value"
            }else{
                if (preparationTime.toFloat() <= 0) {
                    "Preparation time cannot be $preparationTime"
                }else {
                    null
                }
            }
        }
    }
}
