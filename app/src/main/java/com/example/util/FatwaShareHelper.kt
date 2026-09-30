package com.example.util

import android.content.Context
import android.content.Intent
import com.example.model.Fatwa

object FatwaShareHelper {

    /**
     * Builds a beautifully formatted textual representation of the Fatwa for sharing across
     * social media (WhatsApp, Telegram, Facebook, X, etc.) and messaging apps.
     */
    fun createShareText(fatwa: Fatwa): String {
        return buildString {
            appendLine("📜 فتوى سماحة آية الله العظمى السيد علي السيستاني (دام ظله)")
            appendLine("━━━━━━━━━━━━━━━━━━━")
            appendLine("📌 ${fatwa.title}")
            appendLine("🏷️ الباب: ${fatwa.category.titleArabic} • ${fatwa.subCategory}")
            appendLine("⚖️ الحكم: ${fatwa.rulingType.labelArabic}")
            appendLine()
            appendLine("❓ السؤال:")
            appendLine(fatwa.question)
            appendLine()
            appendLine("💡 الجواب:")
            appendLine(fatwa.answer)
            appendLine()
            appendLine("📚 المصدر المعتمد:")
            appendLine(fatwa.sourceBook)
            if (fatwa.tags.isNotEmpty()) {
                appendLine()
                appendLine(fatwa.tags.joinToString(" ") { "#$it" } + " #فتاوى_السيستاني")
            }
        }
    }

    /**
     * Dispatches an Android ACTION_SEND intent with a chooser dialog to allow the user
     * to share the fatwa ruling via any installed app (messaging, social media, email).
     */
    fun shareFatwa(context: Context, fatwa: Fatwa) {
        val shareText = createShareText(fatwa)
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "فتوى السيد السيستاني: ${fatwa.title}")
            putExtra(Intent.EXTRA_TEXT, shareText)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooserIntent = Intent.createChooser(sendIntent, "مشاركة الفتوى عبر...").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooserIntent)
    }
}
