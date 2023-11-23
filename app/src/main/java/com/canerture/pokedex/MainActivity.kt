package com.canerture.pokedex

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.canerture.pokedex.ui.theme.PokedexTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.IOException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

data class Pokemon(
    val id: Int?,
    val name: String?,
    val pokemonId: Int?,
)

data class PokemonUI(
    val id: Int,
    val name: String,
    val image: ImageBitmap
)

sealed interface HomeState {
    data object Loading : HomeState
    data class PokemonList(val list: List<PokemonUI>) : HomeState
    data class Error(val message: String) : HomeState
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokedexTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {

                    val state: MutableState<HomeState> = remember {
                        mutableStateOf(HomeState.Loading)
                    }

                    fetchData(
                        url = "https://my-json-server.typicode.com/ozcanzaferayan/pokedex/pokemons",
                        method = "GET",
                        onSuccess = {
                            val pokemonResponse = JSONArray(it)

                            val pokemonList = mutableListOf<Pokemon>()

                            for (i in 0 until pokemonResponse.length()) {
                                val item = pokemonResponse.getJSONObject(i)

                                val pokemon = Pokemon(
                                    id = item.getInt("id"),
                                    name = item.getString("name"),
                                    pokemonId = item.getInt("pokemonId")
                                )

                                pokemonList.add(pokemon)
                            }



                            state.value = HomeState.PokemonList(
                                pokemonList.map {
                                    val imageUrl =
                                        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/${it.pokemonId}.png"

                                    PokemonUI(
                                        id = it.id ?: 0,
                                        name = it.name.orEmpty(),
                                        image = imageUrl.urlToImageBitmap() ?: ImageBitmap(1, 1)
                                    )
                                }
                            )
                        },
                        onError = {
                            state.value = HomeState.Error(it)
                        }
                    )

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
            }
        }
    }
}

fun fetchData(
    url: String,
    method: String,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        val connection = (URL(url).openConnection() as HttpsURLConnection).apply {
            requestMethod = method
            setRequestProperty("Content-Type", "application/json")
        }

        try {
            connection.connect()

            if (connection.responseCode == HttpsURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                onSuccess(response)
            } else {
                throw Exception("HTTP error: ${connection.responseCode}")
            }
        } catch (e: Exception) {
            onError(e.message.orEmpty())
        } finally {
            connection.disconnect()
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

fun String?.urlToImageBitmap(): ImageBitmap? {
    val connection = (URL(this).openConnection() as HttpsURLConnection)

    return try {
        connection.connect()

        BitmapFactory.decodeStream(connection.inputStream).asImageBitmap()
    } catch (e: IOException) {
        null
    } finally {
        connection.disconnect()
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

        Image(
            modifier = Modifier.size(64.dp),
            bitmap = pokemon.image,
            contentDescription = "Pokemon Image"
        )
        Text(
            text = pokemon.name.orEmpty()
        )
    }
}