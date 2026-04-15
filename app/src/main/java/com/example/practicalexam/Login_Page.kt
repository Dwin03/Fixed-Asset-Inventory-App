package com.example.practicalexam

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Login_Page : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DBHelper(this)

        val usernameLogin = findViewById<EditText>(R.id.editTextUser)
        val passwordLogin = findViewById<EditText>(R.id.editTextPassword)
        val loginAcc = findViewById<Button>(R.id.loginButton)

        loginAcc.setOnClickListener {
            val user = usernameLogin.text.toString()
            val pass = passwordLogin.text.toString()

            if (user.isNotEmpty() && pass.isNotEmpty()) {
                if (dbHelper.checkUser(user, pass)) {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    val nextPage = Intent(this, Dashboard::class.java)
                    startActivity(nextPage)
                    finish()
                } else {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        val createAcc = findViewById<TextView>(R.id.regButton)
        createAcc.setOnClickListener {
            val nextPage = Intent(this, Register::class.java)
            startActivity(nextPage)
        }
    }
}
