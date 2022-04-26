package com.hcdisat.schools.dataaccess.network

import com.hcdisat.schools.dataaccess.database.IDbRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface ISchoolApiRepository {
    fun getSchools(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<RequestState>
    fun getSchoolDetails(
        dbn: String
    ): Flow<RequestState>
}

class SchoolApiRepository @Inject constructor(
    private val schoolApi: SchoolApi,
    private val dbRepository: IDbRepository
) : ISchoolApiRepository {

    override fun getSchools(dispatcher: CoroutineDispatcher): Flow<RequestState> =
        flow {
            try {
                val schoolsResponse = withContext(dispatcher) { schoolApi.getSchools() }
                val detailsResponse = withContext(dispatcher) { schoolApi.getDetails() }
                if (schoolsResponse.isSuccessful || detailsResponse.isSuccessful) {
                    schoolsResponse.body()?.let { schools ->
                        detailsResponse.body()?.let { details ->
                            dbRepository.saveSchools(schools)
                            dbRepository.saveSchoolDetails(details)
                        } ?: throw Exception("School Body is empty")
                    } ?: throw Exception("School Details Body is empty")

                } else throw Exception("Not Success")

                emit(RequestState.SUCCESS(dbRepository.allSchools()))
            } catch (ex: Exception) {
                emit(RequestState.ERROR(ex))
            }
        }

    override fun getSchoolDetails(
        dbn: String
    ): Flow<RequestState> =
        flow {
            val results = dbRepository.details(dbn)
            if (results == null)
                emit(RequestState.ERROR(Exception("No records Found")))
            else emit(RequestState.SUCCESS(results))
        }
}

sealed interface RequestState {
    class SUCCESS<T>(val results: T) : RequestState
    class ERROR(val throwable: Throwable) : RequestState
}