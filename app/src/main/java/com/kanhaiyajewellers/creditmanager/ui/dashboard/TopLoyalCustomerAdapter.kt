package com.kanhaiyajewellers.creditmanager.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kanhaiyajewellers.creditmanager.data.model.CustomerWithTotals
import com.kanhaiyajewellers.creditmanager.databinding.ItemTopLoyalCustomerBinding
import java.text.NumberFormat
import java.util.Locale

class TopLoyalCustomerAdapter(
    private val onClick: (CustomerWithTotals) -> Unit
) : ListAdapter<CustomerWithTotals, TopLoyalCustomerAdapter.ViewHolder>(DiffCallback()) {

    private val inr = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    inner class ViewHolder(private val binding: ItemTopLoyalCustomerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CustomerWithTotals) {
            binding.tvCustomerName.text = item.customerName
            binding.tvMeta.text = "Loyal Customer • ${item.completedPayments} completed payments"
            binding.tvTotalValue.text = inr.format(item.totalTransactionValue)
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTopLoyalCustomerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class DiffCallback : DiffUtil.ItemCallback<CustomerWithTotals>() {
        override fun areItemsTheSame(oldItem: CustomerWithTotals, newItem: CustomerWithTotals): Boolean =
            oldItem.customerId == newItem.customerId

        override fun areContentsTheSame(oldItem: CustomerWithTotals, newItem: CustomerWithTotals): Boolean =
            oldItem == newItem
    }
}
