package com.example.testapp.screens

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.testapp.components.CustomTextField
import com.example.testapp.components.PhotoPicker
import com.example.testapp.ui.theme.BorderPrimaryColor
import com.example.testapp.ui.theme.DisabledPrimaryColor
import com.example.testapp.ui.theme.LightBgColor
import com.example.testapp.ui.theme.TextColor3
import com.example.testapp.utils.handleAddItem
import com.example.testapp.utils.handleAddRecipe
import com.google.firebase.storage.FirebaseStorage

@Preview(showBackground = true, widthDp = 370, heightDp = 700)
@Composable
fun AddRecipeItemScreen(onAddItemClick: (item: HashMap<String, Any>) -> Unit = {}, onBackBtnClick: () -> Unit = {}){
    val context = LocalContext.current;
    var recipeName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var preparationTime by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf("") }
    var recipe by remember { mutableStateOf("") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    var addingItem by remember { mutableStateOf(false) }
    val storage = FirebaseStorage.getInstance();
    val storageRef = storage.reference;

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
                    "Add Recipe",
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

            CustomTextField(
                value = recipeName,
                onChange = { it -> recipeName = it },
                label = "Recipe Name",
                placeholder = "eg. Pudding",
                modifier = Modifier.fillMaxWidth()
            )

            CustomTextField(
                value = description,
                onChange = { description = it },
                label = "Description",
                placeholder = "Brief description of the recipe",
                singleLine = false,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth(),
                minHeight = 100.dp,
            )

            CustomTextField(
                value = preparationTime,
                onChange = { preparationTime = it },
                label = "Preparation Time (minutes)",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = null,
                        tint = TextColor3,
                        modifier = Modifier.size(22.dp)
                    )
                },
            )

            CustomTextField(
                value = ingredients,
                onChange = { ingredients = it },
                label = "Ingredients",
                placeholder = "eg. rice, ajwain, milk, etc.",
                singleLine = false,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth(),
                minHeight = 100.dp
            )

            CustomTextField(
                value = recipe,
                onChange = { recipe = it },
                label = "Recipe",
                placeholder = "Enter recipe, one per line",
                singleLine = false,
                maxLines = 16,
                modifier = Modifier.fillMaxWidth(),
                minHeight = 300.dp
            )

            Button(
                onClick = { ->
                    addingItem = true
                    val itemDetails: HashMap<String, Any?> = hashMapOf(
                        "name" to recipeName,
                        "description" to description,
                        "picture" to imageUri,
                        "preparationTime" to preparationTime,
                        "ingredients" to ingredients,
                        "recipe" to recipe
                    )

                    handleAddRecipe(
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
                        "Add Recipe",
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