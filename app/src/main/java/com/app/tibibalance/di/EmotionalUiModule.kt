package com.app.tibibalance.di

import com.app.tibibalance.R
import com.app.tibibalance.data.repository.EmotionalRepository
import com.app.tibibalance.ui.screens.emotional.EmotionRecord
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EmotionalUiModule {

    /** Stub que devuelve siempre lista vacía, solo para que la UI funcione. */
    @Provides
    @Singleton
    fun provideEmotionalRepository(): EmotionalRepository =
        object : EmotionalRepository {
            override fun observeEmotions(): Flow<List<EmotionRecord>> =
                flowOf(listOf(
                    EmotionRecord(LocalDate.now().withDayOfMonth(2), R.drawable.iconhappyimage),
                    EmotionRecord(LocalDate.now().withDayOfMonth(5), R.drawable.iconsadimage),
                    EmotionRecord(LocalDate.now().withDayOfMonth(8), R.drawable.iconangryimage)
                ))
        }
}
