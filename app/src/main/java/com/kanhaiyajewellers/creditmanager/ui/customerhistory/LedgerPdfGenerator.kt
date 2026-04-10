package com.kanhaiyajewellers.creditmanager.ui.customerhistory

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object LedgerPdfGenerator {

    fun generate(context: Context, data: LedgerExportData): File {
        val pdf = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val margin = 36f
        val inr = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 18f
            isFakeBoldText = true
        }
        val subTitlePaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 14f
            isFakeBoldText = true
        }
        val normalPaint = Paint().apply {
            color = Color.BLACK
            textSize = 11f
        }
        val headerCellPaint = Paint().apply {
            color = Color.parseColor("#EEEEEE")
        }
        val linePaint = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 1f
        }
        val creditPaint = Paint(normalPaint).apply { color = Color.parseColor("#D32F2F") }
        val paymentPaint = Paint(normalPaint).apply { color = Color.parseColor("#2E7D32") }

        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = pdf.startPage(pageInfo)
        var canvas = page.canvas
        var y = margin

        fun drawHeader() {
            canvas.drawText("Kanhaiya Jewellers", margin, y, titlePaint)
            y += 24f
            canvas.drawText("Customer Ledger", margin, y, subTitlePaint)
            y += 20f
            canvas.drawText("Customer Name: ${data.customerName}", margin, y, normalPaint)
            y += 16f
            canvas.drawText("Phone Number: ${data.phone}", margin, y, normalPaint)
            y += 18f
        }

        fun drawTableHeader() {
            val left = margin
            val right = pageWidth - margin
            val rowHeight = 22f
            canvas.drawRect(left, y, right, y + rowHeight, headerCellPaint)
            canvas.drawText("Date", left + 6f, y + 15f, normalPaint)
            canvas.drawText("Type", left + 170f, y + 15f, normalPaint)
            canvas.drawText("Amount", left + 300f, y + 15f, normalPaint)
            y += rowHeight
            canvas.drawLine(left, y, right, y, linePaint)
        }

        drawHeader()
        drawTableHeader()

        if (data.entries.isEmpty()) {
            y += 18f
            canvas.drawText("No transactions found.", margin, y, normalPaint)
            y += 20f
        } else {
            data.entries.forEach { entry ->
                if (y > pageHeight - 120f) {
                    pdf.finishPage(page)
                    pageNumber += 1
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    page = pdf.startPage(pageInfo)
                    canvas = page.canvas
                    y = margin
                    drawHeader()
                    drawTableHeader()
                }
                val left = margin
                val right = pageWidth - margin
                canvas.drawText(dateFormat.format(Date(entry.dateMillis)), left + 6f, y + 15f, normalPaint)
                canvas.drawText(entry.type, left + 170f, y + 15f, normalPaint)
                val amountPaint = if (entry.type == "Credit") creditPaint else paymentPaint
                canvas.drawText(inr.format(entry.amount), left + 300f, y + 15f, amountPaint)
                y += 22f
                canvas.drawLine(left, y, right, y, linePaint)
            }
        }

        y += 24f
        canvas.drawText("Summary", margin, y, subTitlePaint)
        y += 18f
        canvas.drawText("Total Credit: ${inr.format(data.totalCredit)}", margin, y, creditPaint)
        y += 16f
        canvas.drawText("Total Paid: ${inr.format(data.totalPaid)}", margin, y, paymentPaint)
        y += 16f
        canvas.drawText("Remaining Balance: ${inr.format(data.remainingBalance)}", margin, y, normalPaint)

        pdf.finishPage(page)

        val exportDir = File(context.getExternalFilesDir(null), "exports").apply { mkdirs() }
        val safeName = data.customerName.replace("[^a-zA-Z0-9]".toRegex(), "_")
        val output = File(exportDir, "Ledger_${safeName}_${System.currentTimeMillis()}.pdf")
        FileOutputStream(output).use { pdf.writeTo(it) }
        pdf.close()
        return output
    }
}
