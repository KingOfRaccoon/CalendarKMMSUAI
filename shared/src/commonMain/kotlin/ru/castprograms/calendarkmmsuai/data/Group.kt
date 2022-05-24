package ru.castprograms.calendarkmmsuai.data

import kotlinx.serialization.SerialName

data class Group(
    @SerialName("ItemId")
    val itemId: Int = 0,
    @SerialName("Name")
    val name: String = ""
)