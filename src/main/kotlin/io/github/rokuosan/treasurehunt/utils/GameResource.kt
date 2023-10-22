package io.github.rokuosan.treasurehunt.utils



object GameResource {
    enum class GameStatus {
        NOT_STARTED,
        PLAYING,
    }

    var status = GameStatus.NOT_STARTED
}