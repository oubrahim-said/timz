package com.example.timz

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ScheduleAdapter(private val myList: MutableList<ScheduleCallback>, val context: Context): RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>()  {

    val teacherList = myList.groupBy { it.teacher }.toList()
    class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tableLayout: TableLayout = itemView.findViewById(R.id.scheduleTable)
        val textView: TextView = itemView.findViewById(R.id.name_of_teach)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.table_item_layout, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun getItemCount(): Int {
        return teacherList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val tableData = teacherList[position]
        holder.textView.setText("emploie de le formateur : ${teacherList[position].first}")
        generateSchedule(holder.tableLayout, teacherList[position].second)
    }
    fun createTextView(text: String, isHeader: Boolean): TextView {
        return TextView(context).apply {
            this.text = text
            textSize = if (isHeader) 14f else 12f
            height = 120
            width = 400
            gravity = Gravity.TOP or Gravity.CENTER
            setTypeface(null, if (isHeader) Typeface.BOLD else Typeface.NORMAL)
            setBackgroundResource(R.drawable.border_for_each_cell)
        }
    }
    fun generateSchedule(tableLayout: TableLayout, schedulesData: List<ScheduleCallback>){
        tableLayout.removeAllViews()

        val days = listOf("LUNDI", "MARDI", "MERCREDI", "JEUDI", "VENDREDI", "SAMEDI")
        val timeSlots = listOf("8:30 - 11:00", "11:00 - 13:30", "13:30 - 16:00", "16:00 - 18:30")

        val headerRow = TableRow(tableLayout.context)
        headerRow.addView(createTextView("", true))
        timeSlots.forEach { hour ->
            headerRow.addView(createTextView(hour, true))
        }
        tableLayout.addView(headerRow)

        days.forEach { day ->
            val row = TableRow(tableLayout.context)
            row.addView(createTextView(day, true))

            timeSlots.forEach { hour ->
                val cellText = getScheduleForTime(schedulesData, day, hour)
                val cell = createTextView(cellText, false)
                row.addView(cell)
            }
            tableLayout.addView(row)
        }
    }
    fun getScheduleForTime(list: List<ScheduleCallback>, day: String, hour: String): String{
        val entry = list.find { it.day == day && it.hour == hour }
        return if (entry != null) "${entry.filiere}\n${entry.salle}" else ""
    }
}

class FilieresAdapter(private val myList: MutableList<ScheduleCallback>, val context: Context): RecyclerView.Adapter<FilieresAdapter.FilieresViewHolder>() {

    val teacherList = myList.groupBy { it.filiere }.toList()

    class FilieresViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tableLayout: TableLayout = itemView.findViewById(R.id.scheduleTable)
        val textView: TextView = itemView.findViewById(R.id.name_of_teach)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilieresViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.table_item_layout, parent, false)
        return FilieresViewHolder(view)
    }
    override fun getItemCount(): Int {
        return teacherList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FilieresViewHolder, position: Int) {
        val tableData = teacherList[position]
        holder.textView.setText("emploie de le filiere : ${teacherList[position].first}")
        generateSchedule(holder.tableLayout, tableData.second)
    }
    fun createTextView(text: String, isHeader: Boolean): TextView {
        return TextView(context).apply {
            this.text = text
            textSize = if (isHeader) 14f else 12f
            height = 120
            width = 400
            gravity = Gravity.CENTER or Gravity.TOP
            setTypeface(null, if (isHeader) Typeface.BOLD else Typeface.NORMAL)
            setBackgroundResource(R.drawable.border_for_each_cell)

        }
    }

    fun generateSchedule(tableLayout: TableLayout, schedulesData: List<ScheduleCallback>) {
        tableLayout.removeAllViews()

        val days = listOf("LUNDI", "MARDI", "MERCREDI", "JEUDI", "VENDREDI", "SAMEDI")
        val timeSlots = listOf("8:30 - 11:00", "11:00 - 13:30", "13:30 - 16:00", "16:00 - 18:30")

        val headerRow = TableRow(tableLayout.context)
        headerRow.addView(createTextView("", true))
        timeSlots.forEach { hour ->
            headerRow.addView(createTextView(hour, true))
        }
        tableLayout.addView(headerRow)

        days.forEach { day ->
            val row = TableRow(tableLayout.context)
            row.addView(createTextView(day, true))

            timeSlots.forEach { hour ->
                val cellText = getScheduleForTime(schedulesData, day, hour)
                val cell = createTextView(cellText, false)
                row.addView(cell)
            }
            tableLayout.addView(row)
        }
    }

    fun getScheduleForTime(list: List<ScheduleCallback>, day: String, hour: String): String {
        val entry = list.find { it.day == day && it.hour == hour }
        return if (entry != null) "${entry.teacher}\n${entry.salle}" else ""
    }
}