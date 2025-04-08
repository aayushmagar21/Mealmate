package com.example.testapp.screens

import android.content.ContentValues
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.testapp.LocalGoogleAuthUiClient
import com.example.testapp.LocalNavController
import com.example.testapp.ui.theme.BorderPrimaryColor
import com.example.testapp.ui.theme.LightGrayColor
import com.example.testapp.ui.theme.TextColor1
import com.example.testapp.ui.theme.TextColor2
import com.example.testapp.utils.getSingleRecipeFromDb
import com.google.firebase.firestore.FirebaseFirestore

@Preview(showBackground = true, widthDp = 370, heightDp = 700)
@Composable
fun RecipeDetailScreen(
    onBackBtnClick: () -> Unit = {},
    itemId: String? = null
) {
    var itemDetails by remember { mutableStateOf<Map<String, Any>?>(null) }
    val db = FirebaseFirestore.getInstance();
    val googleAuthUiClient = LocalGoogleAuthUiClient.current;

    LaunchedEffect(key1 = Unit) {
        val user = googleAuthUiClient.getSignedInUser();

        if (user?.email != null && itemId != null) {
            getSingleRecipeFromDb(db, user.email, itemId) { item ->
                Log.d(ContentValues.TAG, "Items fetched $item")

                itemDetails = item;
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(22.dp),
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 8.dp)
            .fillMaxSize()
    ) {
        Surface(
            color = LightGrayColor,
            shape = RoundedCornerShape(bottomStart = 42.dp, bottomEnd = 42.dp),
            modifier = Modifier
                .wrapContentHeight(),
        ) {
            Column {
                Box(modifier = Modifier.padding(14.dp)){
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
                                containerColor = Color.White
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
                        "Recipe Details",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )

                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 28.dp, top = 8.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(itemDetails?.get("picture")),
                        contentDescription = null,
                        modifier = Modifier.size(180.dp)
                    )
                }
            }

        }

        /*
            Basic item information ================================
            =======================================================
         */
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = itemDetails?.get("name") as? String ?: "",
                    style = MaterialTheme.typography.titleMedium
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = null,
                        tint = TextColor1,
                        modifier = Modifier.size(20.dp)
                    )

                    (itemDetails?.get("preparationTime") as? String)?.let {
                        Text(
                            text = "$it mins",
                            style = MaterialTheme.typography.bodyMedium,

                            )
                    }
                }
            }

            Text(
                itemDetails?.get("description") as? String ?: "",
                style = MaterialTheme.typography.labelMedium,
                color = TextColor2,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp,
                modifier = Modifier
                    .fillMaxWidth()
            )


            /*
            Ingredients information ===========================
            =================================================
             */
            Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 6.dp)) {
                Text(
                    text = "Ingredients",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                (itemDetails?.get("ingredients") as? String)?.let {
                    Text(text = it, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium, color = TextColor2)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 6.dp)) {
                Text(
                    text = "Recipe",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                (itemDetails?.get("recipe") as? String)?.let {
                    Text(text = it, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium, color = TextColor2)
                }
            }
        }
        }

}