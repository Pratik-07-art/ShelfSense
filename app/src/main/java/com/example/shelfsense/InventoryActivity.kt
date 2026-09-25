package com.example.shelfsense

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class InventoryActivity : AppCompatActivity() {

    private lateinit var storageManager: StorageManager
    private lateinit var adapter: ItemAdapter

    private lateinit var searchBox: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var sortSpinner: Spinner
    private lateinit var itemCount: TextView

    private var allItems = mutableListOf<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_inventory)

        storageManager = StorageManager(this)

        searchBox = findViewById(R.id.etSearch)
        categorySpinner = findViewById(R.id.spinnerFilter)
        sortSpinner = findViewById(R.id.spinnerSort)
        itemCount = findViewById(R.id.tvItemCount)

        val recyclerView =
            findViewById<RecyclerView>(R.id.recyclerViewItems)

        adapter = ItemAdapter(emptyList()) { item ->

            val intent =
                Intent(this, ItemDetailsActivity::class.java)

            intent.putExtra(
                "ITEM_ID",
                item.id
            )

            startActivity(intent)
        }

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        recyclerView.adapter = adapter

        findViewById<TextView>(
            R.id.btnBack
        ).setOnClickListener {
            finish()
        }

        setupCategorySpinner()
        setupSortSpinner()
        setupSearch()

        loadItems()
    }

    override fun onResume() {
        super.onResume()

        if (::storageManager.isInitialized) {
            loadItems()
        }
    }

    private fun loadItems() {

        allItems =
            storageManager.getItems()

        applyFilters()
    }

    private fun setupSearch() {

        searchBox.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    applyFilters()
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )
    }

    private fun setupCategorySpinner() {

        val categories = arrayOf(
            "All",
            "Food/Grocery",
            "Personal Care",
            "Medicine",
            "Cleaning",
            "Other"
        )

        val spinnerAdapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                categories
            )

        spinnerAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        categorySpinner.adapter =
            spinnerAdapter

        categorySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    applyFilters()
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                }
            }
    }

    private fun setupSortSpinner() {

        val sortOptions = arrayOf(
            "Default",
            "Name A-Z",
            "Name Z-A",
            "Expiry Soonest",
            "Expiry Latest",
            "Quantity Low-High",
            "Quantity High-Low"
        )

        val spinnerAdapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                sortOptions
            )

        spinnerAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        sortSpinner.adapter =
            spinnerAdapter

        sortSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    applyFilters()
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                }
            }
    }

    private fun applyFilters() {

        val filterType =
            intent.getStringExtra("FILTER_TYPE")

        var filteredItems =
            when (filterType) {

                "LOW_STOCK" ->
                    allItems.filter {
                        ExpiryUtils.isLowStock(it)
                    }

                "EXPIRED" ->
                    allItems.filter {
                        ExpiryUtils.isExpired(it)
                    }

                else ->
                    allItems
            }

        // Search

        val searchText =
            searchBox.text
                .toString()
                .trim()
                .lowercase()

        if (searchText.isNotEmpty()) {

            filteredItems =
                filteredItems.filter {

                    it.name
                        .lowercase()
                        .contains(searchText)
                }
        }

        // Category

        val selectedCategory =
            categorySpinner.selectedItem?.toString()

        if (
            !selectedCategory.isNullOrEmpty() &&
            selectedCategory != "All"
        ) {

            filteredItems =
                filteredItems.filter {
                    it.category == selectedCategory
                }
        }

        // Sorting

        filteredItems =
            when (sortSpinner.selectedItem?.toString()) {

                "Name A-Z" ->
                    filteredItems.sortedBy {
                        it.name.lowercase()
                    }

                "Name Z-A" ->
                    filteredItems.sortedByDescending {
                        it.name.lowercase()
                    }

                "Expiry Soonest" ->
                    filteredItems.sortedBy {
                        ExpiryUtils.getDaysUntilExpiry(
                            it.expiryDate
                        )
                    }

                "Expiry Latest" ->
                    filteredItems.sortedByDescending {
                        ExpiryUtils.getDaysUntilExpiry(
                            it.expiryDate
                        )
                    }

                "Quantity Low-High" ->
                    filteredItems.sortedBy {
                        it.quantity
                    }

                "Quantity High-Low" ->
                    filteredItems.sortedByDescending {
                        it.quantity
                    }

                else ->
                    filteredItems
            }

        adapter.updateItems(
            filteredItems
        )

        itemCount.text =
            "${filteredItems.size} Items"
    }
}