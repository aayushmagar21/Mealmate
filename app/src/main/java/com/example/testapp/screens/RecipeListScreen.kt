package com.example.testapp.screens

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.testapp.LocalGoogleAuthUiClient
import com.example.testapp.LocalNavController
import com.example.testapp.MealMateScreen
import com.example.testapp.R
import com.example.testapp.components.ItemsGrid
import com.example.testapp.ui.theme.BorderPrimaryColor
import com.example.testapp.ui.theme.LightBgColor
import com.example.testapp.ui.theme.PrimaryColor
import com.example.testapp.ui.theme.TextColor1
import com.example.testapp.utils.getRecipeItemsFromDb
import com.google.firebase.firestore.FirebaseFirestore

@Preview(showBackground = true, widthDp = 370, heightDp = 700)
@Composable
fun RecipeListScreen(
    onBackBtnClick: () -> Unit = {},
){
    val db = FirebaseFirestore.getInstance();
    val googleAuthUiClient = LocalGoogleAuthUiClient.current;
    val navController = LocalNavController.current;

    val listOfItems = remember { mutableStateListOf<Map<String, Any>>() }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        isLoading = true
        val user = googleAuthUiClient.getSignedInUser();

        if (user?.email != null) {
            getRecipeItemsFromDb(db, user.email) { items ->
                Log.d(TAG, "Items fetched $items")
                listOfItems.clear()
                listOfItems.addAll(items)
                isLoading = false
            }
        }
    }

        Column(
            modifier = Modifier
                .padding(vertical = 14.dp, horizontal = 14.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
                /*
                Topbar =============================
                (Back button and title)
            */
            Box(){
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
                                Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = "back",
                                modifier = Modifier.size(23.dp)
                            )
                        }
                    }

                    Text(
                        "Recipe List",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    OutlinedButton(
                        onClick = {
                            navController.navigate(MealMateScreen.AddRecipe.name) },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = LightBgColor
                        ),
                        contentPadding = PaddingValues(0.dp),
                        border = BorderStroke(1.dp, BorderPrimaryColor),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .width(80.dp)
                            .height(34.dp)
                            .align(Alignment.CenterEnd)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.plus_icon),
                                tint = PrimaryColor,
                                modifier = Modifier.size(15.dp),
                                contentDescription = null
                            )

                            Text(
                                text = "Add",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Normal,
                            )
                        }
                    }
                }

                /*
                List of cards (Scrollable)
                # ItemCard: Composable
                ================================
                */
                if (isLoading) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryColor, modifier = Modifier.size(42.dp))
                    }
                } else {
                    if (listOfItems.isEmpty()) {
                        Text(
                            "No items available",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextColor1
                        )
                    } else {
                        ItemsGrid(listOfItems, isRecipe = true)
                    }
            }
        }

}