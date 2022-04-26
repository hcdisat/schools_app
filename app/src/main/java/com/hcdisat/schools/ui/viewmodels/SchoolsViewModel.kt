package com.hcdisat.schools.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hcdisat.schools.dataaccess.network.ISchoolApiRepository
import com.hcdisat.schools.dataaccess.network.RequestState
import com.hcdisat.schools.di.CoroutinesDispatchersModule.IODispatcher
import com.hcdisat.schools.models.SchoolDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SchoolsViewModel @Inject constructor(
    private val schoolRepo: ISchoolApiRepository,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private var _schoolData: MutableLiveData<SchoolResults> =
        MutableLiveData(SchoolResults.LOADING)
    val schoolData: LiveData<SchoolResults> get() = _schoolData

    private var _schoolDetails: MutableLiveData<SchoolResults> =
        MutableLiveData(SchoolResults.LOADING)
    val schoolDetails: LiveData<SchoolResults> get() = _schoolDetails

    private var _selectedDbn: String = ""

    fun getSchools() {
        if (schoolData.value is SchoolResults.SUCCESS<*>) return

        viewModelScope.launch {
            schoolRepo.getSchools(ioDispatcher).collect {
                when (it) {
                    is RequestState.ERROR ->
                        _schoolData.postValue(SchoolResults.ERROR(it.throwable))
                    is RequestState.SUCCESS<*> ->
                        _schoolData.postValue(SchoolResults.SUCCESS(it.results))
                }
            }
        }
    }

    fun getSchoolDetails() {
        _schoolDetails.value = SchoolResults.LOADING
        viewModelScope.launch(ioDispatcher) {
            schoolRepo.getSchoolDetails(_selectedDbn).collect {
                when(it) {
                    is RequestState.SUCCESS<*> ->
                        _schoolDetails.postValue(
                            SchoolResults.SUCCESS(it.results as? SchoolDetails)
                        )
                    is RequestState.ERROR -> _schoolDetails.postValue(
                        SchoolResults.ERROR(it.throwable)
                    )
                }
            }
        }
    }

    fun selectedDbn(dbn: String) {
        _selectedDbn = dbn
    }

}

sealed interface SchoolResults {
    object LOADING : SchoolResults
    class SUCCESS<T>(val results: T) : SchoolResults
    class ERROR(val throwable: Throwable) : SchoolResults
}