package com.example.shelfsense

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ExpiringItemsActivity : AppCompatActivity() {

    private lateinit var storageManager: StorageManager
    private lateinit var adapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expiring_items)

        storageManager = StorageManager(this)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewExpiring)

        adapter = ItemAdapter(emptyList()) { item ->

            val intent = Intent(this, ItemDetailsActivity::class.java)
            intent.putExtra("ITEM_ID", item.id)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        findViewById<TextView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        loadExpiringItems()
    }

    override fun onResume() {
        super.onResume()

        if (::storageManager.isInitialized) {
            loadExpiringItems()
        }
    }

    private fun loadExpiringItems() {

        val items = storageManager.getItems()

        val expiringItems = items
            .filter { item ->
                ExpiryUtils.getDaysUntilExpiry(item.expiryDate) <= 7
            }
            .sortedBy {
                ExpiryUtils.getDaysUntilExpiry(it.expiryDate)
            }

        adapter.updateItems(expiringItems)
    }
}