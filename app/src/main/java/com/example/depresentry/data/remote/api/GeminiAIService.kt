package com.example.depresentry.data.remote.api

import com.example.depresentry.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.gson.Gson
import javax.inject.Inject
import android.util.Log
import com.example.depresentry.domain.model.Notification
import com.example.depresentry.domain.model.Task
import kotlinx.coroutines.launch

class GeminiAIService @Inject constructor() {

    private data class WelcomeResponse(val welcome_message: String)
    private data class AffirmationResponse(val affirmation_message: String)
    private data class TodosResponse(
        val tasks: List<Task>
    )
    private data class NotificationsResponse(
        val notifications: List<Notification>
    )


    private val gson = Gson()

    private val model = GenerativeModel(
        "gemini-1.5-pro",
        BuildConfig.GEMINI_API_KEY,
        generationConfig = generationConfig {
            temperature = 1f
            topK = 40
            topP = 0.95f
            maxOutputTokens = 8192
            responseMimeType = "text/plain"
        },
        systemInstruction = content { text("DepreSentry İşlevleri için Açıklamalı Komutlar\n\nAmaç\nDepreSentry uygulamasında, kullanıcının depresyon skoruna ve diğer verilere dayalı çıktılar üretmek. Çıktılar; kullanıcıyı destekleyen mesajlar, pozitif olumlama, günlük yapılabilir görevler (to-do), ve kısa bildirim mesajlarıdır. Aşağıdaki tüm komutlar aynı parametreleri girdi olarak alır ve bu verilere dayalı sonuçlar üretir.\n\nVeri Parametreleri\n\nTüm komutlar için ortak parametre seti:\n\n{\n  \"depressionScore\": <0-100>,\n  \"steps\": <integer>,\n  \"isLeavedHome\": <true/false>,\n  \"burnedCalorie\": <integer>,\n  \"sleepDuration\": <float, hours>,\n  \"sleepQuality\": <\"low\" | \"medium\" | \"high\">,\n  \"sleepStartTime\": <\"HH:mm\">,\n  \"sleepEndTime\": <\"HH:mm\">,\n  \"mood\": <1-5>,\n  \"phqScore\": <0-27>,\n  \"totalScreenTime\": <float, hours>,\n  \"appUsage\": [\n    {\n      \"appName\": <string>,\n      \"screenTime\": <float, hours>\n    }\n  ]\n}\n\n1. Karşılaşma Mesajı Oluşturma\n\nKomut: generate_welcome_message\nAçıklama: Kullanıcının depresyon skoruna göre bir karşılama mesajı oluşturur. Mesajlar, kullanıcının ruh halini destekler ve skor aralıklarına göre motive edici olur:\n\t•\tSkor 0-30: Destekleyici ve empatik mesajlar.\n\t•\tSkor 31-60: Umut veren, küçük ilerlemeleri vurgulayan mesajlar.\n\t•\tSkor 61-100: Gelişimi öven ve motivasyonu artıran mesajlar.\n\nGirdi Formatı: Yukarıdaki parametre setinin tamamı.\n\nÇıktı Formatı:\n\n{\n  \"welcome_message\": \"...\"\n}\n\n2. Pozitif Olumlama Mesajı\n\nKomut: generate_affirmation_message\nAçıklama: Kullanıcının depresyon skoru, ruh hali ve günlük durumuna uygun pozitif bir olumlama mesajı oluşturur:\n\t•\tSkor 0-30: Güven ve dayanıklılığı vurgular.\n\t•\tSkor 31-60: Denge ve iyileşmeyi hedefler.\n\t•\tSkor 61-100: Başarı ve mutluluğu öne çıkarır.\n\nGirdi Formatı: Yukarıdaki parametre setinin tamamı.\n\nÇıktı Formatı:\n\n{\n  \"affirmation_message\": \"...\"\n}\n\n3. Günlük Yapılabilir Görevler (To-Do)\n\nKomut: generate_daily_todos\nAçıklama: Kullanıcının depresyon skoru ve günlük durumuna göre yapılabilir görevler önerir. Öneriler, kullanıcının zorluk çekmeden tamamlayabileceği ve başarı hissi yaratacak görevlerden oluşur.\n\t•\tHareket: Adım sayısı düşükse, kısa bir yürüyüş önerilir.\n\t•\tUyku: Uyku kalitesi düşükse, rahatlatıcı bir aktivite veya erken uyuma önerilir.\n\t•\tEkran Süresi: Yüksekse, telefon kullanımını azaltacak bir görev önerilir.\n\nGirdi Formatı: Yukarıdaki parametre setinin tamamı.\n\nÇıktı Formatı:\n\n{\n   \"tasks\": [\n    {\n      \"title\": \"Kısa bir yürüyüş\",\n      \"body\": \"Bugün 10 dakikalık bir yürüyüş yap.\",\n      \"status\": \"pending\"\n    },\n    {\n      \"title\": \"Telefon kullanımını azalt\",\n      \"body\": \"Telefon kullanımını 1 saat azaltmayı dene.\",\n      \"status\": \"pending\"\n    },\n    {\n      \"title\": \"Rahatlama egzersizi\",\n      \"body\": \"Uyku öncesi bir rahatlama egzersizi yap.\",\n      \"status\": \"pending\"\n    }\n  ]}\n\n4. Kısa Bildirim Mesajları\n\nKomut: generate_notification_message\nAçıklama: Kullanıcının depresyon skoru, ruh hali ve genel durumuna uygun kısa bildirim mesajları oluşturur. Mesajlar, pozitif, motive edici ve gönderim zamanlarını içeren bir liste halinde döner.\n\nGirdi Formatı: Yukarıdaki parametre setinin tamamı.\n\nÇıktı Formatı:\n\n{\n  \"notifications\": [\n    {\n      \"title\": \"Güne küçük bir hedef belirleyerek başla.\",\n      \"body\": \"Her adım önemlidir!\",\n      \"pushingTime\": \"08:00\"\n    },\n    {\n      \"title\": \"Biraz hareket etmeyi deneyin.\",\n      \"body\": \"Kısa bir yürüyüş iyi gelebilir.\",\n      \"pushingTime\": \"13:00\"\n    },\n    {\n      \"title\": \"Kendine zaman ayırdın mı?\",\n      \"body\": \"Kendine iyi bakmayı unutma.\",\n      \"pushingTime\": \"21:00\"\n    }\n  ]\n}") },
    )


