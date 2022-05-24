package ru.castprograms.calendarkmmsuai.android.showtimetable

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.castprograms.calendarkmmsuai.android.R
import ru.castprograms.calendarkmmsuai.android.databinding.ItemEventBinding
import ru.castprograms.calendarkmmsuai.android.databinding.ItemLessonBinding
import ru.castprograms.calendarkmmsuai.data.Lesson

class LessonsAdapter(
    var lessons: List<Lesson> = listOf(),
    private val getTime: (Int) -> String
) : RecyclerView.Adapter<LessonsAdapter.LessonsViewHolder>() {
    fun setNewLessons(newLessons: List<Lesson>) {
        val diff = object : DiffUtil.Callback() {
            override fun getOldListSize() = lessons.size
            override fun getNewListSize() = newLessons.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                lessons[oldItemPosition] == newLessons[newItemPosition]

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                lessons[oldItemPosition] == newLessons[newItemPosition]
        }
        DiffUtil.calculateDiff(diff).dispatchUpdatesTo(this)
        lessons = newLessons
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonsViewHolder {
        return LessonsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_event, parent, false)
        )
    }

    override fun onBindViewHolder(holder: LessonsViewHolder, position: Int) {
        holder.bind(lessons[position])
    }

    override fun getItemCount() = lessons.size

    inner class LessonsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemEventBinding.bind(view)

        fun bind(lesson: Lesson) {
            getTime(lesson.less).split('-').let {
                println(it)
                binding.textStartTime.text = it[0]
                binding.textEndTime.text = it[1]
            }

            binding.chipTypeLesson.text = lesson.type
            binding.textNameEvent.text = lesson.disc
            binding.textNameLeading.text = lesson.prepsText
            binding.textLocation.text = "${lesson.build}, ${lesson.rooms}"
        }
    }
}