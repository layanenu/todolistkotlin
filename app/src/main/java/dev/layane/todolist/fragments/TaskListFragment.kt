package dev.layane.todolist.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import dev.layane.todolist.R
import dev.layane.todolist.TaskActivity
import dev.layane.todolist.utils.Navigation

class TaskListFragment : Fragment() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseUser = firebaseAuth.currentUser
    private val dbRef = FirebaseDatabase.getInstance().getReference("users/${firebaseUser?.uid}/tasks")
    private val listItems = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var ctx: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_list, container, false)
        ctx = requireContext()

        val listView = view.findViewById<ListView>(R.id.tasks)
        adapter = ArrayAdapter(ctx, android.R.layout.simple_list_item_1, listItems)
        listView.adapter = adapter

        loadData(listView)

        return view
    }

    private fun loadData(listView: ListView) {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listItems.clear()
                for (element in snapshot.children) {
                    listItems.add(element.child("title").value.toString())
                }
                adapter.notifyDataSetChanged()

                listView.setOnItemLongClickListener { _, _, position, _ ->
                    val taskId = snapshot.children.toList()[position].key
                    if (taskId != null) {
                        AlertDialog.Builder(ctx)
                            .setTitle(R.string.title_delete_task)
                            .setMessage(R.string.message_delete_task)
                            .setPositiveButton(R.string.confirm) { dialog, _ ->
                                dbRef.child(taskId).removeValue()
                                dialog.dismiss()
                                Toast.makeText(ctx, R.string.task_deleted, Toast.LENGTH_SHORT).show()
                            }
                            .setNegativeButton(R.string.cancel) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    }
                    true
                }

                listView.setOnItemClickListener { _, _, position, _ ->
                    val taskId = snapshot.children.toList()[position].key
                    val activity = Intent(ctx, TaskActivity::class.java)
                    activity.putExtra("taskId", taskId)
                    Navigation.goToScreen(ctx, activity)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(ctx, R.string.error_loading_tasks, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
