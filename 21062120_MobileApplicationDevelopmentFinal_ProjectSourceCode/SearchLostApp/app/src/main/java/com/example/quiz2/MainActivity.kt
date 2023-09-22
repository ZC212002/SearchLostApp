package com.example.quiz2

import DatabaseHelper
import Task
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    private lateinit var taskList: ArrayList<Task>
    private lateinit var adapter: TaskAdapter
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbarMainActivity.title = "Searching For Lost Person"
        toolbarMainActivity.subtitle = "The snow to send carbon"
        setSupportActionBar(toolbarMainActivity)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)

        db = DatabaseHelper(this@MainActivity)

        getAllTasks()

        fab.setOnClickListener {
            startActivity(Intent(this@MainActivity, TaskRegistrationActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        val item = menu.findItem(R.id.action_search)
        val searchView = item.actionView as SearchView
        searchView.setOnQueryTextListener(this)

        val shareItem = menu.findItem(R.id.action_share)
        shareItem.setOnMenuItemClickListener {
            shareTaskList()
            true
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        Log.e("Submitted search query", query)
        performSearch(query)
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        Log.e("Search query change", newText)
        performSearch(newText)
        return true
    }

    private fun getAllTasks() {
        taskList = TaskDAO().getAllTasks(db)
        adapter = TaskAdapter(this@MainActivity, taskList, db)
        recyclerView.adapter = adapter
    }

    private fun performSearch(searchKeyword: String) {
        taskList = TaskDAO().searchTasks(db, searchKeyword)
        adapter = TaskAdapter(this@MainActivity, taskList, db)
        recyclerView.adapter = adapter
    }

    private fun getAllTasksAsString(): String {
        val taskList = TaskDAO().getAllTasks(db)
        val stringBuilder = StringBuilder()

        for (task in taskList) {
            stringBuilder.append("Person Image: ${task.taskPhotoUrl}\n")
            stringBuilder.append("Person Name: ${task.taskTitle}\n")
            stringBuilder.append("Missing Person Details: ${task.taskName}\n")
            stringBuilder.append("Missing Date and Time: ${task.taskDateTime}\n\n")
        }

        return stringBuilder.toString()
    }

    private fun shareTaskList() {
        val tasks = getAllTasksAsString()
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "List of Tasks")
            putExtra(Intent.EXTRA_TEXT, tasks)
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Tasks List")
        startActivity(shareIntent)
    }
}
