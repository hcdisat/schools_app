package com.hcdisat.schools.di

import android.content.Context
import androidx.room.Room
import com.hcdisat.schools.dataaccess.database.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    fun providesDatabase(@ApplicationContext context: Context): SchoolDb =
        Room.databaseBuilder(
            context,
            SchoolDb::class.java,
            DB_NAME
        ).build()

    @Provides
    fun providesSchoolDao(database: SchoolDb): SchoolDao = database.schoolsDao()

    @Provides
    fun providesDbRepository(schoolDao: SchoolDao): IDbRepository = DbRepository(schoolDao)
}