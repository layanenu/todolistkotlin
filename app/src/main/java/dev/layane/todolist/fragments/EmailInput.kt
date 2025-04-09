package dev.layane.todolist.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import dev.layane.todolist.R

class EmailInput : Fragment() {
    lateinit var emailInput: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_email_input, container, false)
        emailInput = view.findViewById(R.id.emailInputField)

        // Usa o hint padrão que já existe em strings.xml
        emailInput.hint = getString(R.string.email)

        return view
    }

    fun getEmail(): String {
        return emailInput.text.toString()
    }
}
