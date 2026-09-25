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
import java.util.UUID

class AddItemActivity : AppCompatActivity() {

    private lateinit var storageManager: StorageManager

    private var purchaseDate = ""
    private var expiryDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_item)

        storageManager = StorageManager(this)

        setupCategorySpinner()
        setupUnitSpinner()
        setupDatePickers()
        setupButtons()
    }

    private fun setupCategorySpinner() {

        val categories = arrayOf(
            "Food/Grocery",
            "Personal Care",
            "Medicine",
            "Cleaning",
            "Other"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        findViewById<Spinner>(
            R.id.spinnerCategory
        ).adapter = adapter
    }

    private fun setupUnitSpinner() {

        val units = arrayOf(
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

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            units
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        findViewById<Spinner>(
            R.id.spinnerUnit
        ).adapter = adapter
    }

    private fun setupDatePickers() {

        val purchase =
            findViewById<TextView>(R.id.tvPurchaseDate)

        val expiry =
            findViewById<TextView>(R.id.tvExpiryDate)

        purchase.setOnClickListener {

            showDatePicker { date ->

                purchaseDate = date

                purchase.text = date
            }
        }

        expiry.setOnClickListener {

            showDatePicker { date ->

                expiryDate = date

                expiry.text = date
            }
        }
    }

    private fun setupButtons() {

        findViewById<TextView>(
            R.id.btnBack
        ).setOnClickListener {

            finish()
        }

        findViewById<Button>(
            R.id.btnSaveItem
        ).setOnClickListener {

            saveItem()
        }
    }

    private fun saveItem() {

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

        // Validation

        if (name.isEmpty()) {

            Toast.makeText(
                this,
                "Please enter item name",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (quantityText.isEmpty()) {

            Toast.makeText(
                this,
                "Please enter quantity",
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

        // Create item

        val item = Item(
            id = UUID.randomUUID().toString(),
            name = name,
            category = category,
            quantity = quantity,
            unit = unit,
            purchaseDate = purchaseDate,
            expiryDate = expiryDate,
            notes = notes
        )

        storageManager.addItem(item)

        Toast.makeText(
            this,
            "Item saved successfully",
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