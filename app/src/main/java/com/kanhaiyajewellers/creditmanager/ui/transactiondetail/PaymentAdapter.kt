package com.kanhaiyajewellers.creditmanager.ui.transactiondetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kanhaiyajewellers.creditmanager.data.db.entity.Payment
import com.kanhaiyajewellers.creditmanager.databinding.ItemPaymentBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PaymentAdapter : ListAdapter<Payment, PaymentAdapter.PaymentViewHolder>(DiffCallback) {

    private val dateFormat = SimpleDateFormat("MMM d, yyyy • hh:mm a", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val binding = ItemPaymentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PaymentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PaymentViewHolder(private val binding: ItemPaymentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(payment: Payment) {
            binding.tvPaymentAmount.text = "+₹%.2f".format(payment.amount)
            binding.tvDate.text = dateFormat.format(Date(payment.createdAt))
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Payment>() {
            override fun areItemsTheSame(oldItem: Payment, newItem: Payment): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Payment, newItem: Payment): Boolean {
                return oldItem == newItem
            }
        }
    }
}
