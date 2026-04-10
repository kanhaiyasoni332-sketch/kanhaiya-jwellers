package com.kanhaiyajewellers.creditmanager.ui.customerhistory

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kanhaiyajewellers.creditmanager.KanhaiyaJewellersApp
import com.kanhaiyajewellers.creditmanager.databinding.FragmentCustomerHistoryBinding
import com.kanhaiyajewellers.creditmanager.ui.ViewModelFactory
import com.kanhaiyajewellers.creditmanager.ui.dashboard.TransactionAdapter

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
