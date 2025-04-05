package com.example.timz

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FirstPage : AppCompatActivity() {

    lateinit var FormateurSpinner: Spinner
    lateinit var DaySpinner: Spinner
    lateinit var SalleSpinner: Spinner
    lateinit var daysOfWeek: ArrayList<String>
    lateinit var buttonAddSceances: Button
    lateinit var tableLayout: TableLayout
    lateinit var TimesPeriod: ArrayList<TimeOfDay>
    val selectedPeriods = mutableMapOf<String, MutableList<String>>()
    var filieres: List<Filiere> = listOf()
    var salles: List<Salle> = listOf()
    var teachers: List<Teacher> = listOf()
    private val selectedFilieres = mutableMapOf<String, MutableList<String>>()
    lateinit var timeSlot: MutableList<String>
    private var teacherNameSelected = ""
    private val schedules = mutableListOf<Schedule>()
    lateinit var buttonResetSchedule: Button
    lateinit var buttonGenerateSchedule: Button



    val TAG = "CHECK_RESPONSE"
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_first_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        getDatabase()


        val nestedScrollView = findViewById<NestedScrollView>(R.id.nestedScrollView)
        nestedScrollView.isNestedScrollingEnabled = true

        buttonResetSchedule = findViewById(R.id.recreateSchedul)
        SalleSpinner = findViewById(R.id.salle_spinner)
        buttonGenerateSchedule = findViewById(R.id.generate_the_schedule)
        FormateurSpinner = findViewById(R.id.formateur_spinner)
        DaySpinner = findViewById(R.id.day_spinner)
        tableLayout = findViewById(R.id.table_layout)
        buttonAddSceances = findViewById(R.id.buttonAddSeances)
        daysOfWeek = arrayListOf("Selectionner un jour :", "LUNDI", "MARDI", "MERCREDI", "JEUDI", "VENDREDI", "SAMEDI")
        TimesPeriod = arrayListOf(TimeOfDay("8:30","11:00"),TimeOfDay("11:00","13:30"),TimeOfDay("13:30","16:00"),TimeOfDay("16:00","18:30"))
        timeSlot = mutableListOf("8:30 - 11:00", "11:00 - 13:30", "13:30 - 16:00", "16:00 - 18:30")

        val DaysAdapter = object : ArrayAdapter<String>(this@FirstPage,android.R.layout.simple_spinner_dropdown_item,daysOfWeek){
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }
        }
        DaysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        DaySpinner.adapter = DaysAdapter //Spinner de days
        DaySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @SuppressLint("SetTextI18n")
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    showSceanceDialog(daysOfWeek[position])
                }
                DaySpinner.setSelection(0)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        var insertedSeances = mutableListOf<MutableList<String>>()
        buttonAddSceances.setOnClickListener {
            schedules.forEach { schedule ->
                schedule.days.forEach { (day, times) ->
                    times.forEach { time ->
                        val selectedSalle = SalleSpinner.selectedItem.toString()
                        if (selectedSalle != "Sélectionner une Salle") {
                            // Use selected salle directly
                            val salle = selectedSalle
                            val an = filieres.find { it.name == schedule.filiere }?.anne ?: 1
                            val key = mutableListOf(schedule.teacher, schedule.filiere, day, time, salle)
                            val dejaExiste = insertedSeances.find { it[0] == schedule.teacher && it[1] == schedule.filiere && it[2] == day && it[3] == time }
                            if (dejaExiste == null) {
                                addSchudleOfFormateur(schedule.teacher, schedule.filiere, day, time, an, salle)
                                ajouterSeancesAuTableau(schedule.teacher, schedule.filiere, day, time, salle)
                                insertedSeances.add(key) // Mark as processed
                            }
                        } else {
                            Toast.makeText(this@FirstPage, "Veuillez sélectionner une salle!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            Toast.makeText(this@FirstPage, "Scéances ajoutées: ${schedules.size} seances", Toast.LENGTH_LONG).show()
        }
        // Button for add info to table and send it to database
        buttonResetSchedule.setOnClickListener {
            AlertDialog.Builder(this@FirstPage)
                .setTitle("Alerte")
                .setMessage("êtes-vous sûr de vouloir supprimer tous les scéances que vous avez enregistrés :")
                .setPositiveButton("Oui"){_,_ ->

                    tableLayout.removeViews(1, tableLayout.childCount-1)
                    schedules.clear()
                    RetrofitClient.instance.resetData().enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@FirstPage, "Tous les scéances ont été supprimés avec succès", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@FirstPage, "Échec de la suppression des scéances", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Toast.makeText(this@FirstPage, "Erreur: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                .setNegativeButton("Non", null)
                .show()
        }
        buttonGenerateSchedule.setOnClickListener {
            startActivity(Intent(this@FirstPage, MainActivity::class.java))
        }

    }
    private fun getDatabase(){
        RetrofitClient.instance.getDatabase().enqueue(object: Callback<Database> {
            override fun onResponse(call: Call<Database>, response: Response<Database>){
                if(response.isSuccessful){
                    response.body()?.let {
                        teachers = it.teachers ?: emptyList()
                        salles = it.salles ?: emptyList()
                        filieres = it.filieres ?: emptyList()



                        val teachersName: ArrayList<String> = arrayListOf("Selectionner un Formateur:")
                        teachers?.forEach { teachersName.add(it.name) }
                        val FormateurAdapter = object : ArrayAdapter<String>(this@FirstPage,android.R.layout.simple_spinner_dropdown_item,teachersName) {
                            override fun isEnabled(position: Int): Boolean {
                                return position != 0
                            }
                        }
                        FormateurAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        FormateurSpinner.adapter = FormateurAdapter

                        val filieresName = arrayListOf<String>()
                        filieres.forEach { filieresName.add(it.name) }
                        FormateurSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            @SuppressLint("SetTextI18n")
                            override fun onItemSelected(
                                parent: AdapterView<*>,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                if (position != 0) {
                                    teacherNameSelected = teachersName[position]
                                    showTeacherDialog(filieresName, teacherNameSelected)
                                }
                                FormateurSpinner.setSelection(0)
                            }
                            override fun onNothingSelected(parent: AdapterView<*>) {}
                        }

                        // Populate the salles spinner
                        val salleNames = arrayListOf("Sélectionner une Salle")
                        salles.forEach { salle -> salleNames.add(salle.name) }

                        val salleAdapter = ArrayAdapter<String>(this@FirstPage, android.R.layout.simple_spinner_dropdown_item, salleNames)
                        SalleSpinner.adapter = salleAdapter

                        // Setup SalleSpinner item selected listener
                        SalleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                                if (position != 0) {
                                    // When a salle is selected
                                    val selectedSalle = salles[position - 1] // Adjusting for the "Sélectionner une Salle" placeholder
                                    // You can use this salle in your logic if needed
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>) {}
                        }
                    }
                }
            }

            override fun onFailure(call: Call<Database>, t: Throwable){
                Log.i(TAG, "onFailure: $t")
            }
        })
    }
    // get data from database
    private fun addSchudleOfFormateur(teacher: String, filier: String, day:   String, time: String, anne: Int?, salle: String){
        RetrofitClient.instance.setshudle(teacher, filier, day, time, anne, salle).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@FirstPage, "Scèance ajouté avec succés!", Toast.LENGTH_SHORT).show()

                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@FirstPage, "Erreur: Scèance non ajouté.", Toast.LENGTH_SHORT).show()
                Log.e("MainActivity", "Error adding user: ${t.message}")
            }
        })
    }
    @SuppressLint("SetTextI18n")
    private fun showTeacherDialog(filieres: List<String>, teacherName: String) {
        var selectedPosition = -1 // Stores the selected position

        AlertDialog.Builder(this)
            .setTitle("Selectionner un groupe")
            .setSingleChoiceItems(filieres.toTypedArray(), -1) { _, index ->
                selectedPosition = index
            }
            .setPositiveButton("OK") { _, _ ->
                if (selectedPosition != -1) {
                    findViewById<TextView>(R.id.selected_teacher_filiere).setText("$teacherName : ${filieres[selectedPosition]}")
                    selectedFilieres[teacherName] = mutableListOf(filieres[selectedPosition])
                } else {
                    Toast.makeText(this, "Pas de filiere", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }
    private fun showSceanceDialog(day: String) {
        if (teacherNameSelected.isEmpty()) {
            Toast.makeText(this, "Svp selectionner un formateur!", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedFiliere = selectedFilieres[teacherNameSelected]?.firstOrNull()
        if (selectedFiliere == null) {
            Toast.makeText(this, "Pas de filiere selectionné !", Toast.LENGTH_SHORT).show()
            return
        }

        // Find or create the schedule for this teacher & filiere
        var schedule = schedules.find { it.teacher == teacherNameSelected && it.filiere == selectedFiliere }
        if (schedule == null) {
            schedule = Schedule(teacherNameSelected, selectedFiliere, mutableMapOf())
            schedules.add(schedule)
        }

        // Get or create the list of already selected time slots for this teacher & filiere on the given day
        val alreadySelectedSlots = schedule.days.getOrPut(day) { mutableListOf() }

        // Collect all time slots that have been selected by either the same TEACHER or the same FILIERE on this day
        val occupiedSlots = mutableSetOf<String>()

        schedules.forEach { sch ->
            if (sch.teacher == teacherNameSelected || sch.filiere == selectedFiliere) {
                sch.days[day]?.let { occupiedSlots.addAll(it) }
            }
        }

        // Filter available slots by removing occupied ones
        val availableTimeSlots = timeSlot.filterNot { occupiedSlots.contains(it) || occupiedSlots.count {v-> v == it } > 11}

        if (availableTimeSlots.isEmpty()) {
            Toast.makeText(this, "Suffisant de scéance pour $day!", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedItems = BooleanArray(availableTimeSlots.size)

        AlertDialog.Builder(this)
            .setTitle("Selectionner des scéances pour ${day.lowercase()}")
            .setMultiChoiceItems(availableTimeSlots.toTypedArray(), selectedItems) { _, index, isChecked ->
                if (isChecked) {
                    alreadySelectedSlots.add(availableTimeSlots[index])
                } else {
                    alreadySelectedSlots.remove(availableTimeSlots[index])
                }
            }
            .setPositiveButton("OK") { _, _ ->
                // Save selected slots in the schedule
                schedule.days[day] = alreadySelectedSlots.toMutableList()
                Toast.makeText(this, "Selectionner: $alreadySelectedSlots", Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("Annuler", null)
            .show()
    }
    private fun ajouterSeancesAuTableau(formateur: String, filiere: String, jour: String, heure: String, salle: String) {
        val row = TableRow(this)

        val formateurTextView = TextView(this)
        formateurTextView.text = formateur
        formateurTextView.setPadding(8, 8, 8, 8)
        formateurTextView.setBackgroundColor(Color.LTGRAY)


        val jourTextView = TextView(this)
        jourTextView.text = jour
        jourTextView.setPadding(8, 8, 8, 8)
        jourTextView.setBackgroundColor(Color.LTGRAY)


        val filiereTextView = TextView(this)
        filiereTextView.text = filiere
        filiereTextView.setPadding(8, 8, 8, 8)
        filiereTextView.setBackgroundColor(Color.WHITE)


        val heureTextView = TextView(this)
        heureTextView.text = heure
        heureTextView.setPadding(8, 8, 8, 8)
        heureTextView.setBackgroundColor(Color.WHITE)

        val salleTextView = TextView(this)
        salleTextView.text = salle
        salleTextView.setPadding(8, 8, 8, 8)
        salleTextView.setBackgroundColor(Color.LTGRAY)


        row.addView(formateurTextView)
        row.addView(filiereTextView)
        row.addView(jourTextView)
        row.addView(heureTextView)
        row.addView(salleTextView)

        tableLayout.addView(row)
    }
    private fun assignSalleToSchedule(salles: List<Salle>, insertedSeances: MutableList<MutableList<String>>, day: String, t: String): String {
        val availableSalles = salles.filter { salle ->
            !insertedSeances.any { it[2] == day && it[3] == t && it[4] == salle.name }
        }
        if (timeSlot.indexOf(t) > 0){
            val studieSceanceBefore = insertedSeances.find {it[2] == day && it[0] == insertedSeances.lastOrNull()?.get(0)}

            if(studieSceanceBefore != null){
                return studieSceanceBefore[4]
            }
        }

        return availableSalles.randomOrNull()?.name ?: "No Available Salle"
    }


}