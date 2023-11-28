package com.canerture.pokedex.common

fun Int.getImageUrl(): String =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/$this.png"