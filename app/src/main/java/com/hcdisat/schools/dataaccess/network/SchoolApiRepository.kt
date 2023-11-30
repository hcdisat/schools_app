package com.hcdisat.schools.dataaccess.network

import com.hcdisat.schools.di.IODispatcher
import com.hcdisat.schools.models.School
import com.hcdisat.schools.models.SchoolDetails
import com.hcdisat.schools.ui.state.RequestState
import com.hcdisat.schools.ui.state.RequestState.ERROR
import com.hcdisat.schools.ui.state.RequestState.SUCCESS
import kotlinx.coroutines.*
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.EmptyCoroutineContext

interface ISchoolApiRepository {
    suspend fun getSchools(): RequestState
}

class SchoolApiRepository @Inject constructor(
    private val schoolApi: SchoolApi
) : ISchoolApiRepository {

    override suspend fun getSchools(): RequestState {

        return try {

            lateinit var schoolsResponseDeferred: Deferred<Response<List<School>>>
            lateinit var detailsResponseDeferred: Deferred<Response<List<SchoolDetails>>>

            coroutineScope {
                schoolsResponseDeferred = async { schoolApi.getSchools() }
                detailsResponseDeferred = async { schoolApi.getDetails() }
            }

            val schoolsResponse = schoolsResponseDeferred.await()
            val detailsResponse = detailsResponseDeferred.await()

            if (schoolsResponse.isSuccessful || detailsResponse.isSuccessful) {
                schoolsResponse.body()?.let { schools ->
                    detailsResponse.body()?.let { details ->
                        SUCCESS(Pair(schools, details))
                    } ?: throw Exception("School details body is empty")
                } ?: throw Exception("Schools body is empty")
            } else throw Exception("Not Success")
        } catch (ex: Exception) {
            ERROR(ex)
        }
    }
}