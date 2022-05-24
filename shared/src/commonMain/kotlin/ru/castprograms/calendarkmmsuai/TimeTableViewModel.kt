package ru.castprograms.calendarkmmsuai

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import ru.castprograms.calendarkmmsuai.data.Lesson
import ru.castprograms.calendarkmmsuai.data.Semester
import ru.castprograms.calendarkmmsuai.data.time.DataTime
import ru.castprograms.calendarkmmsuai.data.time.DataTimeWithDifferentWeek
import ru.castprograms.calendarkmmsuai.dispatchers.ioDispatcher
import ru.castprograms.calendarkmmsuai.repository.TimeTableRepository
import ru.castprograms.calendarkmmsuai.util.Resource

class TimeTableViewModel(private val timeTableRepository: TimeTableRepository) : ViewModel() {
    private val _timeTableGroupFlow = MutableSharedFlow<Resource<Map<Int, List<Lesson>>>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val timeTableGroupFlow = _timeTableGroupFlow.asSharedFlow()

    private val _datesFlow = MutableSharedFlow<List<DataTimeWithDifferentWeek>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val datesFlow = _datesFlow.asSharedFlow()

    private val _semesterInfoFlow = MutableSharedFlow<Resource<Semester>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val semesterInfoFlow = _semesterInfoFlow.asSharedFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch(ioDispatcher) {
            _timeTableGroupFlow.emit(timeTableRepository.getTimeTableGroup("211"))
            timeTableRepository.getSemInfo().let { semester ->
                if (semester is Resource.Success) {
                    semester.data?.let { data ->
                        _semesterInfoFlow.emit(semester)
                        _datesFlow.emit(generateTwoWeeks(data.isWeekUp))
                    }
                }
            }
        }
    }

    private fun generateTwoWeeks(weekUp: Boolean): List<DataTimeWithDifferentWeek> {
        val localDateTime = DataTime.now()
        var instant = Clock.System.now()
        instant = instant.minus(
                localDateTime.dayOfWeek,
                DateTimeUnit.DAY,
                TimeZone.currentSystemDefault()
            )

        return List(14) {
            instant = instant.plus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
            DataTimeWithDifferentWeek(
                instant.toLocalDateTime(TimeZone.currentSystemDefault()),
                if (it < 7)
                    weekUp
                else
                    !weekUp
            )
        }
    }

    fun getTime(lessonTime: Int) = timeTableRepository.getTime(lessonTime)

    fun getCurrentDay() = DataTime.now()

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}