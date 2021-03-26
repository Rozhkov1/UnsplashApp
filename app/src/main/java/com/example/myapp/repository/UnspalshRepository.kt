package com.example.myapp.repository

import com.example.myapp.models.UnsplashModel

interface UnsplashRepository {

    suspend fun getUnsplashImage(): List<UnsplashModel>

    suspend fun insertAllImages(list: List<UnsplashModel>)
}