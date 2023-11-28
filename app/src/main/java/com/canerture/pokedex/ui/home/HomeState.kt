package com.canerture.pokedex.ui.home

import com.canerture.pokedex.data.model.PokemonUI

sealed interface HomeState {
    data object Loading : HomeState
    data class PokemonList(val list: List<PokemonUI>) : HomeState
    data class Error(val message: String) : HomeState
}