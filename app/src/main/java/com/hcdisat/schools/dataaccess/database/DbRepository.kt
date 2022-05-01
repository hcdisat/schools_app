package com.hcdisat.schools.dataaccess.database

import com.hcdisat.schools.models.School
import com.hcdisat.schools.models.SchoolDetails
import javax.inject.Inject

interface IDbRepository {
    suspend fun allSchools(): List<School>
    suspend fun details(dbn: String): SchoolDetails?
    suspend fun saveSchools(schools: List<School>)
    suspend fun saveSchoolDetails(schoolsDetails: List<SchoolDetails>)
}

class DbRepository @Inject constructor(private val schoolDao: SchoolDao) : IDbRepository {
    override suspend fun allSchools(): List<School> = schoolDao.allSchools()

    override suspend fun details(dbn: String): SchoolDetails? = schoolDao.details(dbn)

    override suspend fun saveSchools(schools: List<School>) = schoolDao.saveSchools(schools)

    override suspend fun saveSchoolDetails(schoolsDetails: List<SchoolDetails>) =
        schoolDao.saveSchoolDetails(schoolsDetails)
}