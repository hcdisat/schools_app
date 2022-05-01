package com.hcdisat.schools.dataaccess.network

import com.hcdisat.schools.dataaccess.database.IDbRepository
import com.hcdisat.schools.di.IODispatcher
import com.hcdisat.schools.ui.state.RequestState
import com.hcdisat.schools.ui.state.RequestState.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

interface ISchoolApiRepository {
    val schoolRequestState: StateFlow<RequestState>
    val detailsRequestState: StateFlow<RequestState>
    fun getSchoolDetails(dbn: String)
    fun refreshSchools(): Job
}

class SchoolApiRepository @Inject constructor(
    private val schoolApi: SchoolApi,
    private val dbRepository: IDbRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher
) : ISchoolApiRepository {

    private val _schoolRequestState: MutableStateFlow<RequestState> = MutableStateFlow(LOADING)
    override val schoolRequestState: StateFlow<RequestState>
        get() = _schoolRequestState

    private val _detailsRequestState: MutableStateFlow<RequestState> = MutableStateFlow(LOADING)
    override val detailsRequestState: StateFlow<RequestState>
        get() = _detailsRequestState

    var scope = CoroutineScope(dispatcher)

    override fun refreshSchools() = scope.launch {
        _detailsRequestState.value = LOADING
        try {
            val schoolsResponseDeferred = async { schoolApi.getSchools() }
            val detailsResponseDeferred = async { schoolApi.getDetails() }

            val schoolsResponse = schoolsResponseDeferred.await()
            val detailsResponse = detailsResponseDeferred.await()

            if (schoolsResponse.isSuccessful || detailsResponse.isSuccessful) {
                schoolsResponse.body()?.let { schools ->
                    detailsResponse.body()?.let { details ->
                        dbRepository.saveSchools(schools)
                        dbRepository.saveSchoolDetails(details)
                        _schoolRequestState.value = SUCCESS(dbRepository.allSchools())
                    } ?: throw Exception("School details body is empty")
                } ?: throw Exception("Schools body is empty")
            } else throw Exception("Not Success")
        } catch (ex: Exception) {
            _schoolRequestState.value = ERROR(ex)
        }
    }

    override fun getSchoolDetails(dbn: String) {
        _detailsRequestState.value = LOADING
        scope.launch {
            _detailsRequestState.value = dbRepository.details(dbn)?.let {
                SUCCESS(it)
            } ?: ERROR(Exception("No records Found"))
        }
    }
}