package com.example.testapp.components

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.testapp.LocalGoogleAuthUiClient
import com.example.testapp.MealMateScreen
import com.example.testapp.LocalNavController
import com.example.testapp.ui.theme.LightPrimaryColor
import com.example.testapp.ui.theme.TextColor1
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Preview(showBackground = true, widthDp = 200, heightDp = 260)
@Composable
fun RecipeItemCard(modifier: Modifier = Modifier, item: Map<String, Any>? = null) {
    val navController = LocalNavController.current;

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        onClick = {
            val route = "${MealMateScreen.RecipeDetails.name}/${item?.get("id")}"
            navController.navigate(route)
        }
    ) {
        Column {
            ImageCardSection(item)

            Column(
                modifier = Modifier.padding(top = 6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item?.get("name") as? String ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.2.sp
                    )

                    ViewRating()
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = null,
                        tint = TextColor1,
                        modifier = Modifier.size(16.dp)
                    )

                    (item?.get("preparationTime") as? String)?.let {
                        Text(
                            text = "$it mins",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextColor1
                            )
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageCardSection(item: Map<String, Any>?, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val pictureUri = item?.get("picture") as? String ?: ""

    Box(
        modifier = modifier
            .height(160.dp),
    ) {
        Surface(shape = RoundedCornerShape(10.dp), color = LightPrimaryColor) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(pictureUri)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Fit
            )

        }
    }
}

