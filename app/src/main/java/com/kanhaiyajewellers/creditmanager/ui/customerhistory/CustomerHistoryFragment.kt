package com.kanhaiyajewellers.creditmanager.ui.customerhistory

import android.graphics.Rect
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kanhaiyajewellers.creditmanager.KanhaiyaJewellersApp
import com.kanhaiyajewellers.creditmanager.R
import com.kanhaiyajewellers.creditmanager.databinding.FragmentCustomerHistoryBinding
import com.kanhaiyajewellers.creditmanager.ui.ViewModelFactory
import com.kanhaiyajewellers.creditmanager.ui.dashboard.TransactionAdapter
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CustomerHistoryFragment : Fragment() {

    private var _binding: FragmentCustomerHistoryBinding? = null
    private val binding get() = _binding!!

    private val args: CustomerHistoryFragmentArgs by navArgs()

    private val viewModel: CustomerHistoryViewModel by viewModels {
        ViewModelFactory((requireActivity().application as KanhaiyaJewellersApp).database)
    }

    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvCustomerName.text = args.customerName
        binding.tvPhone.text = args.customerPhone
        binding.tvTotalPending.text = "₹%.2f".format(args.totalPending)
        binding.tvTotalPaid.text = "₹%.2f".format(args.totalPaid)

        setupRecyclerView()
        observeData()
        setupExportButton()
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter { transaction ->
            val action = CustomerHistoryFragmentDirections
                .actionCustomerHistoryFragmentToTransactionDetailFragment(transaction.transactionId)
            findNavController().navigate(action)
        }
        binding.rvTransactions.apply {
            adapter = transactionAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(SpaceItemDecoration(8))
        }
    }

    private inner class SpaceItemDecoration(private val spaceDP: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val spacePx = (spaceDP * view.context.resources.displayMetrics.density).toInt()
            outRect.bottom = spacePx
        }
    }

    private fun observeData() {
        viewModel.getTransactionsForCustomer(args.customerId).observe(viewLifecycleOwner) { list ->
            transactionAdapter.submitList(list)
        }
    }

    private fun setupExportButton() {
        binding.btnExportLedger.setOnClickListener {
            exportLedgerPdf()
        }
    }

    private fun exportLedgerPdf() {
        setExportLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val data = withContext(Dispatchers.IO) {
                    viewModel.getLedgerExportData(
                        customerId = args.customerId,
                        fallbackName = args.customerName,
                        fallbackPhone = args.customerPhone
                    )
                }
                if (data.entries.isEmpty()) {
                    Toast.makeText(requireContext(), getString(R.string.no_ledger_entries), Toast.LENGTH_SHORT).show()
                }
                val pdfFile = withContext(Dispatchers.IO) {
                    LedgerPdfGenerator.generate(requireContext(), data)
                }
                Toast.makeText(requireContext(), getString(R.string.ledger_export_success), Toast.LENGTH_SHORT).show()
                showShareOptions(pdfFile)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Export failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            } finally {
                setExportLoading(false)
            }
        }
    }

    private fun showShareOptions(pdfFile: File) {
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            pdfFile
        )
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.export_ledger_pdf))
            .setItems(arrayOf(getString(R.string.share_whatsapp), getString(R.string.save_pdf))) { _, which ->
                when (which) {
                    0 -> shareViaWhatsApp(uri)
                    1 -> saveAndOpen(pdfFile, uri)
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun shareViaWhatsApp(uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "Customer ledger PDF")
            setPackage("com.whatsapp")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try {
            startActivity(intent)
        } catch (_: Exception) {
            openWithChooser(uri)
        }
    }

    private fun openWithChooser(uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, getString(R.string.save_pdf)))
    }

    private fun saveAndOpen(file: File, uri: Uri) {
        Toast.makeText(requireContext(), "${getString(R.string.ledger_export_success)}\n${file.absolutePath}", Toast.LENGTH_LONG).show()
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try {
            startActivity(intent)
        } catch (_: Exception) {
            openWithChooser(uri)
        }
    }

    private fun setExportLoading(isLoading: Boolean) {
        binding.progressExportLedger.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnExportLedger.isEnabled = !isLoading
        binding.btnExportLedger.text = if (isLoading) getString(R.string.ledger_exporting) else getString(R.string.export_ledger_pdf)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
