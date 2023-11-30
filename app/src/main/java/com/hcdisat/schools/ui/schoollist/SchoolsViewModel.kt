package com.hcdisat.schools.ui.schoollist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hcdisat.schools.dataaccess.AppRepository
import com.hcdisat.schools.di.IODispatcher
import com.hcdisat.schools.domain.Store
import com.hcdisat.schools.models.School
import com.hcdisat.schools.ui.state.RequestState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SchoolsViewModel @Inject constructor(
    private val appRepository: AppRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val store: Store<SchoolState, SchoolAction> = Store(
        SchoolState(showProgressBar = true),
        SchoolReducer()
    )

    val state = store.currentState

    init {
        loadSchools()
    }

    private fun dispatchNewSchools(requestState: RequestState.SUCCESS<*>) {
        store.dispatch(
            SchoolAction.SchoolListChanged(
                requestState.getSchools()
            )
        )
    }

    private fun dispatchError(requestState: RequestState.ERROR) {
        store.dispatch(
            SchoolAction.ErrorLoadingSchools(
                requestState.throwable
            )
        )
    }

    private fun showProgressBar() {
        store.dispatch(SchoolAction.ShowHideProgressBar(true))
    }

    private fun loadSchools() {
        showProgressBar()
        viewModelScope.launch(dispatcher) {
            when (val requestState = appRepository.getSchools()) {
                is RequestState.SUCCESS<*> -> dispatchNewSchools(requestState)
                is RequestState.ERROR -> dispatchError(requestState)
                is RequestState.INITIAL -> showProgressBar()
            }
        }
    }
}