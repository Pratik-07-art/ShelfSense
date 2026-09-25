package com.example.shelfsense

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ItemDetailsActivity : AppCompatActivity() {

    private lateinit var storageManager: StorageManager
    private var itemId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_item_details)

        storageManager = StorageManager(this)

        itemId = intent.getStringExtra("ITEM_ID")

        val back = findViewById<TextView>(R.id.btnBack)

        back.setOnClickListener {
            finish()
        }

        loadItem()

        findViewById<Button>(R.id.btnEdit).setOnClickListener {

            val intent = Intent(
                this,
                EditItemActivity::class.java
            )

            intent.putExtra("ITEM_ID", itemId)

            startActivity(intent)
        }

        findViewById<Button>(R.id.btnDelete).setOnClickListener {
            showDeleteDialog()
        }
    }

    override fun onResume() {
        super.onResume()

        if (::storageManager.isInitialized) {
            loadItem()
        }
    }

    private fun loadItem() {

        val id = itemId ?: return

        val item = storageManager
            .getItems()
            .find { it.id == id }

        if (item == null) {
            finish()
            return
        }

        findViewById<TextView>(
            R.id.tvItemName
        ).text = item.name

        findViewById<TextView>(
            R.id.tvCategory
        ).text = item.category

        findViewById<TextView>(
            R.id.tvQuantity
        ).text =
            "Quantity: ${item.quantity} ${item.unit}"

        findViewById<TextView>(
            R.id.tvPurchaseDate
        ).text =
            "Purchase Date: ${item.purchaseDate}"

        findViewById<TextView>(
            R.id.tvExpiryDate
        ).text =
            "Expiry Date: ${item.expiryDate}"

        findViewById<TextView>(
            R.id.tvNotes
        ).text =
            if (item.notes.isEmpty()) {
                "Notes: No notes"
            } else {
                "Notes: ${item.notes}"
            }
    }

    private fun showDeleteDialog() {

        AlertDialog.Builder(this)
            .setTitle("Delete Item?")
            .setMessage(
                "Are you sure you want to delete this item?"
            )
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->

                val id = itemId ?: return@setPositiveButton

                storageManager.deleteItem(id)

                Toast.makeText(
                    this,
                    "Item deleted",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
            .show()
    }
}