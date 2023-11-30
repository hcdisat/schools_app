package com.hcdisat.schools.ui.schoollist

import com.hcdisat.schools.domain.Reducer
import com.hcdisat.schools.ui.schoollist.SchoolAction.*

class SchoolReducer : Reducer<SchoolState, SchoolAction> {

    override fun reduce(currentState: SchoolState, action: SchoolAction): SchoolState =
        when (action) {
            is SchoolListChanged -> {
                currentState.copy(
                    schools = action.newSchoolList,
                    showProgressBar = false
                )
            }

            is ShowHideProgressBar -> currentState.copy(showProgressBar = true)
            else -> currentState
        }
}