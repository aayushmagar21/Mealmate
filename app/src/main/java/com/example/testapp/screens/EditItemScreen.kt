package com.example.testapp.screens

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.testapp.LocalGoogleAuthUiClient
import com.example.testapp.components.CustomTextField
import com.example.testapp.components.LocationField
import com.example.testapp.components.PhotoPicker
import com.example.testapp.ui.theme.BorderPrimaryColor
import com.example.testapp.ui.theme.DisabledPrimaryColor
import com.example.testapp.ui.theme.LightBgColor
import com.example.testapp.ui.theme.TextColor3
import com.example.testapp.utils.getPlaceNameFromCoordinates
import com.example.testapp.utils.getSingleItemFromDb
import com.example.testapp.utils.validateItemFields
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

@Preview(showBackground = true, widthDp = 370, heightDp = 700)
@Composable
fun EditItemScreen(
    itemId: String? = null,
    onBackBtnClick: () -> Unit = {},
    onUpdateItemClick: (item: HashMap<String, Any>) -> Unit = {}
) {
    val context = LocalContext.current;

    var updatingItem by remember { mutableStateOf(false) }
    val storage = FirebaseStorage.getInstance();
    val storageRef = storage.reference;

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    var itemName by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("0") }
    var itemDescription by remember { mutableStateOf("") }
    var itemCategory by remember { mutableStateOf("") }
    var pictureRef by remember { mutableStateOf("") }

    var supporterName by remember { mutableStateOf("") }
    var supporterContact by remember { mutableStateOf("") }

    val db = FirebaseFirestore.getInstance();
    val googleAuthUiClient = LocalGoogleAuthUiClient.current;

    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var placeName by remember { mutableStateOf("Select location on the map") }

    LaunchedEffect(key1 = Unit) {
        val user = googleAuthUiClient.getSignedInUser();

        if (user?.email != null && itemId != null) {
            getSingleItemFromDb(db, user.email, itemId) { item ->
                Log.d(ContentValues.TAG, "Items fetched $item")

                imageUri = Uri.parse(item["picture"] as? String)
                pictureRef = item["pictureRef"] as? String ?: ""
                itemName = item["name"] as? String ?: ""
                itemPrice = item["price"] as? String ?: ""
                itemDescription = item["description"] as? String ?: ""
                itemCategory = item["category"] as? String ?: ""

                supporterName = (item["supporter"] as? Map<*, *>)?.get("name") as? String ?: ""
                supporterContact =
                    (item["supporter"] as? Map<*, *>)?.get("contact") as? String ?: ""

                selectedLocation = LatLng(
                    (item["coordinates"] as? Map<*, *>)?.get("latitude") as? Double ?: 0.0,
                    (item["coordinates"] as? Map<*, *>)?.get("longitude") as? Double ?: 0.0
                )
                placeName = item["placeName"] as? String ?: ""
            }
        }
    }

    LaunchedEffect(selectedLocation) {
        selectedLocation?.let { latLng ->
            placeName = getPlaceNameFromCoordinates(latLng)  // Function to fetch place name
        }
    }

    Column(
        modifier = Modifier
            .padding(vertical = 14.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {

        Column(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            /*
                Topbar =============================
                (Back button and title)
            */
            Box() {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = onBackBtnClick,
                        contentPadding = PaddingValues(0.dp),
                        border = BorderStroke(1.dp, BorderPrimaryColor),
                        modifier = Modifier.width(40.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = LightBgColor
                        )
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "back",
                            modifier = Modifier.size(23.dp)
                        )
                    }
                }

                Text(
                    "Edit item",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            }


            /*
                Photo Picker ==============================
                ==========================================
            */
            PhotoPicker(imageUri = imageUri) { uri ->
                imageUri = uri
            }

            /*
            Item name, price, category fields ==================
            ====================================================
            */
            CustomTextField(
                value = itemName,
                onChange = { it -> itemName = it },
                label = "Item Name",
                placeholder = "Lettuce",
                modifier = Modifier.fillMaxWidth()
            )

            CustomTextField(
                value = itemPrice.toString(),
                onChange = { it -> itemPrice = it },
                label = "Price",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = {
                    Icon(
                        Icons.Default.AttachMoney, contentDescription = null,
                        tint = TextColor3, modifier = Modifier.size(22.dp)
                    )
                },
            )

            CustomTextField(
                value = itemCategory,
                onChange = { it -> itemCategory = it },
                label = "Category",
            )

            CustomTextField(
                value = itemDescription,
                onChange = { it -> itemDescription = it },
                label = "Description",
                minHeight = 110.dp,
                singleLine = false,
                maxLines = 4
            )

            /*
             Google map box =========================================
             =======================================================
            */
            LocationField(selectedLocation = selectedLocation, placeName = placeName) { latlng ->
                selectedLocation = latlng
            }

        }

        // Full width line
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 6.dp)
        ) {
            // Fetching width and height for
            // setting start x and end y
            val canvasWidth = size.width
            val canvasHeight = size.height

            // drawing a line between start(x,y) and end(x,y)
            drawLine(
                start = Offset(x = canvasWidth, y = 0f),
                end = Offset(x = 0f, y = canvasHeight),
                color = BorderPrimaryColor,
                strokeWidth = 3F
            )
        }


        /*
            Supporter form elements ==========================
            Name, contact ====================================
         */

        Column(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(text = "Supporter", style = MaterialTheme.typography.bodyLarge)

            CustomTextField(
                value = supporterName, onChange = { it -> supporterName = it },
                label = "Full Name",
                placeholder = "xyz",
                modifier = Modifier.fillMaxWidth(),
                required = false
            )

            CustomTextField(
                value = supporterContact, onChange = { it -> supporterContact = it },
                label = "Contact",
                placeholder = "98000000",
                modifier = Modifier.fillMaxWidth(),
                required = false,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Button(
                onClick = { ->
                    updatingItem = true
                    val itemDetails = hashMapOf(
                        "id" to itemId,
                        "name" to itemName,
                        "price" to itemPrice,
                        "category" to itemCategory,
                        "description" to itemDescription,
                        "coordinates" to selectedLocation,
                        "placeName" to placeName,
                        "picture" to imageUri,
                        "pictureRef" to pictureRef,
                        "supporter" to mapOf(
                            "name" to supporterName,
                            "contact" to supporterContact
                        )
                    )

                    handleUpdateItem(
                        onUpdateItemClick,
                        context,
                        itemDetails,
                        storageRef
                    ) {
                        updatingItem = false;
                    }
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(7.dp),
                enabled = !updatingItem,
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = DisabledPrimaryColor
                )
            ) {
                if (updatingItem) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(32.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        "Update Item",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp,
                        color = Color.White
                    )
                }
            }
        }


    }
}

