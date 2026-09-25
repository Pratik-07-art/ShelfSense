package com.example.shelfsense

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var storageManager: StorageManager
    private lateinit var expiryCalendarView: ExpiryCalendarView

    private lateinit var tvInventoryCount: TextView
    private lateinit var tvExpiringCount: TextView
    private lateinit var tvLowStockCount: TextView
    private lateinit var tvExpiredCount: TextView

    private lateinit var tvUpcomingItem1: TextView
    private lateinit var tvUpcomingDays1: TextView
    private lateinit var tvUpcomingItem2: TextView
    private lateinit var tvUpcomingDays2: TextView
    private lateinit var tvUpcomingItem3: TextView
    private lateinit var tvUpcomingDays3: TextView

    private lateinit var tvCalendarMonth: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        storageManager = StorageManager(this)

        initializeViews()
        setupClickListeners()
        setupCalendar()

        requestNotificationPermission()
        scheduleNotifications()
    }

    override fun onResume() {
        super.onResume()

        if (::storageManager.isInitialized) {
            updateDashboard()
        }
    }

    private fun initializeViews() {

        tvInventoryCount =
            findViewById(R.id.tvInventoryCount)

        tvExpiringCount =
            findViewById(R.id.tvExpiringCount)

        tvLowStockCount =
            findViewById(R.id.tvLowStockCount)

        tvExpiredCount =
            findViewById(R.id.tvExpiredCount)

        tvUpcomingItem1 =
            findViewById(R.id.tvUpcomingItem1)

        tvUpcomingDays1 =
            findViewById(R.id.tvUpcomingDays1)

        tvUpcomingItem2 =
            findViewById(R.id.tvUpcomingItem2)

        tvUpcomingDays2 =
            findViewById(R.id.tvUpcomingDays2)

        tvUpcomingItem3 =
            findViewById(R.id.tvUpcomingItem3)

        tvUpcomingDays3 =
            findViewById(R.id.tvUpcomingDays3)

        tvCalendarMonth =
            findViewById(R.id.tvCalendarMonth)

        expiryCalendarView =
            findViewById(R.id.calendarView)
    }

    private fun setupClickListeners() {

        findViewById<View>(R.id.cardInventory).setOnClickListener {
            startActivity(
                Intent(this, InventoryActivity::class.java)
            )
        }

        findViewById<View>(R.id.cardExpiring).setOnClickListener {
            startActivity(
                Intent(this, ExpiringItemsActivity::class.java)
            )
        }

        findViewById<View>(R.id.tvViewAll).setOnClickListener {
            startActivity(
                Intent(this, ExpiringItemsActivity::class.java)
            )
        }

        findViewById<View>(R.id.cardLowStock).setOnClickListener {
            val intent = Intent(this, InventoryActivity::class.java)
            intent.putExtra("FILTER_TYPE", "LOW_STOCK")
            startActivity(intent)
        }

        findViewById<View>(R.id.cardExpired).setOnClickListener {
            val intent = Intent(this, InventoryActivity::class.java)
            intent.putExtra("FILTER_TYPE", "EXPIRED")
            startActivity(intent)
        }

        findViewById<View>(R.id.btnAddItem).setOnClickListener {
            startActivity(
                Intent(this, AddItemActivity::class.java)
            )
        }
    }

    private fun setupCalendar() {

        updateCalendar()

        findViewById<TextView>(
            R.id.btnPreviousMonth
        ).setOnClickListener {

            expiryCalendarView.previousMonth()

            updateCalendar()
        }

        findViewById<TextView>(
            R.id.btnNextMonth
        ).setOnClickListener {

            expiryCalendarView.nextMonth()

            updateCalendar()
        }

        expiryCalendarView.onDateClick = { selectedDate ->

            val selectedItems =
                storageManager.getItems()
                    .filter {
                        it.expiryDate == selectedDate
                    }

            if (selectedItems.isEmpty()) {

                android.widget.Toast.makeText(
                    this,
                    "No items expire on $selectedDate",
                    android.widget.Toast.LENGTH_SHORT
                ).show()

            } else {

                val names =
                    selectedItems.joinToString("\n") {
                        "• ${it.name}"
                    }

                android.app.AlertDialog.Builder(this)
                    .setTitle(
                        "Expiry: $selectedDate"
                    )
                    .setMessage(names)
                    .setPositiveButton(
                        "OK",
                        null
                    )
                    .show()
            }
        }
    }

    private fun updateCalendar() {

        tvCalendarMonth.text =
            expiryCalendarView.getMonthYear()

        expiryCalendarView.setItems(
            storageManager.getItems()
        )
    }

    private fun updateDashboard() {

        val items = storageManager.getItems()

        tvInventoryCount.text =
            items.size.toString()

        val expiringItems = items.filter {
            ExpiryUtils.isExpiringSoon(it)
        }

        tvExpiringCount.text =
            expiringItems.size.toString()

        val lowStockItems = items.filter {
            ExpiryUtils.isLowStock(it)
        }

        tvLowStockCount.text =
            lowStockItems.size.toString()

        val expiredItems = items.filter {
            ExpiryUtils.isExpired(it)
        }

        tvExpiredCount.text =
            expiredItems.size.toString()

        updateUpcomingItems(items)

        updateCalendar()
    }

    private fun updateUpcomingItems(
        items: List<Item>
    ) {

        val upcomingItems = items
            .filter {
                !ExpiryUtils.isExpired(it)
            }
            .sortedBy {
                ExpiryUtils.getDaysUntilExpiry(
                    it.expiryDate
                )
            }
            .take(3)

        if (upcomingItems.isNotEmpty()) {

            val item = upcomingItems[0]

            tvUpcomingItem1.text =
                item.name

            tvUpcomingDays1.text =
                ExpiryUtils.getExpiryStatus(item)

        } else {

            tvUpcomingItem1.text =
                "No items"

            tvUpcomingDays1.text =
                ""
        }

        if (upcomingItems.size >= 2) {

            val item = upcomingItems[1]

            tvUpcomingItem2.text =
                item.name

            tvUpcomingDays2.text =
                ExpiryUtils.getExpiryStatus(item)

        } else {

            tvUpcomingItem2.text =
                "No items"

            tvUpcomingDays2.text =
                ""
        }

        if (upcomingItems.size >= 3) {

            val item = upcomingItems[2]

            tvUpcomingItem3.text =
                item.name

            tvUpcomingDays3.text =
                ExpiryUtils.getExpiryStatus(item)

        } else {

            tvUpcomingItem3.text =
                "No items"

            tvUpcomingDays3.text =
                ""
        }
    }
    private fun scheduleNotifications() {

        val workRequest =
            PeriodicWorkRequestBuilder<ExpiryNotificationWorker>(
                1,
                TimeUnit.DAYS
            ).build()

        WorkManager
            .getInstance(this)
            .enqueueUniquePeriodicWork(
                "ShelfSenseDailyNotifications",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
    }
    private fun requestNotificationPermission() {

        if (
            android.os.Build.VERSION.SDK_INT >= 33
        ) {

            requestPermissions(
                arrayOf(
                    android.Manifest.permission.POST_NOTIFICATIONS
                ),
                101
            )
        }
    }
}