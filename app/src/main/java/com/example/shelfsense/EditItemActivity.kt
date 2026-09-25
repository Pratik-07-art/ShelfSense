package com.example.shelfsense

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditItemActivity : AppCompatActivity() {

    private lateinit var storageManager: StorageManager
    private lateinit var item: Item

    private var purchaseDate = ""
    private var expiryDate = ""

    private val categories = arrayOf(
        "Food/Grocery",
        "Personal Care",
        "Medicine",
        "Cleaning",
        "Other"
    )

    private val units = arrayOf(
        "Piece",
        "Packet",
        "Bottle",
        "Box",
        "Kg",
        "Gram",
        "Liter",
        "ml",
        "Dozen",
        "Other"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_item)

        storageManager = StorageManager(this)

        val itemId =
            intent.getStringExtra("ITEM_ID")

        if (itemId == null) {
            finish()
            return
        }

        val foundItem =
            storageManager.getItems()
                .find { it.id == itemId }

        if (foundItem == null) {
            finish()
            return
        }

        item = foundItem

        setupScreen()
    }

    private fun setupScreen() {

        val name =
            findViewById<EditText>(
                R.id.etItemName
            )

        val quantity =
            findViewById<EditText>(
                R.id.etQuantity
            )

        val notes =
            findViewById<EditText>(
                R.id.etNotes
            )

        val category =
            findViewById<Spinner>(
                R.id.spinnerCategory
            )

        val unit =
            findViewById<Spinner>(
                R.id.spinnerUnit
            )

        val purchase =
            findViewById<TextView>(
                R.id.tvPurchaseDate
            )

        val expiry =
            findViewById<TextView>(
                R.id.tvExpiryDate
            )

        // Existing values

        name.setText(item.name)

        quantity.setText(
            item.quantity.toString()
        )

        notes.setText(item.notes)

        purchaseDate =
            item.purchaseDate

        expiryDate =
            item.expiryDate

        purchase.text =
            purchaseDate

        expiry.text =
            expiryDate

        // Category spinner

        val categoryAdapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                categories
            )

        categoryAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        category.adapter =
            categoryAdapter

        val categoryPosition =
            categories.indexOf(item.category)

        if (categoryPosition >= 0) {
            category.setSelection(
                categoryPosition
            )
        }

        // Unit spinner

        val unitAdapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                units
            )

        unitAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        unit.adapter =
            unitAdapter

        val unitPosition =
            units.indexOf(item.unit)

        if (unitPosition >= 0) {
            unit.setSelection(unitPosition)
        }

        // Back

        findViewById<TextView>(
            R.id.btnBack
        ).setOnClickListener {
            finish()
        }

        // Purchase date

        purchase.setOnClickListener {

            showDatePicker { date ->

                purchaseDate = date

                purchase.text = date
            }
        }

        // Expiry date

        expiry.setOnClickListener {

            showDatePicker { date ->

                expiryDate = date

                expiry.text = date
            }
        }

        // Update

        findViewById<Button>(
            R.id.btnUpdateItem
        ).setOnClickListener {

            updateItem()
        }
    }

    private fun updateItem() {

        val name =
            findViewById<EditText>(
                R.id.etItemName
            ).text.toString().trim()

        val quantityText =
            findViewById<EditText>(
                R.id.etQuantity
            ).text.toString().trim()

        val notes =
            findViewById<EditText>(
                R.id.etNotes
            ).text.toString().trim()

        val category =
            findViewById<Spinner>(
                R.id.spinnerCategory
            ).selectedItem.toString()

        val unit =
            findViewById<Spinner>(
                R.id.spinnerUnit
            ).selectedItem.toString()

        if (name.isEmpty()) {

            Toast.makeText(
                this,
                "Please enter item name",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val quantity =
            quantityText.toIntOrNull()

        if (quantity == null || quantity <= 0) {

            Toast.makeText(
                this,
                "Please enter a valid quantity",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (purchaseDate.isEmpty()) {

            Toast.makeText(
                this,
                "Please select purchase date",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (expiryDate.isEmpty()) {

            Toast.makeText(
                this,
                "Please select expiry date",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val updatedItem = Item(
            id = item.id,
            name = name,
            category = category,
            quantity = quantity,
            unit = unit,
            purchaseDate = purchaseDate,
            expiryDate = expiryDate,
            notes = notes
        )

        storageManager.updateItem(
            updatedItem
        )

        Toast.makeText(
            this,
            "Item updated",
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }

    private fun showDatePicker(
        onDateSelected: (String) -> Unit
    ) {

        val calendar =
            Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, year, month, day ->

                val selected =
                    Calendar.getInstance()

                selected.set(
                    year,
                    month,
                    day
                )

                val format =
                    SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                    )

                onDateSelected(
                    format.format(
                        selected.time
                    )
                )
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}