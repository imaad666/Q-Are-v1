package com.example.spiderqr.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import java.text.SimpleDateFormat
import java.util.*

object QRCodeGenerator {
    
    fun generateTodaysQRCode(rollNumber: String, studentName: String): String {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return "$rollNumber/$today/$studentName"
    }

    fun generateQRBitmap(
        content: String,
        size: Int = 512,
        foregroundColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE,
        cornerRadius: Float = 0f,
        userInitials: String? = null
    ): Bitmap? {
        return try {
            val writer = QRCodeWriter()
            val hints = mapOf(EncodeHintType.MARGIN to 1)
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints)
            
            val bitmap = createBitmapFromMatrix(bitMatrix, foregroundColor, backgroundColor)
            
            // Apply customizations
            val customBitmap = applyCustomizations(bitmap, cornerRadius, userInitials)
            customBitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

    private fun createBitmapFromMatrix(
        matrix: BitMatrix,
        foregroundColor: Int,
        backgroundColor: Int
    ): Bitmap {
        val width = matrix.width
        val height = matrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (matrix[x, y]) foregroundColor else backgroundColor)
            }
        }
        
        return bitmap
    }

    private fun applyCustomizations(
        originalBitmap: Bitmap,
        cornerRadius: Float,
        userInitials: String?
    ): Bitmap {
        val size = originalBitmap.width
        val customBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(customBitmap)
        
        // Apply corner radius if specified
        if (cornerRadius > 0) {
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            val rect = RectF(0f, 0f, size.toFloat(), size.toFloat())
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
            
            // Use the rounded rectangle as a mask
            paint.xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(originalBitmap, 0f, 0f, paint)
        } else {
            canvas.drawBitmap(originalBitmap, 0f, 0f, null)
        }
        
        // Add initials overlay if provided
        userInitials?.let { initials ->
            if (initials.length <= 2) {
                addInitialsOverlay(canvas, initials, size)
            }
        }
        
        return customBitmap
    }

    private fun addInitialsOverlay(canvas: Canvas, initials: String, size: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = size * 0.08f // 8% of QR size
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
        }
        
        // Create a small background circle for the initials
        val centerX = size * 0.85f
        val centerY = size * 0.15f
        val radius = size * 0.06f
        
        val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(180, 0, 0, 0) // Semi-transparent black
        }
        
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint)
        canvas.drawText(
            initials.uppercase(),
            centerX,
            centerY + paint.textSize / 3,
            paint
        )
    }

    fun isValidLoginQR(qrContent: String): Boolean {
        // Check if QR matches the pattern: rollnumber/YYYY-MM-DD/student
        val pattern = Regex("""\w+/\d{4}-\d{2}-\d{2}/.+""")
        return pattern.matches(qrContent)
    }

    fun parseQRContent(qrContent: String): Triple<String, String, String>? {
        val parts = qrContent.split("/")
        return if (parts.size == 3 && isValidLoginQR(qrContent)) {
            Triple(parts[0], parts[1], parts[2]) // rollNumber, date, studentName
        } else {
            null
        }
    }
}
