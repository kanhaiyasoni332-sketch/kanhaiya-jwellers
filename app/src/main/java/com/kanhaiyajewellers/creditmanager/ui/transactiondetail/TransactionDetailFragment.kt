package com.kanhaiyajewellers.creditmanager.ui.transactiondetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.kanhaiyajewellers.creditmanager.KanhaiyaJewellersApp
import com.kanhaiyajewellers.creditmanager.R
import com.kanhaiyajewellers.creditmanager.databinding.FragmentTransactionDetailBinding
import com.kanhaiyajewellers.creditmanager.ui.common.WhatsAppHelper
import com.kanhaiyajewellers.creditmanager.ui.ViewModelFactory
import com.kanhaiyajewellers.creditmanager.ui.addpayment.AddPaymentBottomSheet
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionDetailFragment : Fragment() {

    private var _binding: FragmentTransactionDetailBinding? = null
    private val binding get() = _binding!!

    private val args: TransactionDetailFragmentArgs by navArgs()

    private val viewModel: TransactionDetailViewModel by viewModels {
        ViewModelFactory((requireActivity().application as KanhaiyaJewellersApp).database)
    }

    private lateinit var paymentAdapter: PaymentAdapter
    private val createdAtFormatter = SimpleDateFormat("EEEE, dd MMMM yyyy, hh:mm a", Locale.getDefault())
    private val promiseDateFormatter = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
    private val inrFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        
        viewModel.loadTransaction(args.transactionId)
        observeTransaction()
        observePayments()
        setupAddPayment()
    }

    private fun setupRecyclerView() {
        paymentAdapter = PaymentAdapter()
        binding.rvPayments.apply {
            adapter = paymentAdapter
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        }
    }

    private fun observePayments() {
        viewModel.payments.observe(viewLifecycleOwner) { list ->
            paymentAdapter.submitList(list)
            
            val hasPayments = list.isNotEmpty()
            binding.tvPaymentHistoryLabel.visibility = if (hasPayments) View.VISIBLE else View.GONE
            binding.rvPayments.visibility = if (hasPayments) View.VISIBLE else View.GONE
        }
    }

    private fun observeTransaction() {
        viewModel.transaction.observe(viewLifecycleOwner) { t ->
            if (t == null) return@observe

            binding.tvCustomerName.text   = t.customerName
            binding.tvPhone.text          = t.phone
            binding.tvItemName.text       = t.itemName
            binding.tvItemWeight.text     = t.itemWeight.ifBlank { "—" }
            binding.tvCreatedAt.text      = createdAtFormatter.format(Date(t.createdAt))
            binding.tvPromiseDate.text    = t.promiseDate?.let { promiseDateFormatter.format(Date(it)) } ?: "—"
            binding.tvTotalAmount.text    = inrFormatter.format(t.totalAmount)
            binding.tvPaidAmount.text     = inrFormatter.format(t.paidAmount)
            binding.tvRemainingAmount.text = inrFormatter.format(t.remainingAmount)

            binding.btnWhatsappReminder.setOnClickListener {
                val amountText = NumberFormat.getCurrencyInstance(Locale("en", "IN")).format(t.remainingAmount)
                val message = "Dear ${t.customerName} ji, as promised, today is your payment date. " +
                    "Your pending amount is $amountText. Kindly pay today. - Kanhaiya Jewellers"
                WhatsAppHelper.openWhatsAppMessage(requireContext(), t.phone, message)
            }

            val isCompleted = t.remainingAmount <= 0.0
            binding.btnThankYouMessage.visibility = if (isCompleted) View.VISIBLE else View.GONE
            binding.btnThankYouMessage.isEnabled = t.phone.isNotBlank()
            binding.btnThankYouMessage.alpha = if (t.phone.isNotBlank()) 1f else 0.5f
            binding.btnThankYouMessage.setOnClickListener {
                val thanks = "Dear ${t.customerName} ji, you have cleared all your dues. " +
                    "Thank you for your trust. Visit again for more shopping 🙂 - Kanhaiya Jewellers"
                WhatsAppHelper.openWhatsAppMessage(requireContext(), t.phone, thanks)
            }

            if (t.status == "COMPLETED") {
                binding.tvStatus.text = "✓  COMPLETED"
                binding.tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_completed))
                binding.tvStatus.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_status_completed)
                binding.tvRemainingAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_completed))
                binding.btnAddPayment.isEnabled = false
                binding.btnAddPayment.alpha = 0.4f
            } else {
                binding.tvStatus.text = "●  PENDING"
                binding.tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_pending))
                binding.tvStatus.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_status_pending)
                binding.tvRemainingAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.gold_primary))
                binding.btnAddPayment.isEnabled = true
                binding.btnAddPayment.alpha = 1.0f
            }
        }
    }

    private fun setupAddPayment() {
        binding.btnAddPayment.setOnClickListener {
            val sheet = AddPaymentBottomSheet.newInstance(
                transactionId = args.transactionId,
                viewModel     = viewModel
            )
            sheet.show(childFragmentManager, AddPaymentBottomSheet.TAG)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
