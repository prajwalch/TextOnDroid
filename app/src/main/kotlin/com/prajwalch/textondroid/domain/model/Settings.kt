package com.prajwalch.textondroid.domain.model

enum class Theme {
    Light,
    Dark,
    FollowSystem;

    companion object {
        val Default = FollowSystem
    }
}