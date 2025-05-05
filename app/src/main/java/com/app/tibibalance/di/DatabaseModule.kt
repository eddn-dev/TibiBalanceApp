/* di/DatabaseModule.kt */
package com.app.tibibalance.di

import android.content.Context
import androidx.room.Room
import com.app.tibibalance.data.local.AppDb
import com.app.tibibalance.data.local.HabitDao
import com.app.tibibalance.data.local.ProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDb =
        Room.databaseBuilder(ctx, AppDb::class.java, "tibibalance.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideProfileDao(db: AppDb): ProfileDao = db.profileDao()

    @Provides
    fun provideHabitDao(db: AppDb): HabitDao = db.habitDao()
}
