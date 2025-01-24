package com.projectdelta.chopper.di

import android.app.Application
import android.content.Context
import android.content.res.AssetManager
import com.projectdelta.chopper.ChopperApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

	@Singleton
	@Provides
	fun provideApplication(@ApplicationContext app: Context): ChopperApplication =
		app as ChopperApplication

	@Singleton
	@Provides
	fun provideAssetManager(application: Application) : AssetManager =
		application.assets

}
