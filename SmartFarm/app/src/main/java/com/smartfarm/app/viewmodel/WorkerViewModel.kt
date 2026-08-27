package com.smartfarm.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfarm.app.api.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class WorkerViewModel : ViewModel() {
    private val _allWorkers = MutableStateFlow<List<Worker>>(emptyList())
    val allWorkers = _allWorkers.asStateFlow()

    private val _attendanceHistory = MutableStateFlow<List<AttendanceRecord>>(emptyList())
    val attendanceHistory = _attendanceHistory.asStateFlow()

    private val _paymentHistory = MutableStateFlow<List<PaymentRecord>>(emptyList())
    val paymentHistory = _paymentHistory.asStateFlow()

    private val _taskList = MutableStateFlow<List<TaskRecord>>(emptyList())
    val taskList = _taskList.asStateFlow()

    private val _notifications = MutableStateFlow<List<NotificationRecord>>(emptyList())
    val notifications = _notifications.asStateFlow()

    private val _statusMessage = MutableSharedFlow<String>()
    val statusMessage = _statusMessage.asSharedFlow()

    private val _currentUsername = MutableStateFlow("")
    val currentUsername = _currentUsername.asStateFlow()

    val todayAttendance: StateFlow<Map<Int, String>> = _attendanceHistory.map { history ->
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        history.filter { it.date == today }.associate { it.worker to it.status }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())

    fun setLoggedInUser(name: String) { _currentUsername.value = name }

    init { refreshAll() }

    private fun showMsg(msg: String) { viewModelScope.launch { _statusMessage.emit(msg) } }

    fun refreshAll() {
        fetchWorkers()
        fetchAttendanceHistory()
        fetchPayments()
        fetchTasks()
        fetchNotifications()
    }

    // --- API CALLS ---
    fun fetchWorkers() = viewModelScope.launch {
        try {
            val res = RetrofitClient.instance.getWorkers()
            if (res.isSuccessful) _allWorkers.value = res.body() ?: emptyList()
        } catch (e: Exception) { Log.e("SmartFarm", "Error: ${e.message}") }
    }

    fun fetchAttendanceHistory() = viewModelScope.launch {
        try {
            val res = RetrofitClient.instance.getAllAttendance()
            if (res.isSuccessful) _attendanceHistory.value = res.body() ?: emptyList()
        } catch (e: Exception) { }
    }

    fun fetchPayments() = viewModelScope.launch {
        try {
            val res = RetrofitClient.instance.getAllPayments()
            if (res.isSuccessful) _paymentHistory.value = res.body() ?: emptyList()
        } catch (e: Exception) { }
    }

    fun fetchTasks() = viewModelScope.launch {
        try {
            val res = RetrofitClient.instance.getTasks()
            if (res.isSuccessful) _taskList.value = res.body() ?: emptyList()
        } catch (e: Exception) { }
    }

    fun fetchNotifications() = viewModelScope.launch {
        try {
            val res = RetrofitClient.instance.getNotifications()
            if (res.isSuccessful) _notifications.value = res.body() ?: emptyList()
        } catch (e: Exception) { }
    }

    // --- CRUD ---
    fun addWorker(worker: Worker) = viewModelScope.launch {
        if (RetrofitClient.instance.addWorker(worker).isSuccessful) {
            fetchWorkers()
            showMsg("Worker Saved! ✅")
        }
    }

    fun addTask(workerId: Int, description: String) = viewModelScope.launch {
        val record = TaskRecord(worker = workerId, description = description)
        if (RetrofitClient.instance.addTask(record).isSuccessful) {
            fetchTasks()
            showMsg("Task Assigned! ✅")
        }
    }

    fun completeTask(taskId: Int) = viewModelScope.launch {
        try {
            val res = RetrofitClient.instance.updateTask(taskId, mapOf("is_completed" to true))
            if (res.isSuccessful) {
                fetchTasks()
                showMsg("Task Completed! 🌟")
            }
        } catch (e: Exception) { }
    }

    fun submitAttendance(dateString: String, records: Map<Int, String>) = viewModelScope.launch {
        val djangoDate = try {
            val inputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            outputFormat.format(inputFormat.parse(dateString)!!)
        } catch (e: Exception) { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

        records.forEach { (id, stat) ->
            try { RetrofitClient.instance.submitAttendance(AttendanceRecord(worker = id, date = djangoDate, status = stat)) } catch (e: Exception) {}
        }
        fetchAttendanceHistory()
        showMsg("Attendance Submitted! ✅")
    }

    fun recordPayment(wid: Int, amt: Double) = viewModelScope.launch {
        if (RetrofitClient.instance.addPayment(PaymentRecord(worker = wid, amount = amt)).isSuccessful) {
            fetchPayments()
            showMsg("Payment Recorded! 💰")
        }
    }

    fun resetPassword(user: String, pass: String) = viewModelScope.launch {
        try {
            RetrofitClient.instance.forgotPassword(mapOf("username" to user, "password" to pass))
            showMsg("Password Updated!")
        } catch (e: Exception) { }
    }

    // --- CALCULATIONS ---
    fun getPresentDaysCount(id: Int): Int = _attendanceHistory.value.count { it.worker == id && it.status == "Present" }
    fun getTotalEarned(id: Int): Double {
        val w = _allWorkers.value.find { it.id == id }
        return (w?.dailyWage ?: 0.0) * getPresentDaysCount(id)
    }
    fun getTotalPaid(id: Int): Double = _paymentHistory.value.filter { it.worker == id }.sumOf { it.amount }
    fun getBalanceDue(id: Int): Double = getTotalEarned(id) - getTotalPaid(id)
    fun calculateRealEarnings(id: Int): Double = getTotalEarned(id)

    fun updateWorker(w: Worker) = viewModelScope.launch { if(RetrofitClient.instance.updateWorker(w.id, w).isSuccessful) fetchWorkers() }
    fun deleteWorker(w: Worker) = viewModelScope.launch { if(RetrofitClient.instance.deleteWorker(w.id).isSuccessful) fetchWorkers() }
}
