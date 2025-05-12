/* di/SchedulerModule.kt */
package com.app.tibibalance.di

import android.app.AlarmManager
import android.content.Context
import com.app.tibibalance.core.scheduler.HabitAlertScheduler
import com.app.tibibalance.core.scheduler.HabitAlertSchedulerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SchedulerModule {

    /* Android system service --------------------------------------------- */
    @Provides
    @Singleton
    fun provideAlarmManager(
        @ApplicationContext ctx: Context
    ): AlarmManager =
        ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
}

@Module
@InstallIn(SingletonComponent::class)
abstract class SchedulerBindings {

    /* HabitAlertScheduler binding ---------------------------------------- */
    @Binds
    @Singleton
    abstract fun bindHabitAlertScheduler(
        impl: HabitAlertSchedulerImpl
    ): HabitAlertScheduler
}
