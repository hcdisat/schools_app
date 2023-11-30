package com.hcdisat.schools.ui.schoollist

import com.hcdisat.schools.domain.Action
import com.hcdisat.schools.models.School

sealed class SchoolAction : Action {
    data class SchoolListChanged(val newSchoolList: List<School>) : SchoolAction()
    data class ShowHideProgressBar(val show: Boolean) : SchoolAction()
    data class SchoolClicked(val school: School) : SchoolAction()
    data class ErrorLoadingSchools(val throwable: Throwable?): SchoolAction()
}
