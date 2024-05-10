package com.example.tfgproject.ui.gameDetail

import com.example.tfgproject.model.Game
import com.example.tfgproject.model.Team

data class GameDetails(
    val game: Game,
    val localTeam: Team?,
    val visitorTeam: Team?,
)
