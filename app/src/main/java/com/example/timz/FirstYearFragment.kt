package com.example.timz

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FirstYearFragment : Fragment() {

    private var allSceances: List<ScheduleCallback> = listOf()
    private lateinit var recyclerView: RecyclerView
    private lateinit var FAB: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_first_year, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.all_Schedule)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
       
        getData()
    }

    private fun getData() {
        RetrofitClient.instance.getDatabase().enqueue(object : Callback<Database> {
            override fun onResponse(call: Call<Database>, response: Response<Database>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        allSceances = it.schedules
                        val fYear = allSceances.filter { it.anne == 1 }.toMutableList()
                        recyclerView.adapter = FilieresAdapter(fYear, requireContext())
                    }
                }
            }

            override fun onFailure(call: Call<Database>, t: Throwable) {
                Log.i("INFO", "onFailure: Connection failed")
            }
        })
    }
}