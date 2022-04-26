package com.hcdisat.schools.di

import com.hcdisat.schools.dataaccess.database.IDbRepository
import com.hcdisat.schools.dataaccess.network.ISchoolApiRepository
import com.hcdisat.schools.dataaccess.network.SchoolApi
import com.hcdisat.schools.dataaccess.network.SchoolApiRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

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

    @Provides
    fun providesSchoolApiRepository(schoolApi: SchoolApi, dbRepository: IDbRepository): ISchoolApiRepository =
        SchoolApiRepository(schoolApi, dbRepository)
}

private const val REQUEST_TIMEOUT = 30L