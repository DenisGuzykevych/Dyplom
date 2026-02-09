package com.example.wellminder // Trigger re-index

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import com.example.wellminder.ui.navigation.AppNavigation
import com.example.wellminder.ui.theme.WellMinderTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @javax.inject.Inject
    lateinit var preferenceManager: com.example.wellminder.data.manager.PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val startDest = if (preferenceManager.isLoggedIn) "home" else "login"
        
        setContent {
            WellMinderTheme {
                AppNavigation(startDestination = startDest)
            }
        }
    }
}