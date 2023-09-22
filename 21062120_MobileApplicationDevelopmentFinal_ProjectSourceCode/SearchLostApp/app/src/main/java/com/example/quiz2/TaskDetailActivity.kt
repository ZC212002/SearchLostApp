package com.example.quiz2

import DatabaseHelper
import Task
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TimePicker
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.task_detail_activity.*
import kotlinx.android.synthetic.main.task_registration_activity.editTextTaskTitle
import java.text.SimpleDateFormat
import java.util.*

private const val REQUEST_IMAGE_PICKER = 2

class TaskDetailActivity : AppCompatActivity() {
    private lateinit var task: Task
    private lateinit var db: DatabaseHelper
    private var selectedImageUri: Uri? = null
    private lateinit var dateButton: Button
    private lateinit var calendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_detail_activity)

        toolbarTaskDetailActivity.title = "Update The Missing Person Details!"
        setSupportActionBar(toolbarTaskDetailActivity)

        db = DatabaseHelper(this@TaskDetailActivity)

        task = intent.getSerializableExtra("task") as Task

        editTextTaskTitle.setText(task.taskTitle)
        editTextTaskName.setText(task.taskName)

        val imageURL = TaskDAO().getImageURL(db, task.taskId)
        if (!imageURL.isNullOrEmpty()) {
            Glide.with(this).load(imageURL).placeholder(R.drawable.photoholder).into(imageView)
        } else {
            imageView.setImageResource(R.drawable.photoholder)
        }

        dateButton = findViewById(R.id.dateButton)
        calendar = Calendar.getInstance()
        dateButton.text = task.taskDateTime

        dateButton.setOnClickListener {
            showDateTimePicker()
        }

        button.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, REQUEST_IMAGE_PICKER)
        }

        buttonUpdate.setOnClickListener {
            val taskTitle = editTextTaskTitle.text.toString()
            val taskName = editTextTaskName.text.toString()
            val taskDateTime = dateButton.text.toString()
            updateTask(task.taskId, taskTitle, taskName, taskDateTime)
        }
    }

    private fun showDateTimePicker() {
        val dateListener = DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)


            showTimePicker()
        }

        val datePickerDialog = DatePickerDialog(
            this, dateListener,
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val timeListener = TimePickerDialog.OnTimeSetListener { _: TimePicker, hourOfDay: Int, minute: Int ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)

            updateDateButton()
        }

        val timePickerDialog = TimePickerDialog(
            this, timeListener,
            calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
        )

        timePickerDialog.show()
    }

    private fun updateDateButton() {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val dateString = sdf.format(calendar.time)
        dateButton.text = dateString
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_PICKER && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            val imageView: ImageView = findViewById(R.id.imageView)
            selectedImageUri?.let {
                Glide.with(this).load(it).placeholder(R.drawable.photoholder).into(imageView)
            } ?: run {
                imageView.setImageResource(R.drawable.photoholder)
            }
        }
    }

    private fun updateTask(taskId: Int, taskTitle: String, taskName: String, taskDateTime: String) {
        if (taskTitle.isBlank() || taskName.isBlank()) {
            Toast.makeText(this, "Please enter both Person Name and Describe the details of the missing person", Toast.LENGTH_SHORT).show()
            return
        }

        if (task.taskTitle == taskTitle && task.taskName == taskName && task.taskDateTime == taskDateTime) {
            Toast.makeText(this, "No changes made here", Toast.LENGTH_SHORT).show()
            return
        }

        Log.e("Update Task", "$taskId - $taskTitle - $taskName")


        TaskDAO().updateTask(db, taskId, taskTitle, taskName, taskDateTime)


        selectedImageUri?.let {
            val imageURL = it.toString()
            TaskDAO().insertImageURL(db, taskId, imageURL)
        }

        startActivity(Intent(this@TaskDetailActivity, MainActivity::class.java))
        finish()
    }
}
