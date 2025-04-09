package dev.layane.todolist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import dev.layane.todolist.fragments.EmailInput
import dev.layane.todolist.fragments.PasswordDifficult
import dev.layane.todolist.utils.Navigation
import dev.layane.todolist.utils.Password
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var btnRegister: Button
    private lateinit var confirmPassword: EditText
    private lateinit var passwordFragment: PasswordDifficult
    private lateinit var emailFragment: EmailInput

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnRegister = findViewById(R.id.submitRegisterBtn)
        confirmPassword = findViewById(R.id.passwordConfirmInput)
        val forgotPassword = findViewById<TextView>(R.id.forgotPassword)
        val loginLink = findViewById<TextView>(R.id.login)

        // Referência aos fragments
        passwordFragment = supportFragmentManager.findFragmentById(R.id.passwordInput) as PasswordDifficult
        emailFragment = supportFragmentManager.findFragmentById(R.id.emailInput) as EmailInput

        btnRegister.setOnClickListener {
            val email = emailFragment.getEmail()
            val passText = passwordFragment.passwordInput.text.toString()
            val confirmPasswordText = confirmPassword.text.toString()

            CoroutineScope(Dispatchers.IO).launch {
                register(email, passText, confirmPasswordText)
            }
        }

        // Desabilita o botão enquanto a senha for fraca
        passwordFragment.passwordInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val password = s?.toString() ?: ""
                val result = Password.verifyPasswordDificult(password)
                btnRegister.isEnabled = result >= 3
            }
        })

        forgotPassword.setOnClickListener {
            Navigation.goToScreen(this, ForgotPasswordActivity::class.java)
        }

        loginLink.setOnClickListener {
            Navigation.goToScreen(this, LoginActivity::class.java)
        }
    }

    private fun register(email: String, password: String, confirmPassword: String) {
        if (password != confirmPassword) {
            showToast("As senhas não são iguais")
        } else if (password.isEmpty() || confirmPassword.isEmpty()) {
            showToast("As senhas não podem ser vazias")
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        showToast("Usuário cadastrado com sucesso!!!")
                        Navigation.goToScreen(this, MainActivity::class.java)
                    } else {
                        showToast("Falha ao registrar usuário.")
                    }
                }
        }
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(baseContext, message, Toast.LENGTH_SHORT).show()
        }
    }
}
