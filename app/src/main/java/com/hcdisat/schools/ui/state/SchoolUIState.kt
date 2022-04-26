package com.hcdisat.schools.ui.state

import com.hcdisat.schools.models.School

sealed class SchoolUIState {
    object LOADING: SchoolUIState()
    class SUCCESS(schools: List<School>): SchoolUIState()
    class ERROR(val throwable: Throwable): SchoolUIState()
}
