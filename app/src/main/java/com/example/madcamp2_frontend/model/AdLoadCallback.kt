package com.example.madcamp2_frontend.model

interface AdLoadCallback {
    fun onAdLoaded()
    fun onAdFailedToLoad(error: String)
}