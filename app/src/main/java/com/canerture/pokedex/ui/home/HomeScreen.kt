package com.canerture.pokedex.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.canerture.pokedex.data.mapper.mapToPokemonUIList
import com.canerture.pokedex.data.model.PokemonUI
import com.canerture.pokedex.data.remote.RetrofitClient
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.Dispatchers

@Composable
fun HomeScreen() {

    val state: MutableState<HomeState> = remember {
        mutableStateOf(HomeState.Loading)
    }

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

    when (state.value) {
        is HomeState.Loading -> {

        }

        is HomeState.PokemonList -> {
            SuccessContent(pokemons = (state.value as HomeState.PokemonList).list)
        }

        is HomeState.Error -> {

        }
    }
}

@Composable
fun SuccessContent(
    pokemons: List<PokemonUI>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(pokemons) {
            PokemonItem(it)
        }
    }
}

@Composable
fun PokemonItem(
    pokemon: PokemonUI
) {

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