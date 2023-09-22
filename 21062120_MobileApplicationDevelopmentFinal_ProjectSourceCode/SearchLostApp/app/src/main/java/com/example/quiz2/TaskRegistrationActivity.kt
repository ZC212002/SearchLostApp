package com.example.quiz2

import DatabaseHelper
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TimePicker
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.task_registration_activity.*
import java.util.Calendar

private const val REQUEST_IMAGE_PICKER = 2

class TaskRegistrationActivity : AppCompatActivity() {
    private var photourl: String? = null
    private lateinit var db: DatabaseHelper
    private var selectedDate: String? = null
    private var selectedTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_registration_activity)

        toolbarTaskRegistrationActivity.title = "Save The Missing Person Details!"
        setSupportActionBar(toolbarTaskRegistrationActivity)

        db = DatabaseHelper(this@TaskRegistrationActivity)

        buttonSelectDateTime.setOnClickListener {
            showDateTimePicker()
        }

        button.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, REQUEST_IMAGE_PICKER)
        }

        buttonSave.setOnClickListener {
            val taskTitle = editTextTaskTitle.text.toString()
            val taskName = editTextTaskName.text.toString()

            saveTask(taskTitle, taskName)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_PICKER && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            photourl = selectedImageUri.toString()

            val imageView: ImageView = findViewById(R.id.imageView)
            photourl?.let {
                Glide.with(this).load(it).placeholder(R.drawable.photoholder).into(imageView)
            } ?: run {
                imageView.setImageResource(R.drawable.photoholder)
            }
        }
    }

    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                selectedDate = "$dayOfMonth/${monthOfYear + 1}/$year"
                showTimePicker(currentHour, currentMinute)
            },
            currentYear,
            currentMonth,
            currentDay
        )


        datePickerDialog.show()
    }

    private fun showTimePicker(currentHour: Int, currentMinute: Int) {
        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { _: TimePicker, hourOfDay: Int, minute: Int ->
                selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                buttonSelectDateTime.text = "$selectedDate, $selectedTime"
            },
            currentHour,
            currentMinute,
            true
        )

        timePickerDialog.show()
    }

    private fun saveTask(taskTitle: String, taskName: String) {
        if (taskTitle.isBlank() || taskName.isBlank()) {
            Toast.makeText(this, "Please enter both Person Name and Describe the details of the missing person", Toast.LENGTH_SHORT).show()
            return
        }

        if (photourl == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedDate == null || selectedTime == null) {
            Toast.makeText(this, "Please select date and time", Toast.LENGTH_SHORT).show()
            return
        }

        Log.e("Task Save", "Title: $taskTitle, Name: $taskName")


        val taskId = TaskDAO().addTask(db, taskTitle, taskName)


        db.insertImageURL(taskId, photourl!!)


        val dateTime = "$selectedDate, $selectedTime"
        TaskDAO().updateDateTime(db, taskId, dateTime)

        startActivity(Intent(this@TaskRegistrationActivity, MainActivity::class.java))
    }
}
