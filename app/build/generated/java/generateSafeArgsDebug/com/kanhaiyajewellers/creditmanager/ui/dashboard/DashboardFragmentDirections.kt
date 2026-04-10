package com.kanhaiyajewellers.creditmanager.ui.dashboard

import android.os.Bundle
import androidx.`annotation`.CheckResult
import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.kanhaiyajewellers.creditmanager.R
import kotlin.Float
import kotlin.Int
import kotlin.Long
import kotlin.String

public class DashboardFragmentDirections private constructor() {
  private data class ActionDashboardFragmentToCustomerHistoryFragment(
    public val customerId: Long,
    public val customerName: String,
    public val customerPhone: String,
    public val totalPending: Float,
    public val totalPaid: Float,
  ) : NavDirections {
    public override val actionId: Int = R.id.action_dashboardFragment_to_customerHistoryFragment

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putLong("customerId", this.customerId)
        result.putString("customerName", this.customerName)
        result.putString("customerPhone", this.customerPhone)
        result.putFloat("totalPending", this.totalPending)
        result.putFloat("totalPaid", this.totalPaid)
        return result
      }
  }

  public companion object {
    @CheckResult
    public fun actionDashboardFragmentToAddTransactionFragment(): NavDirections = ActionOnlyNavDirections(R.id.action_dashboardFragment_to_addTransactionFragment)

    @CheckResult
    public fun actionDashboardFragmentToCustomerHistoryFragment(
      customerId: Long,
      customerName: String,
      customerPhone: String,
      totalPending: Float,
      totalPaid: Float,
    ): NavDirections = ActionDashboardFragmentToCustomerHistoryFragment(customerId, customerName, customerPhone, totalPending, totalPaid)
  }
}
