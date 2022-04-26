package com.hcdisat.schools.dataaccess.database

import androidx.room.*
import com.hcdisat.schools.models.School
import com.hcdisat.schools.models.SchoolDetails

@Database(
    entities = [
        School::class,
        SchoolDetails::class
    ],
    version = 1
)
abstract class SchoolDb: RoomDatabase() {
    abstract fun schoolsDao(): SchoolDao
}

@Dao
interface SchoolDao {

    @Query("SELECT * FROM schools")
    suspend fun allSchools(): List<School>

    @Query("SELECT * FROM school_details WHERE dbn = :dbn")
    suspend fun details(dbn: String): SchoolDetails?

    @Insert(entity = School::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSchools(schools: List<School>)

    @Insert(entity = SchoolDetails::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSchoolDetails(schoolsDetails: List<SchoolDetails>)
}

private const val DB_VERSION = 1
const val DB_NAME = "schools_db"