package com.kanhaiyajewellers.creditmanager.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kanhaiyajewellers.creditmanager.R
import com.kanhaiyajewellers.creditmanager.data.model.TransactionWithCustomer
import com.kanhaiyajewellers.creditmanager.databinding.ItemTransactionBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionAdapter(
    private val onClick: (TransactionWithCustomer) -> Unit
) : ListAdapter<TransactionWithCustomer, TransactionAdapter.ViewHolder>(DiffCallback()) {

    private val createdAtFormatter = SimpleDateFormat("EEE, dd MMM yyyy, hh:mm a", Locale.getDefault())
    private val inrFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    inner class ViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TransactionWithCustomer) {
            binding.tvName.text = item.customerName
            binding.tvPhone.text = item.phone
            binding.tvCreatedAt.text = createdAtFormatter.format(Date(item.createdAt))
            binding.tvAmount.text = inrFormatter.format(item.remainingAmount)

            val amountColor = if (item.status == "COMPLETED") {
                ContextCompat.getColor(binding.root.context, R.color.status_completed)
            } else {
                ContextCompat.getColor(binding.root.context, R.color.gold_primary)
            }
            binding.tvAmount.setTextColor(amountColor)

            // Status dot
            val dotColor = if (item.status == "COMPLETED") {
                ContextCompat.getColor(binding.root.context, R.color.status_completed)
            } else {
                ContextCompat.getColor(binding.root.context, R.color.status_pending)
            }
            binding.viewStatusDot.setBackgroundColor(dotColor)

            binding.root.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<TransactionWithCustomer>() {
        override fun areItemsTheSame(oldItem: TransactionWithCustomer, newItem: TransactionWithCustomer) =
            oldItem.transactionId == newItem.transactionId

        override fun areContentsTheSame(oldItem: TransactionWithCustomer, newItem: TransactionWithCustomer) =
            oldItem == newItem
    }
}