fun handleUpdateItem(
    onAddItemClick: (item: HashMap<String, Any>) -> Unit,
    context: Context,
    item: HashMap<String, Any?>,
    storageRef: StorageReference,
    onComplete: () -> Unit
) {
    val isValidationError =
        validateItemFields(
            item["name"] as? String ?: "",
            item["category"] as? String ?: "",
            item["description"] as? String ?: "",
            item["price"] as? String ?: "",
            item["picture"] as? Uri
        );

    if (isValidationError == null) {
        Log.d(TAG, "Item update details ::: $item");

        val isFirebaseUrl =
            item["picture"].toString().startsWith("https://firebasestorage.googleapis.com")

        if (isFirebaseUrl) {
            @Suppress("UNCHECKED_CAST")
            onAddItemClick(item as HashMap<String, Any>)

            onComplete()
        } else {
            val uniqueImageName = item["pictureRef"] as? String;
            val uploadImageTask =
                storageRef.child("$uniqueImageName").putFile(item["picture"] as Uri);

            uploadImageTask.addOnSuccessListener {
                storageRef.child("$uniqueImageName").downloadUrl.addOnSuccessListener { url ->

                    item["picture"] = url.toString();
                    @Suppress("UNCHECKED_CAST")
                    onAddItemClick(item as HashMap<String, Any>)

                    onComplete();
                }
            }.addOnFailureListener {
                onComplete();
                Toast.makeText(
                    context,
                    "Failed to upload image.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    } else {
        onComplete();
        Toast.makeText(
            context,
            isValidationError,
            Toast.LENGTH_LONG
        ).show()
    }
}