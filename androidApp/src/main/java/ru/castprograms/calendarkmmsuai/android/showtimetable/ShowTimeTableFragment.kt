package ru.castprograms.calendarkmmsuai.android.showtimetable

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import biweekly.Biweekly
import biweekly.ICalendar
import biweekly.component.VEvent
import biweekly.property.*
import biweekly.util.DateTimeComponents
import biweekly.util.ICalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import ru.castprograms.calendarkmmsuai.TimeTableViewModel
import ru.castprograms.calendarkmmsuai.android.R
import ru.castprograms.calendarkmmsuai.android.databinding.FragmentCalendarBinding
import ru.castprograms.calendarkmmsuai.android.showtimetable.SnapOnScrollListener.Companion.NOTIFY_ON_SCROLL
import ru.castprograms.calendarkmmsuai.data.Lesson
import ru.castprograms.calendarkmmsuai.data.time.DataTime
import ru.castprograms.calendarkmmsuai.data.time.DataTimeWithDifferentWeek
import ru.castprograms.calendarkmmsuai.util.Resource
import java.io.File

class ShowTimeTableFragment : Fragment(R.layout.fragment_calendar),
    DatesAdapter.OnDateItemClickListener {

    private var currentIndex = -1
    private val viewModel: TimeTableViewModel by inject()
    private val adapter = TimeTableAdapter { viewModel.getTime(it) }
    private val linearLayoutManager by lazy {
        LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
    }

    private val datesAdapter by lazy {
        DatesAdapter(
            linearLayoutManager,
            onDateItemClickListener = this
        ){ setCurrentDay() }
    }
    lateinit var binding: FragmentCalendarBinding

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        requireActivity().actionBar?.title = "Расписание"
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCalendarBinding.bind(view)
        setCurrentDayData()
        setHasOptionsMenu(true)

        binding.recyclerDates.layoutManager = linearLayoutManager
        binding.recyclerDates.adapter = datesAdapter
        binding.recyclerEvents.adapter = adapter
        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerEvents)

        binding.recyclerEvents.addOnScrollListener(
            SnapOnScrollListener(
                snapHelper,
                NOTIFY_ON_SCROLL
            ) { position ->
//                binding.recyclerDates.smoothScrollToPosition(position)
                changeCurrentDay(position)
            }
        )

        binding.buttonToday.setOnClickListener {
            val date = viewModel.getCurrentDay()
            val position = datesAdapter.dates.indexOfFirst {
                it.dayOfMouth == date.dayOfMouth
                        && it.mouth == date.mouth
                        && it.year == date.year
            }
            changeCurrentDay(position)
            binding.recyclerEvents.smoothScrollToPosition(position)
        }

        setDataAdapters()
    }

    private fun setCurrentDayData() {
        viewModel.getCurrentDay().let {
            binding.textDay.text = it.dayOfMouth.toString()
            binding.textDayWeek.text = it.getDayOfWeekText()
            binding.textMouthAndYear.text = it.getMouthAndYear()
        }
    }

    private fun setCurrentDay() {
        val date = viewModel.getCurrentDay()
        datesAdapter.dates.indexOfFirst {
            it.dayOfMouth == date.dayOfMouth && it.mouth == date.mouth && it.year == date.year
        }.let {
            println(it)
            binding.recyclerDates.scrollToPosition(it)
//            datesAdapter.setCurrentItem(it)
        }
    }

    private fun setDataAdapters() {
        MainScope().launch(Dispatchers.IO) {
            launch {
                viewModel.timeTableGroupFlow.collectLatest {
                    when (it) {
                        is Resource.Success -> {
                            requireActivity().runOnUiThread {
                                adapter.setNewLessons(it.data ?: mapOf())
                            }
                        }
                        is Resource.Loading -> {}
                        is Resource.Error -> {
                            println(it.message)
                        }
                    }
                }
            }

            viewModel.datesFlow.collectLatest { dates ->
                requireActivity().runOnUiThread {
                    datesAdapter.setNewDates(dates)
                    adapter.setNewDates(dates)
                }
            }
        }
    }

    override fun onItemClick(position: Int, day: Int, week: Int) {
        binding.recyclerEvents.smoothScrollToPosition(position)
    }

    private fun changeCurrentDay(position: Int) {
        currentIndex = position
//        datesAdapter.setCurrentItem(position)
        binding.recyclerDates.layoutManager?.startSmoothScroll(
            CenterSmoothScroller(requireContext()).apply {
                targetPosition = position
            }
        )
    }

    private fun createIcs(
        dates: List<DataTimeWithDifferentWeek>,
        lessons: Map<Int, List<Lesson>>
    ): File {
        val calendar = ICalendar()
        dates.forEach { date ->
            lessons[date.getTypeWeek() * 10 + date.dayOfWeek]?.forEach { lesson ->
                val event = VEvent()
                viewModel.getTime(lesson.less).split('-').let {
                    val timeStart = it[0].split(':')
                    val timeEnd = it[1].split(":")
                    println(timeStart)
                    println(timeEnd)
                    event.dateStart = DateStart(
                        ICalDate(
                            DateTimeComponents(
                                date.year,
                                date.mouth,
                                date.dayOfMouth,
                                timeStart[0].toInt(),
                                timeStart[1].toInt(),
                                0,
                                false
                            ), true
                        )
                    )

                    event.dateEnd = DateEnd(
                        ICalDate(
                            DateTimeComponents(
                                date.year,
                                date.mouth,
                                date.dayOfMouth,
                                timeEnd[0].toInt(),
                                timeEnd[1].toInt(),
                                0,
                                false
                            ), true
                        )
                    )
                }
                event.summary = Summary("${lesson.disc}: ${convertText(lesson.type)}")
                event.description =
                    Description("${lesson.build}, ${lesson.rooms}" + "\n" + "Преподаватель: ${lesson.prepsText}")
                calendar.addEvent(event)
            }
        }

        val file = File(requireContext().filesDir.path + "/time_table.ics")
        file.delete()
        Biweekly.write(calendar).go(file)

        return file
    }

    private fun shareIcs(fileIcs: File) {
        val share = Intent(Intent.ACTION_SEND)
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        share.type = "*/*"
        share.putExtra(Intent.EXTRA_TEXT, "Моё расписание:")
        share.putExtra(
            Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                requireContext(),
                requireContext().applicationContext.packageName + ".provider",
                fileIcs
            )
        )

        startActivity(Intent.createChooser(share, "Поделиться"))
    }

    private fun exportIcs(fileIcs: File) {
        val share = Intent(Intent.ACTION_VIEW)
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        share.setDataAndType(
            FileProvider.getUriForFile(
                requireContext(),
                requireContext().applicationContext.packageName + ".provider",
                fileIcs
            ),
            "application/ics"
        )

        startActivity(share)
    }

    private fun convertText(type: String) = when (type) {
        "Л" -> "Лекция"
        "ПР" -> "Практика"
        "ЛР" -> "Лабараторная работа"
        else -> type
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.export_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.share -> {
                if (adapter.dates.isNotEmpty() && adapter.lessons.isNotEmpty())
                    shareIcs(createIcs(adapter.dates, adapter.lessons))
            }

            R.id.export -> {
                if (adapter.dates.isNotEmpty() && adapter.lessons.isNotEmpty())
                    exportIcs(createIcs(adapter.dates, adapter.lessons))
            }
        }
        return true
    }
}