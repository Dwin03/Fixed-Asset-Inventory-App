package com.example.practicalexam

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.IOException

class Dashboard : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper

    private val createFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                try {
                    val csvData = getCsvDataString()
                    if (csvData.isNotEmpty()) {
                        contentResolver.openOutputStream(uri)?.use {
                            it.write(csvData.toByteArray())
                        }
                        AlertDialog.Builder(this)
                            .setTitle("Export Successful")
                            .setMessage("Report saved successfully.")
                            .setPositiveButton("OK", null)
                            .show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    AlertDialog.Builder(this)
                        .setTitle("Export Failed")
                        .setMessage("An error occurred: ${e.message}")
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DBHelper(this)

        val searchBar = findViewById<EditText>(R.id.editTextSearchBar)
        val searchButton = findViewById<Button>(R.id.searchButton)

        searchButton.setOnClickListener {
            val searchQuery = searchBar.text.toString()
            if (searchQuery.isNotEmpty()) {
                val asset = dbHelper.searchAsset(searchQuery)
                if (asset != null) {
                    showAssetDetailsDialog(asset)
                } else {
                    showAssetNotFoundDialog()
                }
            } else {
                Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show()
            }
        }

        val manageAssets = findViewById<Button>(R.id.manageAssetButton)
        manageAssets.setOnClickListener {
            val nextPage = Intent(this, Asset_Manager::class.java)
            startActivity(nextPage)
        }

        val importData = findViewById<Button>(R.id.importButton)
        importData.setOnClickListener {
            val nextPage = Intent(this, Import_Data_CSV::class.java)
            startActivity(nextPage)
        }

        val exportReport = findViewById<Button>(R.id.exportButton)
        exportReport.setOnClickListener {
            createFileForExport()
        }

        val logout = findViewById<Button>(R.id.logoutButton)
        logout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes") { _, _ ->
                    val intent = Intent(this, Login_Page::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }.setNegativeButton("No", null)
                .show()
        }
    }

    private fun getCsvDataString(): String {
        val assets = dbHelper.getAllAssets()
        if (assets.isEmpty()) {
            Toast.makeText(this, "No data to export", Toast.LENGTH_SHORT).show()
            return ""
        }

        val csvHeader = "Asset_Number,Description,Location,Remarks\n"
        val csvData = StringBuilder()
        csvData.append(csvHeader)

        for (asset in assets) {
            csvData.append("\"${asset.assetNumber}\",\"${asset.description}\",\"${asset.location}\",\"${asset.remarks}\"\n")
        }
        return csvData.toString()
    }

    private fun createFileForExport() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/csv"
            putExtra(Intent.EXTRA_TITLE, "Asset_Report_${System.currentTimeMillis()}.csv")
        }
        createFileLauncher.launch(intent)
    }

    private fun showAssetDetailsDialog(asset: Asset) {
        val details = "Asset Number: ${asset.assetNumber}\n\n" +
                "Description: ${asset.description}"

        AlertDialog.Builder(this)
            .setTitle("Asset Found")
            .setMessage(details)
            .setPositiveButton("Close", null)
            .show()
    }

    private fun showAssetNotFoundDialog() {
        AlertDialog.Builder(this)
            .setTitle("Search Result")
            .setMessage("Asset Not Found")
            .setPositiveButton("OK", null)
            .show()
    }
}
