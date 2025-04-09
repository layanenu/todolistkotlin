package dev.layane.todolist

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import dev.layane.todolist.fragments.EmailInput
import dev.layane.todolist.utils.Navigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var emailFragment: EmailInput

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailFragment = supportFragmentManager.findFragmentById(R.id.emailInputFragment) as EmailInput

        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val btnLogin = findViewById<Button>(R.id.submitLoginBtn)
        val forgotPassword = findViewById<TextView>(R.id.forgotPassword)
        val registerLink = findViewById<TextView>(R.id.create_account)

        btnLogin.setOnClickListener {
            val email = emailFragment.getEmail()
            val password = passwordInput.text.toString()

            CoroutineScope(Dispatchers.IO).launch {
                login(email, password)
            }
        }

        forgotPassword.setOnClickListener {
            Navigation.goToScreen(this, ForgotPasswordActivity::class.java)
        }

        registerLink.setOnClickListener {
            Navigation.goToScreen(this, RegisterActivity::class.java)
        }
    }

    private fun login(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        Toast.makeText(this, "Seja bem-vindo ${user.email}", Toast.LENGTH_SHORT).show()
                        Navigation.goToScreen(this, MainActivity::class.java)
                    }
                } else {
                    Toast.makeText(this, "Falha ao realizar login", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
