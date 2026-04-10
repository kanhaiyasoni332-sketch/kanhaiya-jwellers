package com.kanhaiyajewellers.creditmanager.ui.addtransaction

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kanhaiyajewellers.creditmanager.KanhaiyaJewellersApp
import com.kanhaiyajewellers.creditmanager.databinding.FragmentAddTransactionBinding
import com.kanhaiyajewellers.creditmanager.reminders.PaymentReminderScheduler
import com.kanhaiyajewellers.creditmanager.ui.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTransactionFragment : Fragment() {

    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddTransactionViewModel by viewModels {
        ViewModelFactory((requireActivity().application as KanhaiyaJewellersApp).database)
    }
    private var selectedPromiseDate: Long? = null
    private val promiseDateFormatter = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAmountWatchers()
        setupPromiseDatePicker()
        observeRemaining()
        setupSaveButton()
    }

    private fun setupPromiseDatePicker() {
        val openPicker = {
            val calendar = Calendar.getInstance()
            selectedPromiseDate?.let { calendar.timeInMillis = it }
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val selected = Calendar.getInstance().apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        set(Calendar.HOUR_OF_DAY, 9)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    selectedPromiseDate = selected.timeInMillis
                    binding.etPromiseDate.setText(promiseDateFormatter.format(selected.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        binding.etPromiseDate.setOnClickListener { openPicker() }
        binding.tilPromiseDate.setEndIconOnClickListener { openPicker() }
    }

    private fun setupAmountWatchers() {
        binding.etTotalAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { viewModel.totalAmount.value = s.toString() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.etPaidAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { viewModel.paidAmount.value = s.toString() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun observeRemaining() {
        viewModel.remainingAmount.observe(viewLifecycleOwner) { remaining ->
            binding.tvRemainingValue.text = "₹%.2f".format(remaining)
        }
    }

    private fun setupSaveButton() {
        binding.btnSaveTransaction.setOnClickListener {
            val name     = binding.etCustomerName.text.toString().trim()
            val phone    = binding.etPhoneNumber.text.toString().trim()
            val itemName = binding.etItemName.text.toString().trim()
            val itemWeight = binding.etItemWeight.text.toString().trim()
            val totalStr = binding.etTotalAmount.text.toString().trim()
            val paidStr  = binding.etPaidAmount.text.toString().trim()

            // Basic validation
            if (name.isEmpty())        { binding.tilCustomerName.error = "Required"; return@setOnClickListener }
            if (phone.length < 10)     { binding.tilPhoneNumber.error = "Enter valid phone"; return@setOnClickListener }
            if (itemName.isEmpty())    { binding.tilItemName.error = "Required"; return@setOnClickListener }
            if (itemWeight.isEmpty())  { binding.tilItemWeight.error = "Required"; return@setOnClickListener }
            if (totalStr.isEmpty())    { binding.tilTotalAmount.error = "Required"; return@setOnClickListener }

            binding.tilCustomerName.error = null
            binding.tilPhoneNumber.error  = null
            binding.tilItemName.error     = null
            binding.tilItemWeight.error   = null
            binding.tilTotalAmount.error  = null

            val total = totalStr.toDoubleOrNull() ?: 0.0
            val paid  = paidStr.toDoubleOrNull()  ?: 0.0

            binding.btnSaveTransaction.isEnabled = false

            viewModel.saveTransaction(
                name     = name,
                phone    = phone,
                itemName = itemName,
                itemWeight = itemWeight,
                promiseDate = selectedPromiseDate,
                total    = total,
                paid     = paid,
                onSuccess = { transactionId ->
                    selectedPromiseDate?.let { promiseDate ->
                        PaymentReminderScheduler.schedulePromiseReminder(
                            context = requireContext(),
                            transactionId = transactionId,
                            triggerAtMillis = promiseDate
                        )
                    }
                    Toast.makeText(requireContext(), "Transaction saved!", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                },
                onError = { msg ->
                    binding.btnSaveTransaction.isEnabled = true
                    Toast.makeText(requireContext(), "Error: $msg", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
