package com.canerture.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.canerture.pokedex.ui.navigation.PokedexBottomBar
import com.canerture.pokedex.ui.navigation.PokedexNavHost
import com.canerture.pokedex.ui.theme.PokedexTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokedexTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {

                    val navController = rememberNavController()

                    Scaffold(
                        bottomBar = {
                            PokedexBottomBar(navController = navController)
                        }
                    ) {
                        PokedexNavHost(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(it),
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}