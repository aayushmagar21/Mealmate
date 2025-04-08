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
fun ItemCard(modifier: Modifier = Modifier, item: Map<String, Any>? = null) {
    val navController = LocalNavController.current;

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        onClick = {
            val route = "${MealMateScreen.ItemDetails.name}/${item?.get("id")}"
            navController.navigate(route)
        }
    ) {
        Column {
            ImageCardSection(item)

            Column(
                modifier = Modifier.padding(top = 6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item?.get("name") as? String ?: "",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.2.sp
                    )

                    ViewRating()
                }

                Text(text = "$" + item?.get("price") as? String, style = MaterialTheme.typography.labelSmall, color = TextColor1)
            }
        }
    }
}

@Composable
private fun ImageCardSection(item: Map<String, Any>?, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val googleAuthUiClient = LocalGoogleAuthUiClient.current
    val user = googleAuthUiClient.getSignedInUser()

    val pictureUri = item?.get("picture") as? String ?: ""
    val initialPurchaseState = item?.get("mark_as_purchased") as? Boolean == true
    var isPurchased by remember { mutableStateOf(initialPurchaseState) }

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

            FavoriteButton(
                onClick = {
                    val itemId = item?.get("id")?.toString()
                    val userEmail = user?.email?.toString()

                    if (itemId != null && userEmail != null) {
                        // Toggle the purchase state
                        isPurchased = !isPurchased
                        // Update database with new state
                        updateItemPurchaseStatus(db, context, itemId, userEmail, isPurchased)
                    } else {
                        Toast.makeText(
                            context,
                            "Error: Missing item or user information",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                variants = FavButtonVariants.Outlined,
                isPurchased = isPurchased,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(55.dp)
                    .padding(10.dp)
            )
        }
    }
}

fun updateItemPurchaseStatus(
    db: FirebaseFirestore,
    context: Context,
    itemId: String,
    userEmail: String,
    isPurchased: Boolean
) {
    // Create map with the updated purchase status
    val updates = hashMapOf<String, Any>(
        "mark_as_purchased" to isPurchased
    )

    db.collection(userEmail).document(itemId)
        .update(updates)
        .addOnSuccessListener {
            val message = if (isPurchased)
                "Item marked as purchased."
            else
                "Purchase mark removed."
            Toast.makeText(
                context,
                message,
                Toast.LENGTH_LONG
            ).show()
        }
        .addOnFailureListener { e ->
            Log.w(ContentValues.TAG, "Error updating document", e)
            Toast.makeText(
                context,
                "Failed to update item: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
            // Revert the local state in case of failure
            // This would need to be called from the composable
        }
}