package com.prajwalch.textondroid.di

import com.prajwalch.textondroid.data.DocumentRepository

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::DocumentRepository)
}