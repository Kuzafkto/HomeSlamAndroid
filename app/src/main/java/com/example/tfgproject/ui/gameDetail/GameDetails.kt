package com.example.tfgproject.ui.gameDetail

import com.example.tfgproject.model.Game
import com.example.tfgproject.model.Team

/**
 * Data class representing the details of a game, including information about the local and visitor teams.
 *
 * @property game The game information.
 * @property localTeam The local team participating in the game.
 * @property visitorTeam The visitor team participating in the game.
 */
data class GameDetails(
    val game: Game,
    val localTeam: Team?,
    val visitorTeam: Team?,
)
