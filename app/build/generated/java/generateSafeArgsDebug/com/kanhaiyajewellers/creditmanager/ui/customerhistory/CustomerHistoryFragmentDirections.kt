package com.kanhaiyajewellers.creditmanager.ui.customerhistory

import android.os.Bundle
import androidx.`annotation`.CheckResult
import androidx.navigation.NavDirections
import com.kanhaiyajewellers.creditmanager.R
import kotlin.Int
import kotlin.Long

public class CustomerHistoryFragmentDirections private constructor() {
  private data class ActionCustomerHistoryFragmentToTransactionDetailFragment(
    public val transactionId: Long = -1L,
  ) : NavDirections {
    public override val actionId: Int =
        R.id.action_customerHistoryFragment_to_transactionDetailFragment

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putLong("transactionId", this.transactionId)
        return result
      }
  }

  public companion object {
    @CheckResult
    public fun actionCustomerHistoryFragmentToTransactionDetailFragment(transactionId: Long = -1L): NavDirections = ActionCustomerHistoryFragmentToTransactionDetailFragment(transactionId)
  }
}
