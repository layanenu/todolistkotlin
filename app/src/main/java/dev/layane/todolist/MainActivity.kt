package dev.layane.todolist

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import dev.layane.todolist.fragments.TaskListFragment
import dev.layane.todolist.fragments.WeatherFragment
import dev.layane.todolist.utils.AuthUtils
import dev.layane.todolist.utils.Navigation

class MainActivity : AppCompatActivity() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Carrega o fragmento de clima
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_weather, WeatherFragment())
            .commit()

        // Carrega o fragmento de lista de tarefas
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_task_list, TaskListFragment())
            .commit()

        val fabAddTask = findViewById<FloatingActionButton>(R.id.fab_add_task)
        val logoutBtn = findViewById<ImageView>(R.id.logout)
        val profileBtn = findViewById<ImageView>(R.id.profile)

        fabAddTask.setOnClickListener {
            Navigation.goToScreen(this, TaskActivity::class.java)
        }

        logoutBtn.setOnClickListener {
            AuthUtils.logout(this)
        }

        profileBtn.setOnClickListener {
            Navigation.goToScreen(this, ProfileActivity::class.java)
        }

        // Verifica se há sessão ativa, redireciona para Login se não houver
        if (firebaseAuth.currentUser == null) {
            Navigation.goToScreen(this, LoginActivity::class.java)
        }

        requestNotificationPermission()
    }

    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
        }
    }
}
