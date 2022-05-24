package ru.castprograms.calendarkmmsuai.android.showtimetable

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.castprograms.calendarkmmsuai.android.R
import ru.castprograms.calendarkmmsuai.android.databinding.ItemTimetableBinding
import ru.castprograms.calendarkmmsuai.data.Lesson
import ru.castprograms.calendarkmmsuai.data.time.DataTimeWithDifferentWeek

class TimeTableAdapter(
    var dates: List<DataTimeWithDifferentWeek> = listOf(),
    var lessons: Map<Int, List<Lesson>> = mapOf(),
    private val getTime: (Int) -> String
) : RecyclerView.Adapter<TimeTableAdapter.TimeTableViewHolder>() {
    private val viewPool = RecyclerView.RecycledViewPool()

    fun setNewLessons(newLessons: Map<Int, List<Lesson>>) {
        val diff = object : DiffUtil.Callback() {
            override fun getOldListSize() = lessons.size
            override fun getNewListSize() = newLessons.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                lessons.toList()[oldItemPosition].first == newLessons.toList()[newItemPosition].first

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                lessons.toList()[oldItemPosition].second == newLessons.toList()[newItemPosition].second
        }
        if (dates.isNotEmpty()) {
            DiffUtil.calculateDiff(diff).dispatchUpdatesTo(this)
        }
        lessons = newLessons
    }

    fun setNewDates(newDates: List<DataTimeWithDifferentWeek>) {
        val diff = object : DiffUtil.Callback() {
            override fun getOldListSize() = dates.size
            override fun getNewListSize() = newDates.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                dates[oldItemPosition] == newDates[newItemPosition]

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                dates[oldItemPosition] == newDates[newItemPosition]
        }
        if (lessons.isNotEmpty()) {
            DiffUtil.calculateDiff(diff).dispatchUpdatesTo(this)
        }
        dates = newDates
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeTableViewHolder {
        return TimeTableViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_timetable, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TimeTableViewHolder, position: Int) {
        holder.bind(dates[position])
    }

    override fun getItemCount() = dates.size

    inner class TimeTableViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemTimetableBinding.bind(view)

        fun bind(date: DataTimeWithDifferentWeek) {
            val currentLessons = lessons[date.getTypeWeek() * 10 + date.dayOfWeek]
            print(date.getTypeWeek() * 10 + date.dayOfWeek)
            println(": $currentLessons")
            if (currentLessons != null) {
                binding.viewStubNoLessons.visibility = View.GONE
                binding.recyclerLessons.let {
                    it.visibility = View.VISIBLE
                    it.setHasFixedSize(true)
                    val adapter = LessonsAdapter { getTime(it) }
                    it.adapter = adapter
                    it.setRecycledViewPool(viewPool)
                    (it.layoutManager as? LinearLayoutManager)
                        ?.initialPrefetchItemCount = 4
                    adapter.setNewLessons(currentLessons)
                }
            } else {
                binding.recyclerLessons.visibility = View.GONE
                if (binding.viewStubNoLessons.parent != null)
                    binding.viewStubNoLessons.inflate()
                else
                    binding.viewStubNoLessons.visibility = View.VISIBLE
            }
        }
    }
}
