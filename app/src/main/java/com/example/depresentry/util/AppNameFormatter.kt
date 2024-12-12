package com.example.depresentry.util

import androidx.compose.ui.graphics.Color

object AppNameFormatter {
    fun formatAppName(packageName: String): String {
        return try {
            // Son noktadan sonraki kısmı al
            val simpleName = packageName.substringAfterLast('.')
            // İlk harfi büyük yap
            simpleName.replaceFirstChar { it.uppercase() }
        } catch (e: Exception) {
            packageName
        }
    }

    sealed class AppCategory(
        val categoryName: String,
        val color: Color
    ) {
        object Social : AppCategory("Social", Color(0xFF4CAF50))  // Yeşil
        object Entertainment : AppCategory("Entertainment", Color(0xFFFFA726)) // Turuncu
        object Productivity : AppCategory("Productivity", Color(0xFF2196F3)) // Mavi
        object Communication : AppCategory("Communication", Color(0xFF9C27B0)) // Mor
        object Gaming : AppCategory("Gaming", Color(0xFFE53935)) // Kırmızı
        object Health : AppCategory("Health & Fitness", Color(0xFF66BB6A)) // Açık Yeşil
        object Education : AppCategory("Education", Color(0xFF7E57C2)) // Mor-Mavi
        object Other : AppCategory("Other", Color(0xFF78909C)) // Gri
    }

    fun getCategoryForApp(appName: String): AppCategory {
        val lowerAppName = appName.lowercase()
        return when {
            // Sosyal Medya Uygulamaları
            lowerAppName.contains("instagram") || 
            lowerAppName.contains("facebook") || 
            lowerAppName.contains("twitter") ||
            lowerAppName.contains("tiktok") ||
            lowerAppName.contains("snapchat") ||
            lowerAppName.contains("linkedin") ||
            lowerAppName.contains("reddit") -> AppCategory.Social

            // Eğlence Uygulamaları
            lowerAppName.contains("youtube") ||
            lowerAppName.contains("netflix") ||
            lowerAppName.contains("spotify") ||
            lowerAppName.contains("prime") ||
            lowerAppName.contains("disney") ||
            lowerAppName.contains("music") ||
            lowerAppName.contains("video") -> AppCategory.Entertainment

            // Üretkenlik Uygulamaları
            lowerAppName.contains("docs") ||
            lowerAppName.contains("sheet") ||
            lowerAppName.contains("office") ||
            lowerAppName.contains("drive") ||
            lowerAppName.contains("calendar") ||
            lowerAppName.contains("note") ||
                    lowerAppName.contains("crypto") ||
            lowerAppName.contains("keep") -> AppCategory.Productivity

            // İletişim Uygulamaları
            lowerAppName.contains("whatsapp") ||
            lowerAppName.contains("telegram") ||
            lowerAppName.contains("messenger") ||
            lowerAppName.contains("signal") ||
            lowerAppName.contains("discord") ||
            lowerAppName.contains("slack") ||
            lowerAppName.contains("teams") -> AppCategory.Communication

            // Oyun Uygulamaları
            lowerAppName.contains("game") ||
            lowerAppName.contains("play") ||
            lowerAppName.contains("pokemon") ||
            lowerAppName.contains("candy") ||
            lowerAppName.contains("clash") -> AppCategory.Gaming

            // Sağlık Uygulamaları
            lowerAppName.contains("health") ||
            lowerAppName.contains("fit") ||
            lowerAppName.contains("workout") ||
            lowerAppName.contains("exercise") ||
                    lowerAppName.contains("depresentry") ||
            lowerAppName.contains("sleep") -> AppCategory.Health

            // Eğitim Uygulamaları
            lowerAppName.contains("learn") ||
            lowerAppName.contains("edu") ||
            lowerAppName.contains("course") ||
            lowerAppName.contains("study") ||
            lowerAppName.contains("duolingo") -> AppCategory.Education

            // Diğer
            else -> AppCategory.Other
        }
    }
} 