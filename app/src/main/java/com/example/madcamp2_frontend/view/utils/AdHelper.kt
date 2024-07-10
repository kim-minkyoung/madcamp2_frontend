package com.example.madcamp2_frontend.view.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class AdHelper(private val context: Context) {

    private var rewardedAd: RewardedAd? = null
    private val TAG = "AdHelper"

    fun isAdLoaded(): Boolean {
        return rewardedAd != null
    }

    fun loadRewardedAd(adUnitId: String, onAdLoaded: (() -> Unit)? = null) {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(context, adUnitId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, "Ad wasn't loaded: ${adError.message}")
                rewardedAd = null
                onAdLoaded?.invoke()
            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                Log.d(TAG, "Ad was loaded.")
                this@AdHelper.rewardedAd = rewardedAd
                onAdLoaded?.invoke()
            }
        })
    }

    fun showRewardedAd(onAdReward: (() -> Unit)?, onAdClosed: (() -> Unit)? = null) {
        rewardedAd?.let { ad ->
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Ad was dismissed.")
                    loadRewardedAd(Constants.AD_UNIT_ID) // Reload the ad
                    onAdClosed?.invoke()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.d(TAG, "Ad failed to show.")
                    loadRewardedAd(Constants.AD_UNIT_ID)
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Ad showed fullscreen content.")
                    rewardedAd = null
                }
            }

            ad.show(context as Activity) {
                Log.d(TAG, "User earned the reward.")
                onAdReward?.invoke()
            }
        } ?: run {
            Log.d(TAG, "The rewarded ad wasn't ready yet.")
            Toast.makeText(context, "광고가 아직 준비되지 않았어요😢", Toast.LENGTH_SHORT).show()
        }
    }
}
