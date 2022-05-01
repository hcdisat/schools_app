package com.hcdisat.schools.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hcdisat.schools.dataaccess.network.ISchoolApiRepository
import com.hcdisat.schools.ui.state.RequestState
import com.hcdisat.schools.ui.state.RequestState.LOADING
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("UNREACHABLE_CODE")
@HiltViewModel
class SchoolsViewModel @Inject constructor(
    private val schoolRepo: ISchoolApiRepository,
) : ViewModel() {

    private var _schoolData: MutableLiveData<RequestState> = MutableLiveData(LOADING)
    val schoolData: LiveData<RequestState>
        get() = _schoolData

    private var _schoolDetails: MutableLiveData<RequestState> = MutableLiveData(LOADING)
    val schoolDetails: LiveData<RequestState>
        get() = _schoolDetails

    init {
        viewModelScope.launch {
            schoolRepo.refreshSchools()
            launch {
                schoolRepo.schoolRequestState.collect {
                    _schoolData.postValue(it)
                }
            }
        }
    }

    fun getSchoolDetails(dbn: String) = viewModelScope.launch {
        schoolRepo.getSchoolDetails(dbn)
        schoolRepo.detailsRequestState.collect { _schoolDetails.postValue(it) }
    }
}