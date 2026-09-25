package com.example.shelfsense

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class StorageManager(private val context: Context) {

    companion object {
        private const val PREF_NAME = "ShelfSensePrefs"
        private const val ITEMS_KEY = "items"
    }

    private val preferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveItems(items: List<Item>) {

        val jsonArray = JSONArray()

        for (item in items) {

            val jsonObject = JSONObject()

            jsonObject.put("id", item.id)
            jsonObject.put("name", item.name)
            jsonObject.put("category", item.category)
            jsonObject.put("quantity", item.quantity)
            jsonObject.put("unit", item.unit)
            jsonObject.put("purchaseDate", item.purchaseDate)
            jsonObject.put("expiryDate", item.expiryDate)
            jsonObject.put("notes", item.notes)

            jsonArray.put(jsonObject)
        }

        preferences.edit()
            .putString(ITEMS_KEY, jsonArray.toString())
            .apply()
    }

    fun getItems(): MutableList<Item> {

        val items = mutableListOf<Item>()

        val jsonString = preferences.getString(
            ITEMS_KEY,
            "[]"
        )

        val jsonArray = JSONArray(jsonString)

        for (i in 0 until jsonArray.length()) {

            val jsonObject = jsonArray.getJSONObject(i)

            val item = Item(
                id = jsonObject.getString("id"),
                name = jsonObject.getString("name"),
                category = jsonObject.getString("category"),
                quantity = jsonObject.getInt("quantity"),
                unit = jsonObject.getString("unit"),
                purchaseDate = jsonObject.getString("purchaseDate"),
                expiryDate = jsonObject.getString("expiryDate"),
                notes = jsonObject.getString("notes")
            )

            items.add(item)
        }

        return items
    }

    fun addItem(item: Item) {

        val items = getItems()

        items.add(item)

        saveItems(items)
    }

    fun updateItem(item: Item) {

        val items = getItems()

        val index = items.indexOfFirst {
            it.id == item.id
        }

        if (index != -1) {
            items[index] = item
            saveItems(items)
        }
    }

    fun deleteItem(itemId: String) {

        val items = getItems()

        items.removeAll {
            it.id == itemId
        }

        saveItems(items)
    }
}