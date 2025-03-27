package com.example.timz

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream


class TeachersFragment : Fragment() {

    private var allSceances: List<ScheduleCallback> = listOf()
    private lateinit var recyclerView: RecyclerView
    lateinit var adapter: ScheduleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_teachers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.all_Schedule)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        getData()
       /* view?.findViewById<FloatingActionButton>(R.id.telechargerCommePdf)?.setOnClickListener{
            generatePdfFromRecyclerView(recyclerView)
        }*/

    }

    private fun getData() {
        RetrofitClient.instance.getDatabase().enqueue(object : Callback<Database> {
            override fun onResponse(call: Call<Database>, response: Response<Database>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        allSceances = it.schedules ?: emptyList()
                        adapter = ScheduleAdapter(allSceances.toMutableList(), requireContext())
                        recyclerView.adapter = adapter

                    }
                }
            }

            override fun onFailure(call: Call<Database>, t: Throwable) {
                Log.i("INFO", "onFailure: Connection failed")
            }
        })
    }
    fun generatePdfFromRecyclerView(recyclerView: RecyclerView) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(recyclerView.width, recyclerView.height, 1).create()
        val page = pdfDocument.startPage(pageInfo)

        recyclerView.draw(page.canvas) // Draw RecyclerView onto the PDF

        pdfDocument.finishPage(page)

        val filePath = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Schedules.pdf")

        try {
            val fileOutputStream = FileOutputStream(filePath)
            pdfDocument.writeTo(fileOutputStream)
            pdfDocument.close()
            fileOutputStream.close()
            Toast.makeText(recyclerView.context, "PDF saved successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(recyclerView.context, "Failed to save PDF!", Toast.LENGTH_SHORT).show()
        }
    }
}
