package ru.castprograms.calendarkmmsuai.android.showtimetable

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.castprograms.calendarkmmsuai.android.R
import ru.castprograms.calendarkmmsuai.android.databinding.ItemDateBinding
import ru.castprograms.calendarkmmsuai.data.Lesson
import ru.castprograms.calendarkmmsuai.data.time.DataTimeWithDifferentWeek

class DatesAdapter(
    private val layoutManager: LinearLayoutManager,
    val onDateItemClickListener: OnDateItemClickListener,
    var dates: List<DataTimeWithDifferentWeek> = listOf(),
    private val setCurrentDay: () -> Unit
) : RecyclerView.Adapter<DatesAdapter.DatesViewHolder>() {
    private var currentPosition = -1

    fun setNewDates(newDates: List<DataTimeWithDifferentWeek>) {
        val diff = object : DiffUtil.Callback() {
            override fun getOldListSize() = dates.size
            override fun getNewListSize() = newDates.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                dates[oldItemPosition] == newDates[newItemPosition]

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                dates[oldItemPosition] == newDates[newItemPosition]
        }
        DiffUtil.calculateDiff(diff).dispatchUpdatesTo(this)
        dates = newDates
        setCurrentDay()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatesViewHolder {
        return DatesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_date, parent, false)
        ).apply {
            val width = parent.measuredWidth / 7
            val view = this.itemView
            view.layoutParams.width = width
            setIsRecyclable(false)
        }
    }

    override fun onBindViewHolder(holder: DatesViewHolder, position: Int) {
        holder.bind(dates[position], position)
    }

    override fun getItemCount() = dates.size

    interface OnDateItemClickListener {
        fun onItemClick(position: Int, day: Int, week: Int)
    }

    inner class DatesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemDateBinding.bind(view)

        fun bind(date: DataTimeWithDifferentWeek, position: Int) {
            binding.textDayNumber.text = date.dayOfMouth.toString()
            binding.textDaySymbol.text = date.getShortcutDayOfWeek()
            if (date.isRed)
                binding.imageTurned.backgroundTintList = ColorStateList.valueOf(Color.RED)
            else
                binding.imageTurned.backgroundTintList = ColorStateList.valueOf(Color.BLUE)

//            if (position == currentPosition){
//                binding.containerDate.setBackgroundResource(R.drawable.background_current_day)
//                binding.textDayNumber.setTextColor(Color.WHITE)
//                binding.textDaySymbol.setTextColor(Color.WHITE)
//            }

            binding.root.setOnClickListener {
                onDateItemClickListener.onItemClick(position, date.dayOfMouth, if (date.isRed) 1 else 2)
//                setCurrentItem(position)
            }
        }
    }

    private fun select(position: Int) {
        if (currentPosition >= 0) {
            deselect(currentPosition)
        }
        val targetView = getViewByPosition(position)
        if (targetView != null) {
            // change the appearance
            val binding = ItemDateBinding.bind(targetView)
            binding.containerDate.setBackgroundResource(R.drawable.background_current_day)
            binding.textDayNumber.setTextColor(Color.WHITE)
            binding.textDaySymbol.setTextColor(Color.WHITE)
            binding.containerDate.cardElevation = 20f
        }
        currentPosition = position
    }


    private fun deselect(position: Int) {
        if (getViewByPosition(position) != null) {
            val targetView = getViewByPosition(position)
            if (targetView != null) {
                // change the appearance
                val binding = ItemDateBinding.bind(targetView)
                binding.containerDate.setBackgroundResource(R.drawable.background_item_date)
                binding.textDayNumber.setTextColor(Color.parseColor("#212525"))
                binding.textDaySymbol.setTextColor(Color.parseColor("#BCC1CD"))
                binding.containerDate.cardElevation = 2f
            }
        }
        currentPosition = -1
    }

    fun setCurrentItem(position: Int){
        select(position)
    }

    private fun getViewByPosition(pos: Int): View? {
        val firstListItemPosition: Int = layoutManager.findFirstVisibleItemPosition()
        val lastListItemPosition: Int = firstListItemPosition + layoutManager.childCount - 1
        return if (pos < firstListItemPosition || pos > lastListItemPosition) {
            null
        } else {
            val childIndex = pos - firstListItemPosition
            layoutManager.getChildAt(childIndex)
        }
    }
}