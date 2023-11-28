package com.canerture.pokedex.data.remote

import com.canerture.pokedex.data.model.Pokemon
import retrofit2.Response
import retrofit2.http.GET

interface PokemonApi {

    @GET("pokemons")
    suspend fun getPokemons(): Response<List<Pokemon>>
}