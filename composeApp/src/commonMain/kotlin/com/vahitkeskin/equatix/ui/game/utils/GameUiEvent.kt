package com.vahitkeskin.equatix.ui.game.utils

sealed interface GameUiEvent {
    data object VibrateError : GameUiEvent
}