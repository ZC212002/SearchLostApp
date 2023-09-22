package com.example.quiz2

import DatabaseHelper
import Task
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import java.util.*

class TaskDAO {
    fun getAllTasks(dbHelper: DatabaseHelper): ArrayList<Task> {
        val db = dbHelper.writableDatabase
        val taskList = ArrayList<Task>()
        val cursor = db.rawQuery("SELECT * FROM tasks", null)

        while (cursor.moveToNext()) {
            val task = Task(
                cursor.getInt(cursor.getColumnIndex("task_id")),
                cursor.getString(cursor.getColumnIndex("task_title")),
                cursor.getString(cursor.getColumnIndex("task_name")),
                cursor.getString(cursor.getColumnIndex("task_photo_url")),
                cursor.getString(cursor.getColumnIndex("task_date_time"))
            )
            taskList.add(task)
        }
        cursor.close()
        return taskList
    }

    fun searchTasks(dbHelper: DatabaseHelper, searchKeyword: String): ArrayList<Task> {
        val db = dbHelper.writableDatabase
        val taskList = ArrayList<Task>()
        val cursor = db.rawQuery(
            "SELECT * FROM tasks WHERE task_title LIKE '%$searchKeyword%'",
            null
        )

        while (cursor.moveToNext()) {
            val task = Task(
                cursor.getInt(cursor.getColumnIndex("task_id")),
                cursor.getString(cursor.getColumnIndex("task_title")),
                cursor.getString(cursor.getColumnIndex("task_name")),
                cursor.getString(cursor.getColumnIndex("task_photo_url")),
                cursor.getString(cursor.getColumnIndex("task_date_time"))
            )
            taskList.add(task)
        }
        cursor.close()
        return taskList
    }

    fun deleteTask(dbHelper: DatabaseHelper, taskId: Int) {
        val db = dbHelper.writableDatabase
        db.delete("tasks", "task_id=?", arrayOf(taskId.toString()))
        db.close()
    }

    fun addTask(dbHelper: DatabaseHelper, taskTitle: String, taskName: String): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues()
        values.put(DatabaseHelper.COLUMN_TASK_TITLE, taskTitle)
        values.put(DatabaseHelper.COLUMN_TASK_NAME, taskName)

        val insertedId = db.insertOrThrow(DatabaseHelper.TABLE_NAME, null, values)
        db.close()
        return insertedId.toInt()
    }

    fun getImageURL(dbHelper: DatabaseHelper, taskId: Int): String? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_NAME,
            arrayOf(DatabaseHelper.COLUMN_TASK_PHOTO_URL),
            "${DatabaseHelper.COLUMN_TASK_ID}=?",
            arrayOf(taskId.toString()),
            null,
            null,
            null
        )
        var imageURL: String? = null

        if (cursor.moveToFirst()) {
            imageURL = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_PHOTO_URL))
        }

        cursor.close()
        return imageURL
    }

    fun insertImageURL(dbHelper: DatabaseHelper, taskId: Int, imageURL: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues()
        values.put(DatabaseHelper.COLUMN_TASK_PHOTO_URL, imageURL)

        db.update(
            DatabaseHelper.TABLE_NAME,
            values,
            "${DatabaseHelper.COLUMN_TASK_ID}=?",
            arrayOf(taskId.toString())
        )

        db.close()
    }

    fun updateDateTime(dbHelper: DatabaseHelper, taskId: Int, dateTime: String) {
        val db = dbHelper.writableDatabase

        val values = ContentValues()
        values.put(DatabaseHelper.COLUMN_TASK_DATE_TIME, dateTime)

        db.update(
            DatabaseHelper.TABLE_NAME,
            values,
            "${DatabaseHelper.COLUMN_TASK_ID}=?",
            arrayOf(taskId.toString())
        )
        db.close()
    }

    fun updateTask(dbHelper: DatabaseHelper, taskId: Int, taskTitle: String, taskName: String, taskDateTime: String) {
        val db = dbHelper.writableDatabase

        val values = ContentValues()
        values.put(DatabaseHelper.COLUMN_TASK_TITLE, taskTitle)
        values.put(DatabaseHelper.COLUMN_TASK_NAME, taskName)
        values.put(DatabaseHelper.COLUMN_TASK_DATE_TIME, taskDateTime)

        db.update(
            DatabaseHelper.TABLE_NAME,
            values,
            "${DatabaseHelper.COLUMN_TASK_ID}=?",
            arrayOf(taskId.toString())
        )
        db.close()
    }
}
