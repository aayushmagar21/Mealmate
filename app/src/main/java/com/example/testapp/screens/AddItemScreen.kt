package com.example.testapp.screens

import android.net.Uri
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
import com.example.testapp.components.CustomTextField
import com.example.testapp.components.LocationField
import com.example.testapp.components.PhotoPicker
import com.example.testapp.ui.theme.BorderPrimaryColor
import com.example.testapp.ui.theme.DisabledPrimaryColor
import com.example.testapp.ui.theme.LightBgColor
import com.example.testapp.ui.theme.TextColor3
import com.example.testapp.utils.getPlaceNameFromCoordinates
import com.example.testapp.utils.handleAddItem
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.storage.FirebaseStorage

@Preview(showBackground = true, widthDp = 370, heightDp = 700)
@Composable
fun AddItemScreen(
    onBackBtnClick: () -> Unit = {},
    onAddItemClick: (item: HashMap<String, Any>) -> Unit = {}
) {
    val context = LocalContext.current;

    var addingItem by remember { mutableStateOf(false) }
    val storage = FirebaseStorage.getInstance();
    val storageRef = storage.reference;

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    var itemName by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("0") }
    var itemDescription by remember { mutableStateOf("") }
    var itemCategory by remember { mutableStateOf("") }

    var supporterName by remember { mutableStateOf("") }
    var supporterContact by remember { mutableStateOf("") }

    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var placeName by remember { mutableStateOf("Select location on the map") }

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
                    "Add new item",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            /*
                Photo Picker ==============================
                ==========================================
            */
            PhotoPicker(imageUri = imageUri) {uri ->
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
                keyboardOptions =  KeyboardOptions(keyboardType = KeyboardType.Number),
                label = "Price",
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
                Location field =========================================
                =======================================================
             */
            LocationField(placeName = placeName) { latlng ->
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
                placeholder = "XYZ",
                modifier = Modifier.fillMaxWidth(),
                required = false
            )

            CustomTextField(
                value = supporterContact, onChange = { it -> supporterContact = it },
                label = "Contact",
                placeholder = "9800000000",
                modifier = Modifier.fillMaxWidth(),
                required = false,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Button(
                onClick = { ->
                    addingItem = true
                    val itemDetails = hashMapOf(
                        "name" to itemName,
                        "price" to itemPrice,
                        "category" to itemCategory,
                        "description" to itemDescription,
                        "picture" to imageUri,
                        "coordinates" to selectedLocation,
                        "placeName" to placeName,
                        "supporter" to mapOf(
                            "name" to supporterName,
                            "contact" to supporterContact
                        )
                    )

                    handleAddItem(
                        onAddItemClick,
                        context,
                        itemDetails,
                        storageRef
                    ) {
                        addingItem = false;
                    }
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(7.dp),
                enabled = !addingItem,
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = DisabledPrimaryColor
                )
            ) {
                if (addingItem) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(32.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        "Add Item",
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

