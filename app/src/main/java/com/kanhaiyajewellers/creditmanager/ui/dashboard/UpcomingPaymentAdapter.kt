package com.kanhaiyajewellers.creditmanager.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kanhaiyajewellers.creditmanager.data.model.UpcomingPaymentItem
import com.kanhaiyajewellers.creditmanager.databinding.ItemUpcomingPaymentBinding
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Locale

class UpcomingPaymentAdapter : ListAdapter<UpcomingPaymentItem, UpcomingPaymentAdapter.ViewHolder>(DiffCallback()) {

    private val inr = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    inner class ViewHolder(private val binding: ItemUpcomingPaymentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UpcomingPaymentItem) {
            val today = LocalDate.now()
            val dueDate = Instant.ofEpochMilli(item.promiseDate).atZone(ZoneId.systemDefault()).toLocalDate()
            val diff = ChronoUnit.DAYS.between(today, dueDate)

            binding.tvCustomerName.text = item.customerName
            binding.tvPendingAmount.text = inr.format(item.pendingAmount)
            binding.tvPromiseDate.text = when {
                diff == 0L -> "Due today"
                diff > 0L -> "Due in $diff day(s)"
                else -> "Overdue by ${kotlin.math.abs(diff)} day(s)"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUpcomingPaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class DiffCallback : DiffUtil.ItemCallback<UpcomingPaymentItem>() {
        override fun areItemsTheSame(oldItem: UpcomingPaymentItem, newItem: UpcomingPaymentItem): Boolean =
            oldItem.transactionId == newItem.transactionId

        override fun areContentsTheSame(oldItem: UpcomingPaymentItem, newItem: UpcomingPaymentItem): Boolean =
            oldItem == newItem
    }
}
