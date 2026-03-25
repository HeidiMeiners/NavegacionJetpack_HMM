package com.example.navegacionjetpack_hmm

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Buscaminas : Screen("Buscaminas")
    object EncuentraTopo : Screen("encuentraTopo")
}