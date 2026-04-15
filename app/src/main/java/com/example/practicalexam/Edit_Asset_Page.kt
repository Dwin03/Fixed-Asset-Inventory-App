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

class Edit_Asset_Page : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private var assetId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_asset_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DBHelper(this)
        assetId = intent.getStringExtra("ASSET_ID")

        val assetDescription = findViewById<EditText>(R.id.editDescription)
        val assetLocation = findViewById<EditText>(R.id.editLocation)
        val assetRemarks = findViewById<EditText>(R.id.editRemarks)

        if (assetId != null) {
            val asset = dbHelper.getAsset(assetId!!)
            if (asset != null) {
                findViewById<EditText>(R.id.editAssetNumber).setText(asset.assetNumber)
                assetDescription.setText(asset.description)
                assetLocation.setText(asset.location)
                assetRemarks.setText(asset.remarks)
            }
        }

        val editAssetToAssetManager = findViewById<ImageButton>(R.id.editAssetToAssetManager)
        editAssetToAssetManager.setOnClickListener {
            val nextPage = Intent(this, Asset_Manager::class.java)
            startActivity(nextPage)
        }

        val saveEditAsset = findViewById<Button>(R.id.saveEditButton)
        saveEditAsset.setOnClickListener {
            val description = assetDescription.text.toString()
            val location = assetLocation.text.toString()
            val remarks = assetRemarks.text.toString()

            if (assetId != null && description.isNotEmpty()) {
                if (dbHelper.updateAsset(assetId!!, description, location, remarks)) {
                    Toast.makeText(this, "Asset Updated", Toast.LENGTH_SHORT).show()
                    val nextPage = Intent(this, Asset_Manager::class.java)
                    startActivity(nextPage)
                    finish()
                } else {
                    Toast.makeText(this, "Failed to Update Asset", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Description cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
