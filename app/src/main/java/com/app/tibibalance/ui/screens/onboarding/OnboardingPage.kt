package com.app.tibibalance.ui.screens.onboarding

import androidx.annotation.RawRes
import androidx.annotation.StringRes

data class OnboardingPage(
    @StringRes   val titleRes: Int,
    @StringRes   val descRes: Int,
    @RawRes      val lottieRawRes: Int
)
