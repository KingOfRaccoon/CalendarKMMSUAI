package ru.castprograms.calendarkmmsuai.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Semester(
    @SerialName("CurrentWeek")
    val currentWeek: Int,
    @SerialName("IsAutumn")
    val isAutumn: Boolean,
    @SerialName("IsWeekOdd")
    val isWeekOdd: Boolean,
    @SerialName("IsWeekRed")
    val isWeekRed: Boolean,
    @SerialName("IsWeekUp")
    val isWeekUp: Boolean,
    @SerialName("Update")
    val update: String,
    @SerialName("Years")
    val years: String
)