package com.example.testapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.testapp.auth.FirebaseAuthClient
import com.example.testapp.auth.SignInViewModel
import com.example.testapp.auth.SignUpViewModel
import com.example.testapp.screens.AddItemScreen
import com.example.testapp.screens.AddRecipeItemScreen
import com.example.testapp.screens.DashboardScreen
import com.example.testapp.screens.EditItemScreen
import com.example.testapp.screens.GetStartedScreenPreview
import com.example.testapp.screens.ItemDetailsScreen
import com.example.testapp.screens.LoginScreenPreview
import com.example.testapp.screens.RecipeDetailScreen
import com.example.testapp.screens.RecipeListScreen
import com.example.testapp.screens.SignupScreenPreview
import com.example.testapp.utils.addItemToDb
import com.example.testapp.utils.addRecipeToDb
import com.example.testapp.utils.updateItemOfDb
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

// Routes of the app (enums)
enum class MealMateScreen() {
    Login,
    Signup,
    Dashboard,
    GetStarted,
    AddItem,
    EditItem,
    ItemDetails,
    RecipeDetails,
    AddRecipe
}

val LocalNavController = compositionLocalOf<NavHostController> {
    error("No NavController provided")
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun RouteController(
    googleAuthUiClient: FirebaseAuthClient,
    lifecycleOwner: LifecycleOwner,
    context: Context,
    db: FirebaseFirestore
) {
    val navController = rememberNavController();

    CompositionLocalProvider(LocalNavController provides navController) {
        Scaffold() { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = MealMateScreen.GetStarted.name,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route = MealMateScreen.GetStarted.name) {
                    LaunchedEffect(key1 = Unit) {
                        if (googleAuthUiClient.getSignedInUser() != null) {
                            navController.navigate(MealMateScreen.Dashboard.name)
                        }
                    }

                    GetStartedScreenPreview(onGetStartedClicked = {
                        navController.navigate(
                            MealMateScreen.Login.name
                        )
                    })
                }

                composable(route = MealMateScreen.Login.name) {
                    val viewModel = viewModel<SignInViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()

                    LaunchedEffect(key1 = Unit) {
                        if (googleAuthUiClient.getSignedInUser() != null) {
                            navController.navigate(MealMateScreen.Dashboard.name)
                        }
                    }

                    val launcher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartIntentSenderForResult(),
                        onResult = { result ->
                            if (result.resultCode == Activity.RESULT_OK) {
                                lifecycleOwner.lifecycleScope.launch {
                                    val signInResult = googleAuthUiClient.signInWithIntent(
                                        intent = result.data ?: return@launch
                                    )
                                    viewModel.onSignInResult(signInResult)
                                }
                            }
                        }
                    )

                    LaunchedEffect(key1 = state.isSignInSuccessful) {
                        if (state.isSignInSuccessful) {
                            Toast.makeText(
                                context,
                                "Sign in successful",
                                Toast.LENGTH_LONG
                            ).show()

                            navController.navigate(MealMateScreen.Dashboard.name)
                            viewModel.resetState()
                        }
                    }

                    LoginScreenPreview(
                        state = state,
                        onSignInClick = { email, password ->
                            lifecycleOwner.lifecycleScope.launch {
                                val result =
                                    googleAuthUiClient.signInWithEmailAndPassword(email, password)
                                viewModel.onSignInResult(result);
                            }
                        },
                        onGoogleSignInClick = {
                            lifecycleOwner.lifecycleScope.launch {
                                val signInIntentSender = googleAuthUiClient.signIn()
                                launcher.launch(
                                    IntentSenderRequest.Builder(
                                        signInIntentSender ?: return@launch
                                    ).build()
                                )
                            }
                        },
                        onSignupBtnClicked = { navController.navigate(MealMateScreen.Signup.name) },
                    )
                }

                composable(route = MealMateScreen.Signup.name) {
                    val viewModel = viewModel<SignUpViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()

                    LaunchedEffect(key1 = state.isSignUpSuccessful) {
                        if (state.isSignUpSuccessful) {
                            Toast.makeText(
                                context,
                                "Created user successfully",
                                Toast.LENGTH_LONG
                            ).show()

                            navController.navigate(MealMateScreen.Login.name)
                            viewModel.resetState()
                        }
                    }

                    SignupScreenPreview(
                        onSubmit = { email, password ->
                            lifecycleOwner.lifecycleScope.launch {
                                val result =
                                    googleAuthUiClient.signUpWithEmailAndPassword(email, password)
                                viewModel.onSignUpResult(result);
                            }
                        },
                        onLoginBtnClicked = { navController.navigate(MealMateScreen.Login.name) })
                }

                composable(route = MealMateScreen.Dashboard.name) {
                    DashboardScreen(
                        onAddItemClick = { navController.navigate(MealMateScreen.AddItem.name) }
                    )
                }

                composable(route = MealMateScreen.AddItem.name) {
                    AddItemScreen(onAddItemClick = { item ->
                        lifecycleOwner.lifecycleScope.launch {
                            val user = googleAuthUiClient.getSignedInUser();
                            if (user?.email != null) {
                                addItemToDb(db, context, item, user.email)
                            }

                            navController.navigate(MealMateScreen.Dashboard.name)
                        }
                    }, onBackBtnClick = {
                        navController.navigate(MealMateScreen.Dashboard.name)
                    })
                }

                composable(route = "${MealMateScreen.ItemDetails.name}/{itemId}",
                    arguments = listOf(navArgument("itemId") { type = NavType.StringType })
                ) {backStackEntry ->
                    val itemId = backStackEntry.arguments?.getString("itemId")
                    ItemDetailsScreen(onBackBtnClick = { navController.navigate(MealMateScreen.Dashboard.name) }, itemId = itemId)
                }

                composable(route = "${MealMateScreen.EditItem.name}/{itemId}",
                    arguments = listOf(navArgument("itemId") { type = NavType.StringType })
                ) {backStackEntry ->
                    val itemId = backStackEntry.arguments?.getString("itemId")
                    EditItemScreen(onBackBtnClick = { navController.navigate(MealMateScreen.Dashboard.name) }, itemId = itemId,
                        onUpdateItemClick = {item ->
                            lifecycleOwner.lifecycleScope.launch {
                                val user = googleAuthUiClient.getSignedInUser();
                                if (user?.email != null) {
                                    updateItemOfDb(db, context, item, userId = user.email)
                                }
                            }
                        })
                }

                composable(route = MealMateScreen.RecipeDetails.name){
                    RecipeListScreen(onBackBtnClick = { navController.navigate(MealMateScreen.Dashboard.name) })
                }

                composable(route = MealMateScreen.AddRecipe.name){
                    AddRecipeItemScreen(onAddItemClick = { item ->
                        lifecycleOwner.lifecycleScope.launch {
                            val user = googleAuthUiClient.getSignedInUser();
                            if (user?.email != null) {
                                addRecipeToDb(db, context, item, user.email)
                            }

                            navController.navigate(MealMateScreen.RecipeDetails.name)
                        }
                    }, onBackBtnClick = {
                        navController.navigate(MealMateScreen.RecipeDetails.name)
                    })
                }

                composable(route = "${MealMateScreen.RecipeDetails.name}/{itemId}",
                    arguments = listOf(navArgument("itemId") { type = NavType.StringType })){backStackEntry ->
                    val itemId = backStackEntry.arguments?.getString("itemId")
                    RecipeDetailScreen(onBackBtnClick = { navController.navigate(MealMateScreen.RecipeDetails.name) }, itemId = itemId)
                }
            }
        }
    }
}

