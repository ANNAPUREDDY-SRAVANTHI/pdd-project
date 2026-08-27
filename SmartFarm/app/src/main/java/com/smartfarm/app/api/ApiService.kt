package com.smartfarm.app.api

import retrofit2.Response
import retrofit2.http.*
import com.google.gson.annotations.SerializedName

interface ApiService {
    @POST("api/register/")
    suspend fun register(@Body data: Map<String, String>): Response<Map<String, String>>

    @POST("api-token-auth/")
    suspend fun login(@Body credentials: Map<String, String>): Response<Map<String, String>>

    @POST("api/forgot-password/")
    suspend fun forgotPassword(@Body data: Map<String, String>): Response<Map<String, String>>

    @GET("api/workers/")
    suspend fun getWorkers(): Response<List<Worker>>

    @POST("api/workers/")
    suspend fun addWorker(@Body worker: Worker): Response<Worker>

    @PUT("api/workers/{id}/")
    suspend fun updateWorker(@Path("id") id: Int, @Body worker: Worker): Response<Worker>

    @DELETE("api/workers/{id}/")
    suspend fun deleteWorker(@Path("id") id: Int): Response<Unit>

    @GET("api/attendance/")
    suspend fun getAllAttendance(): Response<List<AttendanceRecord>>

    @POST("api/attendance/")
    suspend fun submitAttendance(@Body record: AttendanceRecord): Response<AttendanceRecord>

    @GET("api/payments/")
    suspend fun getAllPayments(): Response<List<PaymentRecord>>

    @POST("api/payments/")
    suspend fun addPayment(@Body record: PaymentRecord): Response<PaymentRecord>

    @GET("api/tasks/")
    suspend fun getTasks(): Response<List<TaskRecord>>

    @POST("api/tasks/")
    suspend fun addTask(@Body record: TaskRecord): Response<TaskRecord>

    @PATCH("api/tasks/{id}/")
    suspend fun updateTask(@Path("id") id: Int, @Body data: Map<String, Boolean>): Response<TaskRecord>

    @GET("api/notifications/")
    suspend fun getNotifications(): Response<List<NotificationRecord>>

    @POST("api/notifications/")
    suspend fun addNotification(@Body record: NotificationRecord): Response<NotificationRecord>
}

// --- DATA MODELS ---
data class Worker(
    val id: Int = 0,
    val name: String,
    val phone: String,
    val address: String,
    @SerializedName("daily_wage") val dailyWage: Double,
    val role: String,
    @SerializedName("join_date") val joinDate: String = ""
)

data class AttendanceRecord(val id: Int = 0, val worker: Int, val date: String, val status: String)
data class PaymentRecord(val id: Int = 0, val worker: Int, val amount: Double, val date: String = "", val description: String = "Salary Payment")
data class TaskRecord(val id: Int = 0, val worker: Int, val description: String, @SerializedName("is_completed") val isCompleted: Boolean = false, val date: String = "")
data class NotificationRecord(val id: Int = 0, val title: String, val message: String, @SerializedName("created_at") val createdAt: String = "")