package com.example.quiz2

import DatabaseHelper
import Task
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val mContext: Context,
    private var taskList: ArrayList<Task>,
    private val db: DatabaseHelper
) : RecyclerView.Adapter<TaskAdapter.CardViewHolder>() {

    inner class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: CardView = view.findViewById(R.id.cardView)
        val textView: TextView = view.findViewById(R.id.textViewTaskName)
        val imageView: ImageView = view.findViewById(R.id.imageViewDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.task_row_layout, parent, false)
        return CardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val task = taskList[position]

        holder.textView.text = task.taskTitle

        holder.imageView.setOnClickListener {
            Toast.makeText(
                mContext,
                "${task.taskTitle} has been found. Thank you for giving a helping hand",
                Toast.LENGTH_SHORT
            ).show()

            TaskDAO().deleteTask(db, task.taskId)

            taskList = TaskDAO().getAllTasks(db)
            notifyDataSetChanged()
        }

        holder.cardView.setOnClickListener {
            val intent = Intent(mContext, TaskDetailActivity::class.java)
            intent.putExtra("task", task)
            mContext.startActivity(intent)
        }
    }
}
