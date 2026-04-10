package com.kanhaiyajewellers.creditmanager.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kanhaiyajewellers.creditmanager.R
import com.kanhaiyajewellers.creditmanager.data.model.CustomerWithTotals
import com.kanhaiyajewellers.creditmanager.databinding.ItemCustomerBinding
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Locale

class CustomerAdapter(
    private val onItemClick: (CustomerWithTotals) -> Unit,
    private val onThankYouClick: (CustomerWithTotals) -> Unit
) : ListAdapter<CustomerWithTotals, CustomerAdapter.CustomerViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding = ItemCustomerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CustomerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val customer = getItem(position)
        holder.bind(customer)
    }

    inner class CustomerViewHolder(private val binding: ItemCustomerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(customer: CustomerWithTotals) {
            val inrFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
            val today = LocalDate.now()
            val promiseDate = customer.nextPromiseDate?.let {
                Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
            }
            val dayDiff = promiseDate?.let { ChronoUnit.DAYS.between(today, it) }
            val isOverdue = dayDiff != null && dayDiff < 0
            val isDueToday = dayDiff == 0L

            binding.tvCustomerName.text = customer.customerName
            binding.tvPhone.text = customer.phone

            if (customer.totalPending > 0) {
                binding.tvTotalPending.text = inrFormatter.format(customer.totalPending)
                binding.tvTotalPending.setTextColor(ContextCompat.getColor(binding.root.context, R.color.gold_primary))
            } else {
                binding.tvTotalPending.text = inrFormatter.format(0.0)
                binding.tvTotalPending.setTextColor(ContextCompat.getColor(binding.root.context, R.color.status_completed))
            }

            binding.tvTotalPaid.text = "Paid: ${inrFormatter.format(customer.totalPaid)}"
            binding.tvDueTodayBadge.visibility = if (isDueToday) android.view.View.VISIBLE else android.view.View.GONE
            val isPaid = customer.totalPending <= 0.0
            val canSendThanks = isPaid && customer.phone.isNotBlank()
            binding.tvPaidBadge.visibility = if (isPaid) android.view.View.VISIBLE else android.view.View.GONE
            binding.btnWhatsAppThankYou.visibility = if (canSendThanks) android.view.View.VISIBLE else android.view.View.GONE
            binding.btnWhatsAppThankYou.setOnClickListener { onThankYouClick(customer) }

            if (dayDiff == null) {
                binding.tvDueStatus.visibility = android.view.View.GONE
                binding.tvCustomerName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.text_primary))
            } else {
                binding.tvDueStatus.visibility = android.view.View.VISIBLE
                binding.tvDueStatus.text = when {
                    dayDiff < 0 -> "Overdue by ${kotlin.math.abs(dayDiff)} day(s)"
                    dayDiff == 0L -> binding.root.context.getString(R.string.due_today)
                    else -> "Due in $dayDiff day(s)"
                }
                val dueColor = if (isOverdue) R.color.status_pending else R.color.text_secondary
                binding.tvDueStatus.setTextColor(ContextCompat.getColor(binding.root.context, dueColor))
                val nameColor = if (isOverdue) R.color.status_pending else R.color.text_primary
                binding.tvCustomerName.setTextColor(ContextCompat.getColor(binding.root.context, nameColor))
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<CustomerWithTotals>() {
            override fun areItemsTheSame(
                oldItem: CustomerWithTotals,
                newItem: CustomerWithTotals
            ): Boolean {
                return oldItem.customerId == newItem.customerId
            }

            override fun areContentsTheSame(
                oldItem: CustomerWithTotals,
                newItem: CustomerWithTotals
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
