package com.hcdisat.schools.ui.schoollist

import com.hcdisat.schools.domain.State
import com.hcdisat.schools.models.School

data class SchoolState(
    val schools: List<School> = listOf(),
    val showProgressBar: Boolean = false,
    val error: Throwable? = null
): State

fun SchoolState.isInErrorState() = error == null
fun SchoolState.loadingSchoolsSucceeded() = schools.isNotEmpty()