package com.vadimdorofeev.orthodoxlentendays

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ScheduleAdapter(private val items: List<ScheduleItem>) :
    RecyclerView.Adapter<ScheduleAdapter.ScheduleItemHolder>() {

    companion object {
        const val ITEM_KIND_MONTH      = 0 // Название месяца
        const val ITEM_KIND_EVENT      = 1 // Событие
        const val ITEM_KIND_TODAY_LINE = 2 // Сегодняшний день
    }

    class ScheduleItemHolder(itemView: View, val viewType: Int) : RecyclerView.ViewHolder(itemView) {
        lateinit var tvTitle: TextView
        lateinit var tvDay: TextView
        lateinit var tvDaysTo: TextView

        init {
            when (viewType) {
                ITEM_KIND_EVENT -> {
                    tvTitle = itemView.findViewById(R.id.title)
                    tvDay = itemView.findViewById(R.id.day)
                    tvDaysTo = itemView.findViewById(R.id.days_to)
                }
                else -> {
                    tvTitle = itemView as TextView
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleItemHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(
            when (viewType) {
                ITEM_KIND_MONTH -> R.layout.schedule_item_month
                ITEM_KIND_EVENT -> R.layout.schedule_item
                else -> R.layout.schedule_item_today
            },
            parent, false
        )
        return ScheduleItemHolder(view, viewType)
    }

    // Генерация фона со скруглёнными углами и заданными цветами фона и рамки
    private fun getRoundedBg(bgColor: Int, borderColor: Int = 0): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setStroke(5, borderColor)
            setColor(bgColor)
            cornerRadius = 15f
        }
    }

    override fun onBindViewHolder(holder: ScheduleItemHolder, position: Int) {
        when (holder.viewType) {
            ITEM_KIND_MONTH -> {
                holder.tvTitle.text = items[position].title
                holder.tvTitle.background = getRoundedBg(items[position].color)
            }
            ITEM_KIND_EVENT -> {
                holder.tvTitle.text = items[position].title
                holder.tvDay.text = items[position].day
                holder.tvDay.setTextColor(if (items[position].isFasting)
                    Common.colorTextFasting
                else
                    Common.colorTextNonFasting)
                holder.tvDaysTo.text = items[position].offset
                if (items[position].tense == 0) // Сегодняшнее событие
                    holder.itemView.background = getRoundedBg(
                        Common.colorTodayBg, Common.colorTodayBorder)
                else
                    holder.itemView.background = null
            }
            else -> { // Сегодняшний день, без события
                holder.tvTitle.text = items[position].title
                holder.tvTitle.background = getRoundedBg(
                    Common.colorTodayBg, Common.colorTodayBorder)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position].kind) {
            ScheduleItemKind.MonthName -> ITEM_KIND_MONTH
            ScheduleItemKind.Event -> ITEM_KIND_EVENT
            else -> ITEM_KIND_TODAY_LINE
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}