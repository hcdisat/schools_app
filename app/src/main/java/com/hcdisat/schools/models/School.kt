package com.hcdisat.schools.models


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "schools")
data class School(
    @SerializedName("dbn")
    @PrimaryKey val dbn: String,
    @SerializedName("school_name")
    val schoolName: String,
    @SerializedName("total_students")
    val totalStudents: String,
    @SerializedName("school_sports")
    val schoolSports: String?
)