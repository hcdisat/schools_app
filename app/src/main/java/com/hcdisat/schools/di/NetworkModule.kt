package com.hcdisat.schools.di

import com.hcdisat.schools.dataaccess.network.ISchoolApiRepository
import com.hcdisat.schools.dataaccess.network.SchoolApi
import com.hcdisat.schools.dataaccess.network.SchoolApiRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    fun providesOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .build()


    @Provides
    fun providesRetrofit(okHttpClient: OkHttpClient): SchoolApi =
        Retrofit.Builder()
            .baseUrl(SchoolApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(SchoolApi::class.java)
}

@Module
@InstallIn(ViewModelScoped::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
   abstract fun providesSchoolApiRepository(impl: SchoolApiRepository): ISchoolApiRepository
}

private const val REQUEST_TIMEOUT = 30L