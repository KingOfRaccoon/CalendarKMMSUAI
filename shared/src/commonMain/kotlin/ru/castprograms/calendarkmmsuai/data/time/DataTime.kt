package ru.castprograms.calendarkmmsuai.data.time

import kotlinx.datetime.*

open class DataTime(
    var year: Int = 0,
    var mouth: Int = 0,
    var dayOfMouth: Int = 0,
    var hour: Int = 0,
    var minute: Int = 0,
    var dayOfWeek: Int = 0,
) {
    constructor(localDateTime: LocalDateTime) : this(
        localDateTime.year,
        localDateTime.month.number,
        localDateTime.dayOfMonth,
        localDateTime.hour,
        localDateTime.minute,
        localDateTime.dayOfWeek.isoDayNumber,
    )

    fun getTime(): String {
        val localDateTime = LocalDateTime(year, mouth, dayOfWeek, hour, minute)
            .toInstant(TimeZone.currentSystemDefault())
            .toLocalDateTime(TimeZone.currentSystemDefault())

        var string = ""
        val date = now()
        when (date.dayOfMouth - localDateTime.dayOfMonth) {
            -1 -> string += "Завтра"
            0 -> string += "Сегодня"
            1 -> string += "Вчера"
            else -> {
                string += localDateTime.dayOfMonth.toString()
                string += " "
                string += getMouth(localDateTime.monthNumber)
            }
        }
        string += ", "
        string += "${localDateTime.hour}:${localDateTime.minute}"
        return string
    }

    fun getShortcutDayOfWeek() = when(dayOfWeek){
        DayOfWeek.MONDAY.isoDayNumber -> "Пн"
        DayOfWeek.TUESDAY.isoDayNumber -> "Вт"
        DayOfWeek.WEDNESDAY.isoDayNumber -> "Ср"
        DayOfWeek.THURSDAY.isoDayNumber -> "Чт"
        DayOfWeek.FRIDAY.isoDayNumber -> "Пт"
        DayOfWeek.SATURDAY.isoDayNumber -> "Сб"
        DayOfWeek.SUNDAY.isoDayNumber -> "Вс"
        else -> ""
    }

    fun getMouthAndYear() = when (mouth) {
        0 -> "Января"
        1 -> "Февраля"
        2 -> "Марта"
        3 -> "Апреля"
        4 -> "Мая"
        5 -> "Июня"
        6 -> "Июля"
        7 -> "Августа"
        8 -> "Сентября"
        9 -> "Октября"
        10 -> "Ноября"
        11 -> "Декабря"
        else -> ""
    } + " $year"

    fun getDayOfWeekText() = when(dayOfWeek){
        DayOfWeek.MONDAY.isoDayNumber -> "Понедельник"
        DayOfWeek.TUESDAY.isoDayNumber -> "Вторник"
        DayOfWeek.WEDNESDAY.isoDayNumber -> "Среда"
        DayOfWeek.THURSDAY.isoDayNumber -> "Четверг"
        DayOfWeek.FRIDAY.isoDayNumber -> "Пятница"
        DayOfWeek.SATURDAY.isoDayNumber -> "Суббота"
        DayOfWeek.SUNDAY.isoDayNumber -> "Воскресенье"
        else -> ""
    }

    override fun toString(): String {
        return "$dayOfMouth.${mouth+1}.$year"
    }

    fun convertToTime() = "${hour}:${minute}"

    fun getTimeAndDate(): String{
        return convertToTime() + " " + toString()
    }


    companion object {
        fun now() = DataTime(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()))

        private fun getMouth(mouth: Int) = when (mouth) {
            0 -> "Января"
            1 -> "Февраля"
            2 -> "Марта"
            3 -> "Апреля"
            4 -> "Мая"
            5 -> "Июня"
            6 -> "Июля"
            7 -> "Августа"
            8 -> "Сентября"
            9 -> "Октября"
            10 -> "Ноября"
            11 -> "Декабря"
            else -> ""
        }
    }
}