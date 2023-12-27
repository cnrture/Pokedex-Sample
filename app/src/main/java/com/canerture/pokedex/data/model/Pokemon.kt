package com.canerture.pokedex.data.model

data class Pokemon(
    val id: Int?,
    val name: String?,
    val pokemonId: Int?,
)

data class PokemonRequest(
    val name: String,
    val pokemonId: Int,
)