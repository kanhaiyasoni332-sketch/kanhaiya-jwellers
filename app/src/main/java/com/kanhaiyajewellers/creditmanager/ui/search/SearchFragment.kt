package com.kanhaiyajewellers.creditmanager.ui.search

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kanhaiyajewellers.creditmanager.KanhaiyaJewellersApp
import com.kanhaiyajewellers.creditmanager.data.model.CustomerWithTotals
import com.kanhaiyajewellers.creditmanager.databinding.FragmentSearchBinding
import com.kanhaiyajewellers.creditmanager.ui.common.WhatsAppHelper
import com.kanhaiyajewellers.creditmanager.ui.ViewModelFactory
import com.kanhaiyajewellers.creditmanager.ui.dashboard.CustomerAdapter

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels {
        ViewModelFactory((requireActivity().application as KanhaiyaJewellersApp).database)
    }

    private lateinit var customerAdapter: CustomerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchView()
        observeResults()
    }

    private fun setupRecyclerView() {
        customerAdapter = CustomerAdapter(
            onItemClick = { customer ->
                val action = SearchFragmentDirections
                    .actionSearchFragmentToCustomerHistoryFragment(
                        customerId = customer.customerId,
                        customerName = customer.customerName,
                        customerPhone = customer.phone,
                        totalPending = customer.totalPending.toFloat(),
                        totalPaid = customer.totalPaid.toFloat()
                    )
                findNavController().navigate(action)
            },
            onThankYouClick = { customer ->
                sendThankYou(customer)
            }
        )
        binding.rvSearchResults.apply {
            adapter = customerAdapter
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

    private fun setupSearchView() {
        // Fix text color for search view
        val searchEditText = binding.searchView.findViewById<android.widget.EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), com.kanhaiyajewellers.creditmanager.R.color.text_primary))
        searchEditText.setHintTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), com.kanhaiyajewellers.creditmanager.R.color.text_secondary))

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.search(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.search(newText.orEmpty())
                return true
            }
        })

        // Trigger initial load of all records
        viewModel.search("")
    }

    private fun observeResults() {
        viewModel.searchResults.observe(viewLifecycleOwner) { list ->
            customerAdapter.submitList(list)
            binding.tvNoResults.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            binding.rvSearchResults.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun sendThankYou(customer: CustomerWithTotals) {
        val message = "Dear ${customer.customerName} ji, you have cleared all your dues. " +
            "Thank you for your trust. Visit again for more shopping 🙂 - Kanhaiya Jewellers"
        WhatsAppHelper.openWhatsAppMessage(requireContext(), customer.phone, message)
    }
}