    private var chat = model.startChat()
    private var userProfileMessage: String? = null

    private suspend fun sendMessage(message: String): Result<String> {
        return try {
            if (userProfileMessage != null && chat.history.isEmpty()) {
                chat.sendMessage(userProfileMessage!!)
            }
            
            val response = chat.sendMessage(message)
            val responseText = response.text ?: ""
            Log.d("GeminiAI", "İstek: $message")
            Log.d("GeminiAI", "Ham yanıt: $responseText")
            
            if (responseText.trim() == "ok") {
                return Result.success("ok")
            }
            
            val cleanJson = if (responseText.contains("\"tasks\"") || responseText.contains("\"notifications\"")) {
                responseText.substringAfter("```json").substringBefore("```").trim()
            } else {
                val jsonMatch = Regex("\\{[^}]*\\}").find(responseText)
                jsonMatch?.value ?: throw IllegalStateException("JSON bulunamadı: $responseText")
            }
            
            Log.d("GeminiAI", "Temizlenmiş JSON: $cleanJson")
            Result.success(cleanJson)
        } catch (e: Exception) {
            Log.e("GeminiAI", "Hata: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun sendUserProfile(userProfile: String): Result<String> {
        return sendMessage("Process this user profile and respond with 'ok' if successful: $userProfile")
    }

    suspend fun sendDailyData(dailyData: String): Result<String> {
        return sendMessage("Process this daily data and respond with 'ok' if successful: $dailyData")
    }

    suspend fun generateWelcomeMessage(): Result<String> {
        return try {
            sendMessage("generate_welcome_message").map { jsonStr ->
                try {
                    gson.fromJson(jsonStr, WelcomeResponse::class.java).welcome_message
                } catch (e: Exception) {
                    throw IllegalStateException("JSON parse hatası: $jsonStr", e)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateAffirmationMessage(): Result<String> {
        return try {
            sendMessage("generate_affirmation_message").map { jsonStr ->
                try {
                    gson.fromJson(jsonStr, AffirmationResponse::class.java).affirmation_message
                } catch (e: Exception) {
                    throw IllegalStateException("JSON parse hatası: $jsonStr", e)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateDailyTodos(): Result<List<Task>> {
        return try {
            sendMessage("generate_daily_todos").map { jsonStr ->
                try {
                    gson.fromJson(jsonStr, TodosResponse::class.java).tasks.map { taskItem ->
                        Task(
                            title = taskItem.title,
                            body = taskItem.body,
                            status = taskItem.status
                        )
                    }
                } catch (e: Exception) {
                    throw IllegalStateException("JSON parse hatası: $jsonStr", e)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateNotificationMessages(): Result<List<Notification>> {
        return try {
            sendMessage("generate_notification_message").map { jsonStr ->
                try {
                    gson.fromJson(jsonStr, NotificationsResponse::class.java).notifications.map { item ->
                        Notification(
                            title = item.title,
                            body = item.body,
                            pushingTime = item.pushingTime
                        )
                    }
                } catch (e: Exception) {
                    throw IllegalStateException("JSON parse hatası: $jsonStr", e)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun resetChat() {
        chat = model.startChat()
    }
}
