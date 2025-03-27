package com.example.timz

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SecondYearFragment : Fragment() {

    private var allSceances: List<ScheduleCallback> = listOf()
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second_year, container, false)
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

                        val sYear = allSceances.filter { it.anne == 2 }

                        recyclerView.adapter = FilieresAdapter(sYear.toMutableList(), requireContext())
                    }
                }
            }

            override fun onFailure(call: Call<Database>, t: Throwable) {
                Log.i("INFO", "onFailure: Connection failed")
            }
        })
    }
}