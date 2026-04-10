package com.kanhaiyajewellers.creditmanager.ui.common

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object WhatsAppHelper {

    fun openWhatsAppMessage(context: Context, phone: String, message: String) {
        if (phone.isBlank()) {
            Toast.makeText(context, "Phone number missing", Toast.LENGTH_SHORT).show()
            return
        }
        val encoded = URLEncoder.encode(message, StandardCharsets.UTF_8.toString())
        val url = "https://wa.me/91$phone?text=$encoded"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
        }
    }
}
