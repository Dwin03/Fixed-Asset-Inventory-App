package com.example.practicalexam

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Register : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DBHelper(this)

        val registerToLogin = findViewById<ImageButton>(R.id.registerToLogin)
        registerToLogin.setOnClickListener{
            val nextPage = Intent(this, Login_Page::class.java)
            startActivity(nextPage)
        }

        val usernameReg = findViewById<EditText>(R.id.editTextUserReg)
        val passwordReg = findViewById<EditText>(R.id.editTextPassReg)
        val confirmPassReg = findViewById<EditText>(R.id.editTextConfirmPass)
        val regAcc = findViewById<Button>(R.id.regButton)

        regAcc.setOnClickListener{
            val userReg = usernameReg.text.toString()
            val passReg = passwordReg.text.toString()
            val confirmPass = confirmPassReg.text.toString()

            if (userReg.isNotEmpty() && passReg.isNotEmpty() && confirmPass.isNotEmpty() && passReg == confirmPass) {
                val success = dbHelper.addUser(userReg, passReg)

                if (success) {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                    val nextPage = Intent(this, Login_Page::class.java)
                    startActivity(nextPage)
                    finish()
                }
                else {
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(this, "Please fill in all fields and ensure passwords match", Toast.LENGTH_SHORT).show()
            }
        }
    }
}