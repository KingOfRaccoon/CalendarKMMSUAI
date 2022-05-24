package ru.castprograms.calendarkmmsuai.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Lesson(
    @SerialName("Build")
    val build: String? = null,
    @SerialName("Day")
    val day: Int = 0,
    @SerialName("Dept")
    val dept: String? = null,
    @SerialName("Disc")
    val disc: String = "",
    @SerialName("Groups")
    val groups: String = "",
    @SerialName("GroupsText")
    val groupsText: String = "",
    @SerialName("ItemId")
    val itemId: Int = 0,
    @SerialName("Less")
    val less: Int = 0,
    @SerialName("Preps")
    val preps: String? = null,
    @SerialName("PrepsText")
    val prepsText: String = "",
    @SerialName("Rooms")
    val rooms: String? = null,
    @SerialName("Type")
    val type: String = "",
    @SerialName("Week")
    val week: Int = 0
)