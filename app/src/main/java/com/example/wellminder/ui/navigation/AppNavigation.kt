package com.example.wellminder.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wellminder.ui.screens.home.HomeScreen
import com.example.wellminder.ui.screens.food.FoodScreen
import com.example.wellminder.ui.screens.stats.StatsScreen
import com.example.wellminder.ui.screens.profile.ProfileScreen

@Composable
fun AppNavigation(
    startDestination: String = "login"
) {
    val navController = rememberNavController()
    val context = androidx.compose.ui.platform.LocalContext.current
    // Ideally inject VM, but for now accessing Prefs directly or via VM
    // We'll trust that MainActivity handles startDestination only. 
    // BUT LoginScreen needs to set isLoggedIn. 
    // Let's get PreferenceManager here or pass a callback? 
    // Simpler: Use a HiltViewModel for Auth, or just direct access since we are in Composable.
    // Let's use entry point or just pass callbacks that MainActivity could handle? 
    // Actually, LoginScreen can get its own ViewModel or we can just instantiate PrefManager (bad practice but quick)
    // BETTER: LoginScreen should have a ViewModel.
    // SHORTCUT for user request: Just Context.
    val preferenceManager = remember { com.example.wellminder.data.manager.PreferenceManager(context) }
    
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable("login") {
            com.example.wellminder.ui.screens.auth.LoginScreen(
                onNavigateToHome = {
                    preferenceManager.isLoggedIn = true // Зберігаємо стан входу
                    val nextRoute = if (preferenceManager.isOnboardingComplete) "home" else "onboarding_gender"
                    navController.navigate(nextRoute) {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }
        
        composable("register") {
            com.example.wellminder.ui.screens.auth.RegisterScreen(
                onNavigateToHome = {
                    // Після реєстрації також ідемо на онбординг
                     preferenceManager.isLoggedIn = true // Зберігаємо стан входу
                    val nextRoute = "onboarding_gender" // Завжди онбординг після реєстрації
                    navController.navigate(nextRoute) {
                        popUpTo("register") { inclusive = true }
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable("onboarding_gender") {
            com.example.wellminder.ui.screens.onboarding.GenderScreen(
                onNavigateToUserInfo = { gender ->
                    preferenceManager.gender = gender
                    navController.navigate("onboarding_goal")
                }
            )
        }

        composable("onboarding_goal") {
            com.example.wellminder.ui.screens.onboarding.GoalScreen(
                onNavigateToUserInfo = {
                    navController.navigate("onboarding_user_info")
                }
            )
        }

        composable("onboarding_user_info") {
            com.example.wellminder.ui.screens.onboarding.UserInfoScreen(
                onNavigateToHome = { age, weight, height ->
                    preferenceManager.age = age
                    preferenceManager.weight = weight
                    preferenceManager.height = height
                    preferenceManager.isOnboardingComplete = true
                    
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true } // Очищаємо стек переходу назад
                        popUpTo("onboarding_gender") { inclusive = true }
                    }
                }
            )
        }

        composable("home") { 
            HomeScreen(onNavigate = { route -> 
                if (route != "home") {
                    navController.navigate(route) {
                        popUpTo("home") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }) 
        }
        composable("food") { 
            FoodScreen(onNavigate = { route ->
                if (route != "food") {
                    navController.navigate(route) {
                        popUpTo("home") { saveState = true } // Повернення на головну
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }) 
        }
        composable("stats") {
            StatsScreen(onNavigate = { route ->
                if (route != "stats") {
                    navController.navigate(route) {
                        popUpTo("home") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            })
        }

        composable("profile") {
            ProfileScreen(onNavigate = { route ->
                if (route == "login") {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true } // Очищаємо весь стек переходів
                    }
                } else if (route != "profile") {
                    navController.navigate(route) {
                        popUpTo("home") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            })
        }
    }
}
