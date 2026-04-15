package com.example.practicalexam

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Asset_Manager : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private lateinit var assetRecyclerView: RecyclerView
    private lateinit var assetAdapter: AssetAdapter
    private var assetList: MutableList<Asset> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_asset_manager)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DBHelper(this)
        val unsortedList = dbHelper.getAllAssets()
        assetList.addAll(unsortedList.sortedWith(naturalOrder()))

        assetRecyclerView = findViewById(R.id.assetRecyclerView)
        assetRecyclerView.layoutManager = LinearLayoutManager(this)
        assetAdapter = AssetAdapter(assetList) { asset ->
            showAssetDetailsDialog(asset)
        }
        assetRecyclerView.adapter = assetAdapter

        val assetManagerToDashboard = findViewById<ImageButton>(R.id.assetManagerToDashboard)
        assetManagerToDashboard.setOnClickListener {
            val nextPage = Intent(this, Dashboard::class.java)
            startActivity(nextPage)
        }

        val addAsset = findViewById<Button>(R.id.addAssetButton)
        addAsset.setOnClickListener {
            val nextPage = Intent(this, Add_Assets_Page::class.java)
            startActivity(nextPage)
        }
    }

    private fun showAssetDetailsDialog(asset: Asset) {
        val details = "Asset Number: ${asset.assetNumber}\n\n" +
                "Description: ${asset.description}\n\n" +
                "Location: ${asset.location}\n\n" +
                "Remarks: ${asset.remarks}"

        AlertDialog.Builder(this)
            .setTitle("Asset Details")
            .setMessage(details)
            .setPositiveButton("Edit") { _, _ ->
                val intent = Intent(this, Edit_Asset_Page::class.java)
                intent.putExtra("ASSET_ID", asset.assetNumber)
                startActivity(intent)
            }
            .setNegativeButton("Delete") { _, _ ->
                if (dbHelper.deleteAsset(asset.assetNumber)) {
                    val position = assetList.indexOf(asset)
                    if (position != -1) {
                        assetList.removeAt(position)
                        assetAdapter.notifyItemRemoved(position)
                    }
                    Toast.makeText(this, "Asset Deleted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to Delete Asset", Toast.LENGTH_SHORT).show()
                }
            }
            .setNeutralButton("Close", null)
            .show()
    }

    private fun naturalOrder(): Comparator<Asset> {
        return Comparator { a, b ->
            val assetNumA = a.assetNumber
            val assetNumB = b.assetNumber

            val regex = "([0-9]+)|([a-zA-Z]+)".toRegex()
            val tokensA = regex.findAll(assetNumA).map { it.value }.toList()
            val tokensB = regex.findAll(assetNumB).map { it.value }.toList()

            val commonSize = minOf(tokensA.size, tokensB.size)
            for (i in 0 until commonSize) {
                val tokenA = tokensA[i]
                val tokenB = tokensB[i]

                val numA = tokenA.toIntOrNull()
                val numB = tokenB.toIntOrNull()

                if (numA != null && numB != null) {
                    val comparison = numA.compareTo(numB)
                    if (comparison != 0) {
                        return@Comparator comparison
                    }
                } else {
                    val comparison = tokenA.compareTo(tokenB)
                    if (comparison != 0) {
                        return@Comparator comparison
                    }
                }
            }

            return@Comparator tokensA.size.compareTo(tokensB.size)
        }
    }
}