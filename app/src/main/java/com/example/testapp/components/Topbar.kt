package com.example.testapp.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.testapp.LocalGoogleAuthUiClient
import com.example.testapp.MealMateScreen
import com.example.testapp.LocalNavController
import com.example.testapp.R
import com.example.testapp.auth.FirebaseAuthClient
import com.example.testapp.ui.theme.BorderPrimaryColor
import com.example.testapp.ui.theme.GradientColor1
import com.example.testapp.ui.theme.GradientColor2
import com.example.testapp.ui.theme.LightBgColor
import com.example.testapp.ui.theme.PrimaryColor
import com.example.testapp.ui.theme.YellowColor
import kotlinx.coroutines.launch

@Preview(showBackground = true, widthDp = 370, heightDp = 700)
@Composable
public fun Topbar(modifier: Modifier = Modifier){
    val googleAuthClient = LocalGoogleAuthUiClient.current;
    val userData = googleAuthClient.getSignedInUser()
    val coroutineScope = rememberCoroutineScope();
    val navController = LocalNavController.current;

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {


            ProfileDropdown(profilePictureUrl = userData?.profilePictureUrl){
                coroutineScope.launch {
                    googleAuthClient.signOut();
                    navController.navigate(MealMateScreen.Login.name)
                }
            }
            
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "Hello",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if(userData?.username != null && userData.username != "") userData.username else "User 1",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Normal,
                )
            }
        }


        Button(
                onClick = {
                    navController.navigate(MealMateScreen.RecipeDetails.name)
                    },
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = YellowColor,
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .width(110.dp)
                    .height(36.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.recipe),
                        tint = Color.White,
                        modifier = Modifier.size(16.dp),
                        contentDescription = null
                    )

                    Text(
                        text = "Recipes",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Normal,
                        color = Color.White
                    )
            }
        }
    }
}