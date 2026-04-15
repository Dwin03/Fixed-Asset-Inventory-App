package com.example.practicalexam

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Import_Data_CSV : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private lateinit var fileNameTextView: TextView
    private var selectedFileUri: Uri? = null

    companion object {
        private const val FILE_SELECT_CODE = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_import_data_csv)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DBHelper(this)
        fileNameTextView = findViewById(R.id.fileNameTextView)

        val importDataToDashboard = findViewById<ImageButton>(R.id.importDataToDashboard)
        importDataToDashboard.setOnClickListener {
            val nextPage = Intent(this, Dashboard::class.java)
            startActivity(nextPage)
        }

        val selectFileButton = findViewById<Button>(R.id.selectFileButton)
        selectFileButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "text/csv"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            try {
                startActivityForResult(Intent.createChooser(intent, "Select a CSV file"), FILE_SELECT_CODE)
            } catch (ex: android.content.ActivityNotFoundException) {
                Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show()
            }
        }

        val importButton = findViewById<Button>(R.id.importButton)
        importButton.setOnClickListener {
            selectedFileUri?.let {
                try {
                    val inputStream = contentResolver.openInputStream(it)
                    if(inputStream != null){
                        val recordsAdded = dbHelper.importAssetsFromCsvStream(inputStream)
                        Toast.makeText(this, "$recordsAdded new records imported.", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, Asset_Manager::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Could not open file stream.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error importing file: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(this, "Please select a file first.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_SELECT_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let {
                selectedFileUri = it
                fileNameTextView.text = it.lastPathSegment ?: "File Selected"
            }
        }
    }
}
