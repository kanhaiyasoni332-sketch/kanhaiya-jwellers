package com.kanhaiyajewellers.creditmanager.ui.addpayment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kanhaiyajewellers.creditmanager.R
import com.kanhaiyajewellers.creditmanager.databinding.DialogAddPaymentBinding
import com.kanhaiyajewellers.creditmanager.ui.common.WhatsAppHelper
import com.kanhaiyajewellers.creditmanager.ui.transactiondetail.TransactionDetailViewModel

class AddPaymentBottomSheet : BottomSheetDialogFragment() {

    private var _binding: DialogAddPaymentBinding? = null
    private val binding get() = _binding!!

    private var viewModel: TransactionDetailViewModel? = null

    companion object {
        const val TAG = "AddPaymentBottomSheet"
        private const val ARG_TRANSACTION_ID = "transactionId"

        fun newInstance(transactionId: Long, viewModel: TransactionDetailViewModel): AddPaymentBottomSheet {
            return AddPaymentBottomSheet().apply {
                this.viewModel = viewModel
                arguments = Bundle().apply {
                    putLong(ARG_TRANSACTION_ID, transactionId)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnConfirmPayment.setOnClickListener {
            val amountStr = binding.etPaymentAmount.text.toString().trim()
            val amount = amountStr.toDoubleOrNull()

            if (amount == null || amount <= 0) {
                binding.tilPaymentAmount.error = "Enter a valid amount"
                return@setOnClickListener
            }

            binding.tilPaymentAmount.error = null
            binding.btnConfirmPayment.isEnabled = false

            viewModel?.addPayment(
                amount = amount,
                onSuccess = { completionInfo ->
                    Toast.makeText(requireContext(), "Payment recorded!", Toast.LENGTH_SHORT).show()
                    if (completionInfo == null) {
                        dismiss()
                        return@addPayment
                    }
                    showCompletionDialog(
                        customerName = completionInfo.customerName,
                        phone = completionInfo.phone,
                        isLoyalCustomer = completionInfo.isLoyalCustomer
                    )
                },
                onError = { msg ->
                    binding.btnConfirmPayment.isEnabled = true
                    Toast.makeText(requireContext(), "Error: $msg", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun showCompletionDialog(customerName: String, phone: String, isLoyalCustomer: Boolean) {
        val message = if (isLoyalCustomer) {
            "$customerName has cleared all dues\n\n${getString(R.string.loyal_customer_unlocked)}\n${getString(R.string.offer_discount_suggestion)}"
        } else {
            "$customerName has cleared all dues"
        }
        val thankYouText = "Dear $customerName ji, you have cleared all your dues. " +
            "Thank you for your trust. Visit again for more shopping 🙂 - Kanhaiya Jewellers"
        val offerText = "Dear $customerName ji, thank you for clearing all dues on time 🙏 " +
            "As a valued customer, we are offering you a special discount on your next purchase. " +
            "Visit again at Kanhaiya Jewellers 💎"
        val ctaText = if (isLoyalCustomer) getString(R.string.send_offer_whatsapp) else getString(R.string.send_thank_you_message)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.payment_completed))
            .setMessage(message)
            .setNegativeButton(android.R.string.ok) { d, _ ->
                d.dismiss()
                dismiss()
            }
            .setPositiveButton(ctaText, null)
            .create()

        dialog.setOnShowListener {
            val positive = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
            positive.isEnabled = phone.isNotBlank()
            positive.setOnClickListener {
                val selectedMessage = if (isLoyalCustomer) offerText else thankYouText
                WhatsAppHelper.openWhatsAppMessage(requireContext(), phone, selectedMessage)
                dialog.dismiss()
                dismiss()
            }
        }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
