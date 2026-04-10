package com.kanhaiyajewellers.creditmanager.ui.customerhistory

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import java.lang.IllegalArgumentException
import kotlin.Float
import kotlin.Long
import kotlin.String
import kotlin.jvm.JvmStatic

public data class CustomerHistoryFragmentArgs(
  public val customerId: Long,
  public val customerName: String,
  public val customerPhone: String,
  public val totalPending: Float,
  public val totalPaid: Float,
) : NavArgs {
  public fun toBundle(): Bundle {
    val result = Bundle()
    result.putLong("customerId", this.customerId)
    result.putString("customerName", this.customerName)
    result.putString("customerPhone", this.customerPhone)
    result.putFloat("totalPending", this.totalPending)
    result.putFloat("totalPaid", this.totalPaid)
    return result
  }

  public fun toSavedStateHandle(): SavedStateHandle {
    val result = SavedStateHandle()
    result.set("customerId", this.customerId)
    result.set("customerName", this.customerName)
    result.set("customerPhone", this.customerPhone)
    result.set("totalPending", this.totalPending)
    result.set("totalPaid", this.totalPaid)
    return result
  }

  public companion object {
    @JvmStatic
    public fun fromBundle(bundle: Bundle): CustomerHistoryFragmentArgs {
      bundle.setClassLoader(CustomerHistoryFragmentArgs::class.java.classLoader)
      val __customerId : Long
      if (bundle.containsKey("customerId")) {
        __customerId = bundle.getLong("customerId")
      } else {
        throw IllegalArgumentException("Required argument \"customerId\" is missing and does not have an android:defaultValue")
      }
      val __customerName : String?
      if (bundle.containsKey("customerName")) {
        __customerName = bundle.getString("customerName")
        if (__customerName == null) {
          throw IllegalArgumentException("Argument \"customerName\" is marked as non-null but was passed a null value.")
        }
      } else {
        throw IllegalArgumentException("Required argument \"customerName\" is missing and does not have an android:defaultValue")
      }
      val __customerPhone : String?
      if (bundle.containsKey("customerPhone")) {
        __customerPhone = bundle.getString("customerPhone")
        if (__customerPhone == null) {
          throw IllegalArgumentException("Argument \"customerPhone\" is marked as non-null but was passed a null value.")
        }
      } else {
        throw IllegalArgumentException("Required argument \"customerPhone\" is missing and does not have an android:defaultValue")
      }
      val __totalPending : Float
      if (bundle.containsKey("totalPending")) {
        __totalPending = bundle.getFloat("totalPending")
      } else {
        throw IllegalArgumentException("Required argument \"totalPending\" is missing and does not have an android:defaultValue")
      }
      val __totalPaid : Float
      if (bundle.containsKey("totalPaid")) {
        __totalPaid = bundle.getFloat("totalPaid")
      } else {
        throw IllegalArgumentException("Required argument \"totalPaid\" is missing and does not have an android:defaultValue")
      }
      return CustomerHistoryFragmentArgs(__customerId, __customerName, __customerPhone, __totalPending, __totalPaid)
    }

    @JvmStatic
    public fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): CustomerHistoryFragmentArgs {
      val __customerId : Long?
      if (savedStateHandle.contains("customerId")) {
        __customerId = savedStateHandle["customerId"]
        if (__customerId == null) {
          throw IllegalArgumentException("Argument \"customerId\" of type long does not support null values")
        }
      } else {
        throw IllegalArgumentException("Required argument \"customerId\" is missing and does not have an android:defaultValue")
      }
      val __customerName : String?
      if (savedStateHandle.contains("customerName")) {
        __customerName = savedStateHandle["customerName"]
        if (__customerName == null) {
          throw IllegalArgumentException("Argument \"customerName\" is marked as non-null but was passed a null value")
        }
      } else {
        throw IllegalArgumentException("Required argument \"customerName\" is missing and does not have an android:defaultValue")
      }
      val __customerPhone : String?
      if (savedStateHandle.contains("customerPhone")) {
        __customerPhone = savedStateHandle["customerPhone"]
        if (__customerPhone == null) {
          throw IllegalArgumentException("Argument \"customerPhone\" is marked as non-null but was passed a null value")
        }
      } else {
        throw IllegalArgumentException("Required argument \"customerPhone\" is missing and does not have an android:defaultValue")
      }
      val __totalPending : Float?
      if (savedStateHandle.contains("totalPending")) {
        __totalPending = savedStateHandle["totalPending"]
        if (__totalPending == null) {
          throw IllegalArgumentException("Argument \"totalPending\" of type float does not support null values")
        }
      } else {
        throw IllegalArgumentException("Required argument \"totalPending\" is missing and does not have an android:defaultValue")
      }
      val __totalPaid : Float?
      if (savedStateHandle.contains("totalPaid")) {
        __totalPaid = savedStateHandle["totalPaid"]
        if (__totalPaid == null) {
          throw IllegalArgumentException("Argument \"totalPaid\" of type float does not support null values")
        }
      } else {
        throw IllegalArgumentException("Required argument \"totalPaid\" is missing and does not have an android:defaultValue")
      }
      return CustomerHistoryFragmentArgs(__customerId, __customerName, __customerPhone, __totalPending, __totalPaid)
    }
  }
}
