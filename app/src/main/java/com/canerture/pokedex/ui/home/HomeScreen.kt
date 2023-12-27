package com.canerture.pokedex.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.canerture.pokedex.data.mapper.mapToPokemonUIList
import com.canerture.pokedex.data.model.PokemonRequest
import com.canerture.pokedex.data.remote.RetrofitClient
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {

    val state: MutableState<HomeState> = remember {
        mutableStateOf(HomeState.Loading)
    }

    val scope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState()
    var sheetVisible by remember { mutableStateOf(false) }

    var pokemonName by remember { mutableStateOf("") }
    var pokemonId by remember { mutableStateOf("") }

    var pokemonIdErrorState by remember { mutableStateOf(false) }
    var pokemonNameErrorState by remember { mutableStateOf(false) }

    LaunchedEffect(Dispatchers.IO) {
        state.value = try {
            val response = RetrofitClient.pokemonApi.getPokemons()

            if (response.isSuccessful) {
                HomeState.PokemonList(response.body().orEmpty().mapToPokemonUIList())
            } else {
                HomeState.Error("Pokemons not found!")
            }
        } catch (e: Exception) {
            HomeState.Error(e.message.orEmpty())
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = {
                    Text(text = "Add")
                },
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Add Pokemon"
                    )
                },
                onClick = {
                    sheetVisible = true
                }
            )
        },
        content = { contentPadding ->
            when (state.value) {
                is HomeState.Loading -> {

                }

                is HomeState.PokemonList -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items((state.value as HomeState.PokemonList).list) { pokemon ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {

                                GlideImage(
                                    modifier = Modifier.size(64.dp),
                                    imageModel = { pokemon.imageUrl },
                                    imageOptions = ImageOptions(
                                        contentScale = ContentScale.Crop,
                                        alignment = Alignment.Center,
                                        contentDescription = "Pokemon Image"
                                    )
                                )
                                Text(
                                    text = pokemon.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                is HomeState.Error -> {

                }
            }
        }
    )

    if (sheetVisible) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                sheetVisible = false
            },
            content = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = pokemonId,
                        label = {
                            Text(text = "Pokemon Id")
                        },
                        supportingText = {
                            if (pokemonIdErrorState) {
                                Text(text = "Pokemon Id must be between 2 and 100")
                            }
                        },
                        onValueChange = {
                            pokemonId = it
                        },
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = pokemonName,
                        label = {
                            Text(text = "Pokemon Name")
                        },
                        supportingText = {
                            if (pokemonNameErrorState) {
                                Text(text = "Pokemon Name must be at least 2 characters")
                            }
                        },
                        onValueChange = {
                            pokemonName = it
                        },
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            when {
                                pokemonId.isEmpty() -> {
                                    pokemonIdErrorState = true
                                }

                                pokemonId.toInt() !in 2..100 -> {
                                    pokemonIdErrorState = true
                                }

                                pokemonName.length < 2 -> {
                                    pokemonNameErrorState = true
                                }

                                else -> {
                                    scope.launch(Dispatchers.IO) {
                                        try {
                                            val response = RetrofitClient.pokemonApi.addPokemon(
                                                PokemonRequest(
                                                    name = pokemonName,
                                                    pokemonId = pokemonId.toInt()
                                                )
                                            )

                                            if (response.isSuccessful) {
                                                sheetVisible = false
                                            } else {
                                                HomeState.Error("Pokemons not found!")
                                            }
                                        } catch (e: Exception) {
                                            HomeState.Error(e.message.orEmpty())
                                        }
                                    }
                                }
                            }
                        }
                    ) {
                        Text(text = "Add Pokemon")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        )
    }
}