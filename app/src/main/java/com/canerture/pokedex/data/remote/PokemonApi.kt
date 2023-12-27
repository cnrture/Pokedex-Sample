package com.canerture.pokedex.data.remote

import com.canerture.pokedex.data.model.Pokemon
import com.canerture.pokedex.data.model.PokemonRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PokemonApi {

    @GET("pokemons")
    suspend fun getPokemons(): Response<List<Pokemon>>

    @POST("pokemons")
    suspend fun addPokemon(
        @Body pokemonRequest: PokemonRequest
    ): Response<Pokemon>
}