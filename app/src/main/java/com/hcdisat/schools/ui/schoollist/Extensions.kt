package com.hcdisat.schools.ui.schoollist

import com.hcdisat.schools.models.School
import com.hcdisat.schools.ui.state.RequestState

fun RequestState.SUCCESS<*>.getSchools(): List<School> =
    (results as List<*>).filterIsInstance<School>()