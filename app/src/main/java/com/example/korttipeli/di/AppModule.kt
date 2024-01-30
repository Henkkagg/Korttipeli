package com.example.korttipeli.di

import android.content.Context
import androidx.room.Room
import com.example.korttipeli.data.HttpClientImpl
import com.example.korttipeli.data.SharedPref
import com.example.korttipeli.data.repository.*
import com.example.korttipeli.data.room.Database
import com.example.korttipeli.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.util.*
import io.ktor.utils.io.concurrent.*
import java.io.File
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPref {
        return SharedPref(context)
    }

    @Provides
    @Singleton
    fun provideHttpClient(sharedPref: SharedPref): HttpClientImpl {
        return HttpClientImpl(sharedPref)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): Database {
        return Room.databaseBuilder(
            context, Database::class.java, "juomapeli_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideLoginAndRegistrationRepository(
        client: HttpClientImpl,
        sharedPref: SharedPref
    ): AccountManagementRepository {
        return AccountManagementImpl(client, sharedPref)
    }

    @Provides
    @Singleton
    fun provideInternalFilesDirectory(
        @ApplicationContext context: Context
    ): File {
        return context.filesDir
    }

    @Provides
    @Singleton
    fun provideFriendlistRepository(
        client: HttpClientImpl
    ): FriendlistRepository {
        return FriendlistImpl(client)
    }

    @Provides
    @Singleton
    fun provideCardsRepository(
        client: HttpClientImpl
    ): CardsRepository {
        return CardsImpl(client)
    }

    @Provides
    @Singleton
    fun provideDecksRepository(
        client: HttpClientImpl
    ): DecksRepository {
        return DecksImpl(client)
    }

    @Provides
    @Singleton
    fun provideGameRepository(
        client: HttpClientImpl
    ): GameRepository {
        return GameImpl(client)
    }
}