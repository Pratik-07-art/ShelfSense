package com.example.shelfsense

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter(
    private var items: List<Item>,
    private val onItemClick: (Item) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val name: TextView = view.findViewById(R.id.tvItemName)
        val category: TextView = view.findViewById(R.id.tvCategory)
        val quantity: TextView = view.findViewById(R.id.tvQuantity)
        val expiry: TextView = view.findViewById(R.id.tvExpiry)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inventory, parent, false)

        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ItemViewHolder,
        position: Int
    ) {

        val item = items[position]

        holder.name.text = item.name
        holder.category.text = item.category

        holder.quantity.text =
            "Quantity: ${item.quantity} ${item.unit}"

        val days = ExpiryUtils.getDaysUntilExpiry(item.expiryDate)

        holder.expiry.text = when {

            days < 0 ->
                "Expired • ${item.expiryDate}"

            days == 0L ->
                "Expires today • ${item.expiryDate}"

            days == 1L ->
                "Expires tomorrow • ${item.expiryDate}"

            days <= 7 ->
                "Expires in $days days • ${item.expiryDate}"

            else ->
                "Expires in $days days • ${item.expiryDate}"
        }

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItems(newItems: List<Item>) {
        items = newItems
        notifyDataSetChanged()
    }
}