package com.kanhaiyajewellers.creditmanager.ui.dashboard

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kanhaiyajewellers.creditmanager.KanhaiyaJewellersApp
import com.kanhaiyajewellers.creditmanager.data.model.CustomerWithTotals
import com.kanhaiyajewellers.creditmanager.databinding.FragmentDashboardBinding
import com.kanhaiyajewellers.creditmanager.ui.common.WhatsAppHelper
import com.kanhaiyajewellers.creditmanager.ui.ViewModelFactory

class DashboardFragment : Fragment() {
    private enum class CustomerFilter { ALL, PENDING, LOYAL }

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels {
        ViewModelFactory((requireActivity().application as KanhaiyaJewellersApp).database)
    }

    private lateinit var customerAdapter: CustomerAdapter
    private lateinit var upcomingPaymentAdapter: UpcomingPaymentAdapter
    private lateinit var topLoyalAdapter: TopLoyalCustomerAdapter
    private var allCustomers: List<CustomerWithTotals> = emptyList()
    private var selectedFilter: CustomerFilter = CustomerFilter.ALL

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupCustomerFilter()
        setupMonthlyAnalysis()
        observeData()
        setupFab()
    }

    private fun setupMonthlyAnalysis() {
        val defaultColor = androidx.core.content.ContextCompat.getColor(requireContext(), com.kanhaiyajewellers.creditmanager.R.color.text_secondary)
        val activeColor = androidx.core.content.ContextCompat.getColor(requireContext(), com.kanhaiyajewellers.creditmanager.R.color.gold_primary)

        val setFilterActive = { activeTextView: android.widget.TextView, inactive1: android.widget.TextView, inactive2: android.widget.TextView ->
            activeTextView.setTextColor(activeColor)
            inactive1.setTextColor(defaultColor)
            inactive2.setTextColor(defaultColor)
        }

        binding.tvFilterWeek.setOnClickListener {
            setFilterActive(binding.tvFilterWeek, binding.tvFilterMonth, binding.tvFilterQuarter)
            binding.lineChartView.setData(
                credit = listOf(20f, 40f, 35f, 55f, 50f, 70f, 65f),
                received = listOf(15f, 30f, 25f, 45f, 40f, 60f, 55f)
            )
            binding.tvInsight.text = "You collected ₹2,500 more than yesterday"
        }

        binding.tvFilterMonth.setOnClickListener {
            setFilterActive(binding.tvFilterMonth, binding.tvFilterWeek, binding.tvFilterQuarter)
            binding.lineChartView.setData(
                credit = listOf(10f, 30f, 25f, 45f, 40f, 60f, 55f),
                received = listOf(5f, 20f, 15f, 35f, 30f, 50f, 45f)
            )
            binding.tvInsight.text = "You collected ₹8,500 more than last week"
        }

        binding.tvFilterQuarter.setOnClickListener {
            setFilterActive(binding.tvFilterQuarter, binding.tvFilterWeek, binding.tvFilterMonth)
            binding.lineChartView.setData(
                credit = listOf(50f, 130f, 125f, 145f, 140f, 160f, 155f),
                received = listOf(45f, 120f, 115f, 135f, 130f, 150f, 145f)
            )
            binding.tvInsight.text = "You collected ₹25,000 more than last quarter"
        }
    }

    private fun setupRecyclerView() {
        customerAdapter = CustomerAdapter(
            onItemClick = { customer ->
                val action = DashboardFragmentDirections
                    .actionDashboardFragmentToCustomerHistoryFragment(
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
        binding.rvTransactions.apply {
            adapter = customerAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
            addItemDecoration(SpaceItemDecoration(8))
        }

        upcomingPaymentAdapter = UpcomingPaymentAdapter()
        binding.rvUpcomingPayments.apply {
            adapter = upcomingPaymentAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
            addItemDecoration(SpaceItemDecoration(8))
        }

        topLoyalAdapter = TopLoyalCustomerAdapter { customer ->
            val action = DashboardFragmentDirections
                .actionDashboardFragmentToCustomerHistoryFragment(
                    customerId = customer.customerId,
                    customerName = customer.customerName,
                    customerPhone = customer.phone,
                    totalPending = customer.totalPending.toFloat(),
                    totalPaid = customer.totalPaid.toFloat()
                )
            findNavController().navigate(action)
        }
        binding.rvTopLoyalCustomers.apply {
            adapter = topLoyalAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
            addItemDecoration(SpaceItemDecoration(8))
        }
    }

    /** Adds vertical spacing between RecyclerView items in dp. */
    private inner class SpaceItemDecoration(private val spaceDP: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val spacePx = (spaceDP * view.context.resources.displayMetrics.density).toInt()
            outRect.bottom = spacePx
        }
    }

    private fun observeData() {
        viewModel.recentCustomers.observe(viewLifecycleOwner) { list ->
            allCustomers = list
            applyCustomerFilter()
        }

        viewModel.totalPendingAmount.observe(viewLifecycleOwner) { amount ->
            binding.tvPendingAmount.text = "₹%.2f".format(amount ?: 0.0)
        }

        viewModel.pendingCustomersCount.observe(viewLifecycleOwner) { count ->
            binding.tvPendingCount.text = (count ?: 0).toString()
        }

        viewModel.completedTransactionsCount.observe(viewLifecycleOwner) { count ->
            binding.tvCompletedCount.text = (count ?: 0).toString()
        }

        viewModel.upcomingPayments.observe(viewLifecycleOwner) { list ->
            upcomingPaymentAdapter.submitList(list)
            val visible = list.isNotEmpty()
            binding.tvUpcomingPaymentsLabel.visibility = if (visible) View.VISIBLE else View.GONE
            binding.rvUpcomingPayments.visibility = if (visible) View.VISIBLE else View.GONE
        }

        viewModel.topLoyalCustomers.observe(viewLifecycleOwner) { list ->
            topLoyalAdapter.submitList(list)
            val visible = list.isNotEmpty()
            binding.tvTopLoyalLabel.visibility = if (visible) View.VISIBLE else View.GONE
            binding.rvTopLoyalCustomers.visibility = if (visible) View.VISIBLE else View.GONE
        }
    }

    private fun setupFab() {
        binding.fab.setOnClickListener {
            val action = DashboardFragmentDirections.actionDashboardFragmentToAddTransactionFragment()
            findNavController().navigate(action)
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

    private fun setupCustomerFilter() {
        val defaultColor = androidx.core.content.ContextCompat.getColor(requireContext(), com.kanhaiyajewellers.creditmanager.R.color.text_secondary)
        val activeColor = androidx.core.content.ContextCompat.getColor(requireContext(), com.kanhaiyajewellers.creditmanager.R.color.gold_primary)

        fun updateFilterUi() {
            binding.tvFilterAllCustomers.setTextColor(if (selectedFilter == CustomerFilter.ALL) activeColor else defaultColor)
            binding.tvFilterPendingCustomers.setTextColor(if (selectedFilter == CustomerFilter.PENDING) activeColor else defaultColor)
            binding.tvFilterLoyalCustomers.setTextColor(if (selectedFilter == CustomerFilter.LOYAL) activeColor else defaultColor)
        }

        binding.tvFilterAllCustomers.setOnClickListener {
            selectedFilter = CustomerFilter.ALL
            updateFilterUi()
            applyCustomerFilter()
        }
        binding.tvFilterPendingCustomers.setOnClickListener {
            selectedFilter = CustomerFilter.PENDING
            updateFilterUi()
            applyCustomerFilter()
        }
        binding.tvFilterLoyalCustomers.setOnClickListener {
            selectedFilter = CustomerFilter.LOYAL
            updateFilterUi()
            applyCustomerFilter()
        }
        updateFilterUi()
    }

    private fun applyCustomerFilter() {
        val filtered = when (selectedFilter) {
            CustomerFilter.ALL -> allCustomers
            CustomerFilter.PENDING -> allCustomers.filter { it.totalPending > 0.0 }
            CustomerFilter.LOYAL -> allCustomers.filter { it.isLoyalCustomer }
        }
        customerAdapter.submitList(filtered)
        binding.tvEmptyState.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        binding.rvTransactions.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
    }
}
