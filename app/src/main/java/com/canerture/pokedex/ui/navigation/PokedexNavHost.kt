package com.canerture.pokedex.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.canerture.pokedex.ui.home.HomeScreen
import com.canerture.pokedex.ui.settings.SettingsScreen

@Composable
fun PokedexNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen()
        }
        composable("settings") {
            SettingsScreen()
        }
    }
}