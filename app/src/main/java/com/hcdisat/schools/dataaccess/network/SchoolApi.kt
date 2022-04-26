package com.hcdisat.schools.dataaccess.network

import com.hcdisat.schools.models.School
import com.hcdisat.schools.models.SchoolDetails
import retrofit2.Response
import retrofit2.http.GET

interface SchoolApi {

    @GET(SCHOOL_PATH)
    suspend fun getSchools(): Response<List<School>>

    @GET(SCHOOL_DETAILS_PATH)
    suspend fun getDetails(): Response<List<SchoolDetails>>

    companion object {
        const val BASE_URL = "https://data.cityofnewyork.us/resource/"
        private const val SCHOOL_PATH = "s3k6-pzi2.json"
        private const val SCHOOL_DETAILS_PATH = "f9bf-2cp4.json"
    }
}