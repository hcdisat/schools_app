package com.hcdisat.schools.dataaccess

import com.hcdisat.schools.dataaccess.database.IDbRepository
import com.hcdisat.schools.dataaccess.network.ISchoolApiRepository
import com.hcdisat.schools.models.School
import com.hcdisat.schools.models.SchoolDetails
import com.hcdisat.schools.ui.state.RequestState
import javax.inject.Inject

interface AppRepository {

    suspend fun getSchools(): RequestState
    suspend fun getSchoolDetails(dbn: String): RequestState
    suspend fun saveSchools(schools: List<School>)
    suspend fun saveSchoolDetails(schoolsDetails: List<SchoolDetails>)
}

class AppRepositoryImpl @Inject constructor(
    private val apiRepository: ISchoolApiRepository,
    private val dbRepository: IDbRepository
) : AppRepository {

    override suspend fun getSchools(): RequestState =
        when (val requestState = apiRepository.getSchools()) {
            is RequestState.SUCCESS<*> -> {
                val data = requestState.results as Pair<*, *>
                val schools = (data.first as List<*>).filterIsInstance<School>()
                val schoolDetails = (data.second as List<*>).filterIsInstance<SchoolDetails>()

                saveSchools(schools)
                saveSchoolDetails(schoolDetails)
                RequestState.SUCCESS(dbRepository.allSchools())
            }
            else -> requestState
        }

    override suspend fun getSchoolDetails(dbn: String): RequestState =
        dbRepository.details(dbn)?.let {
            RequestState.SUCCESS(it)
        } ?: RequestState.ERROR(Exception("No records Found"))

    override suspend fun saveSchools(schools: List<School>) =
        dbRepository.saveSchools(schools)

    override suspend fun saveSchoolDetails(schoolsDetails: List<SchoolDetails>) =
        dbRepository.saveSchoolDetails(schoolsDetails)
}