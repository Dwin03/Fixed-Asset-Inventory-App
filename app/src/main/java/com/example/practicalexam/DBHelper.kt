package com.example.practicalexam

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class DBHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "Inventory.db"
        private const val DATABASE_VERSION = 1

        // Accounts Table
        const val TABLE_ACCOUNTS = "accounts"
        const val COL_ACC_ID = "ID"
        const val COL_ACC_USERNAME = "Username"
        const val COL_ACC_PASSWORD = "Password"

        // Assets Table
        const val TABLE_INVENTORY = "inventory"
        const val COL_ASSET_ID = "Asset_Number"
        const val COL_ASSET_DESCRIPTION = "Description"
        const val COL_ASSET_LOCATION = "Location"
        const val COL_ASSET_REMARKS = "Remarks"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createAccountsTable = "CREATE TABLE $TABLE_ACCOUNTS (" +
                "$COL_ACC_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_ACC_USERNAME TEXT UNIQUE, " +
                "$COL_ACC_PASSWORD TEXT)"
        db.execSQL(createAccountsTable)

        val createAssetsTable = "CREATE TABLE $TABLE_INVENTORY (" +
                "$COL_ASSET_ID TEXT PRIMARY KEY, " +
                "$COL_ASSET_DESCRIPTION TEXT, "+
                "$COL_ASSET_LOCATION TEXT, "+
                "$COL_ASSET_REMARKS TEXT)"
        db.execSQL(createAssetsTable)

        seedAccounts(db)
        seedAssets(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ACCOUNTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_INVENTORY")
        onCreate(db)
    }

    private fun seedAccounts(db: SQLiteDatabase) {
        try {
            val inputStream = context.assets.open("accounts/Inventory_Accounts.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))
            reader.readLine() // Skip header if there is one
            reader.forEachLine { line ->
                val tokens = line.split(',')
                if (tokens.size == 3) { // ID,Username,Password
                    val values = ContentValues()
                    values.put(COL_ACC_USERNAME, tokens[1].trim())
                    values.put(COL_ACC_PASSWORD, tokens[2].trim())
                    db.insert(TABLE_ACCOUNTS, null, values)
                }
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun seedAssets(db: SQLiteDatabase) {
        try {
            val inputStream = context.assets.open("inventory/Asset_Inventory.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))
            reader.readLine() // Skip header if there is one
            reader.forEachLine { line ->
                val tokens = line.split(',')
                if (tokens.size == 4) { // Expects Asset Number, Description, Location, Remarks
                    val values = ContentValues()
                    values.put(COL_ASSET_ID, tokens[0].trim())
                    values.put(COL_ASSET_DESCRIPTION, tokens[1].trim())
                    values.put(COL_ASSET_LOCATION, tokens[2].trim())
                    values.put(COL_ASSET_REMARKS, tokens[3].trim())
                    db.insert(TABLE_INVENTORY, null, values)
                }
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addUser(username: String, password: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_ACC_USERNAME, username)
        values.put(COL_ACC_PASSWORD, password)
        val result = db.insert(TABLE_ACCOUNTS, null, values)
        return result != -1L
    }

    fun checkUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_ACCOUNTS WHERE $COL_ACC_USERNAME = ? AND $COL_ACC_PASSWORD = ?", arrayOf(username, password))
        val userExists = cursor.count > 0
        cursor.close()
        return userExists
    }

    fun addAsset(assetId: String, description: String, location: String, remarks: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_ASSET_ID, assetId)
        values.put(COL_ASSET_DESCRIPTION, description)
        values.put(COL_ASSET_LOCATION, location)
        values.put(COL_ASSET_REMARKS, remarks)
        val result = db.insert(TABLE_INVENTORY, null, values)
        return result != -1L
    }

    @SuppressLint("Range")
    fun getAllAssets(): List<Asset> {
        val assetList = mutableListOf<Asset>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_INVENTORY, null, null, null, null, null, "length($COL_ASSET_ID) ASC, $COL_ASSET_ID ASC")

        if (cursor.moveToFirst()) {
            do {
                val asset = Asset(
                    assetNumber = cursor.getString(cursor.getColumnIndex(COL_ASSET_ID)),
                    description = cursor.getString(cursor.getColumnIndex(COL_ASSET_DESCRIPTION)),
                    location = cursor.getString(cursor.getColumnIndex(COL_ASSET_LOCATION)) ?: "",
                    remarks = cursor.getString(cursor.getColumnIndex(COL_ASSET_REMARKS)) ?: ""
                )
                assetList.add(asset)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return assetList
    }

    fun deleteAsset(assetNumber: String): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_INVENTORY, "$COL_ASSET_ID = ?", arrayOf(assetNumber))
        return result > 0
    }

    fun updateAsset(assetNumber: String, description: String, location: String, remarks: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_ASSET_DESCRIPTION, description)
        values.put(COL_ASSET_LOCATION, location)
        values.put(COL_ASSET_REMARKS, remarks)
        val result = db.update(TABLE_INVENTORY, values, "$COL_ASSET_ID = ?", arrayOf(assetNumber))
        return result > 0
    }

    @SuppressLint("Range")
    fun getAsset(assetNumber: String): Asset? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_INVENTORY, null, "$COL_ASSET_ID = ?", arrayOf(assetNumber), null, null, null
        )
        var asset: Asset? = null
        if (cursor.moveToFirst()) {
            asset = Asset(
                assetNumber = cursor.getString(cursor.getColumnIndex(COL_ASSET_ID)),
                description = cursor.getString(cursor.getColumnIndex(COL_ASSET_DESCRIPTION)),
                location = cursor.getString(cursor.getColumnIndex(COL_ASSET_LOCATION)) ?: "",
                remarks = cursor.getString(cursor.getColumnIndex(COL_ASSET_REMARKS)) ?: ""
            )
        }
        cursor.close()
        return asset
    }

    @SuppressLint("Range")
    fun searchAsset(query: String): Asset? {
        val db = this.readableDatabase
        val selection = "LOWER($COL_ASSET_ID) = LOWER(?) OR LOWER($COL_ASSET_DESCRIPTION) LIKE LOWER(?)"
        val selectionArgs = arrayOf(query, "%$query%")
        val cursor = db.query(
            TABLE_INVENTORY,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null,
            "1" // LIMIT 1
        )
        var asset: Asset? = null
        if (cursor.moveToFirst()) {
            asset = Asset(
                assetNumber = cursor.getString(cursor.getColumnIndex(COL_ASSET_ID)),
                description = cursor.getString(cursor.getColumnIndex(COL_ASSET_DESCRIPTION)),
                location = cursor.getString(cursor.getColumnIndex(COL_ASSET_LOCATION)) ?: "",
                remarks = cursor.getString(cursor.getColumnIndex(COL_ASSET_REMARKS)) ?: ""
            )
        }
        cursor.close()
        return asset
    }

    fun importAssetsFromCsvStream(inputStream: InputStream): Int {
        val db = this.writableDatabase
        var recordsAdded = 0
        db.beginTransaction()
        try {
            val reader = BufferedReader(InputStreamReader(inputStream))
            reader.readLine() // Skip header line
            reader.forEachLine { line ->
                val tokens = line.split(',')
                if (tokens.size == 4) {
                    val values = ContentValues()
                    values.put(COL_ASSET_ID, tokens[0].trim())
                    values.put(COL_ASSET_DESCRIPTION, tokens[1].trim())
                    values.put(COL_ASSET_LOCATION, tokens[2].trim())
                    values.put(COL_ASSET_REMARKS, tokens[3].trim())
                    val result = db.insertWithOnConflict(TABLE_INVENTORY, null, values, SQLiteDatabase.CONFLICT_IGNORE)
                    if (result != -1L) {
                        recordsAdded++
                    }
                }
            }
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            e.printStackTrace()
            recordsAdded = 0 // Reset count on error
        } finally {
            db.endTransaction()
        }
        return recordsAdded
    }
}
