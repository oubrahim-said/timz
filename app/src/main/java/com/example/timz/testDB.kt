package com.example.timz

import android.content.Context
import android.util.Log
import android.widget.ArrayAdapter
import androidx.work.Worker
import androidx.work.WorkerParameters
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyWorker(context: Context, workerParameters: WorkerParameters): Worker(context, workerParameters){
    override fun doWork(): Result {
        return Result.success()
    }
}

fun main(){

    println("*")

    RetrofitClient.instance.getDatabase().enqueue(object: Callback<Database> {
        override fun onResponse(call: Call<Database>, response: Response<Database>){
            println("**")
            if(response.isSuccessful) {
                println("***")
                response.body().let {
                    println("****")
                    val f = it?.filieres
                    println(f)
                }
            }
        }
        override fun onFailure(call: Call<Database>, t:Throwable){
            Log.i("TAG", "onFailure: $t")
        }
    })
}