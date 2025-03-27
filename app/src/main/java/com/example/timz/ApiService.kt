package com.example.timz

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

data class ScheduleCallback(val id: Int, val teacher: String, val filiere: String, val day: String, val hour: String, val anne: Int, val salle: String)
data class Teacher(val id: Int, val name: String)
data class Salle(val id: Int, val name: String)
data class Filiere(val id: Int, val name: String,val anne: Int)
data class TimeOfDay(var start: String, var end: String)
data class Schedule(val teacher: String, val filiere: String, val days: MutableMap<String, MutableList<String>>)
data class Database(val teachers: List<Teacher>, val salles: List<Salle>, val filieres: List<Filiere>, val schedules: List<ScheduleCallback>)




interface ApiService {
    @FormUrlEncoded
    @POST("addShudel.php")
    fun setshudle(
        @Field("teacher") teacher: String,
        @Field("filiere") filiere: String,
        @Field("day") day: String,
        @Field("hour") hour: String,
        @Field("anne") anne: Int?,
        @Field("salle") salle: String
    ): Call<ResponseBody>
    @POST("removeData.php")
    fun resetData(): Call<ResponseBody>
    @GET("teachers.php")
    fun getTeachers(): Call<Teacher>
    @GET("filieres.php")
    fun getFilieres(): Call<List<Filiere>>
    @GET("salles.php")
    fun getSalles(): Call<Salle>
    @GET("database.php")
    fun getDatabase(): Call<Database>
}