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

class Add_Assets_Page : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_assets_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DBHelper(this)

        val assetId = findViewById<EditText>(R.id.addAssetNumber)
        val assetDescription = findViewById<EditText>(R.id.addDescription)
        val assetLocation = findViewById<EditText>(R.id.addLocation)
        val assetRemarks = findViewById<EditText>(R.id.addRemarks)

        val addAssetToDashboard = findViewById<ImageButton>(R.id.addAssetToDashboard)
        addAssetToDashboard.setOnClickListener {
            val nextPage = Intent(this, Asset_Manager::class.java)
            startActivity(nextPage)
        }

        val saveAddAsset = findViewById<Button>(R.id.saveAddButton)
        saveAddAsset.setOnClickListener {
            val id = assetId.text.toString()
            val description = assetDescription.text.toString()
            val location = assetLocation.text.toString()
            val remarks = assetRemarks.text.toString()

            if (id.isNotEmpty() && description.isNotEmpty()) {
                val success = dbHelper.addAsset(id, description, location, remarks)
                if (success) {
                    Toast.makeText(this, "Asset Added Successfully", Toast.LENGTH_SHORT).show()
                    val nextPage = Intent(this, Asset_Manager::class.java)
                    startActivity(nextPage)
                    finish()
                } else {
                    Toast.makeText(this, "Failed to Add Asset. Asset Number might already exist.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill at least Asset Number and Description", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
