package com.canerture.pokedex.data.mapper

import com.canerture.pokedex.common.getImageUrl
import com.canerture.pokedex.data.model.Pokemon
import com.canerture.pokedex.data.model.PokemonUI

fun List<Pokemon>.mapToPokemonUIList(): List<PokemonUI> {
    return map {
        PokemonUI(
            id = it.id ?: 0,
            name = it.name.orEmpty(),
            imageUrl = (it.pokemonId ?: 0).getImageUrl()
        )
    }
}