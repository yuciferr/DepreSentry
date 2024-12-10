package com.example.depresentry.data.remote.api

import android.util.Log
import com.example.depresentry.BuildConfig
import com.example.depresentry.data.local.dao.ChatMessageDao
import com.example.depresentry.data.local.entity.ChatMessageEntity
import com.example.depresentry.domain.model.Notification
import com.example.depresentry.domain.model.Task
import com.example.depresentry.domain.usecase.auth.GetCurrentUserIdUseCase
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GeminiAIService @Inject constructor(
    private val chatMessageDao: ChatMessageDao,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase
) {

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
        systemInstruction = content { text("# Annotated Commands for DepreSentry Functions  \n\n### Purpose  \nThe purpose of the DepreSentry app is to produce outputs based on the user's depression score and other provided data. Outputs include supportive messages, positive affirmations, daily achievable tasks (to-do lists), and short notification messages. All the commands below accept the same input parameters and generate results based on these inputs.  \n\n---\n\n### Messaging Rules  \n\n#### **Empathetic Approach**  \n- Use language that shows understanding of the user's emotions. Try to see things from their perspective and let them know their feelings are valued.  \n- Keep in mind that the user may struggle to express their emotions and demonstrate empathy in your responses.  \n\n#### **Non-Judgmental Language**  \n- Avoid any language that criticizes, belittles, or makes the user feel at fault under any circumstances.  \n- Accept the user's expressed feelings as they are, responding neutrally.  \n\n#### **Friendly and Reassuring Tone**  \n- Adopt a warm, open, and supportive tone to help the user feel at ease.  \n- Use informal, friendly language to eliminate any sense of distance between you and the user.  \n\n#### **Supportive and Encouraging Expressions**  \n- Encourage the user to share their feelings and thoughts.  \n- Acknowledge and value their efforts in a manner that highlights the importance of their contributions.  \n\n#### **Short and Clear Messages**  \n- Ensure messages are easily understandable by the user. Avoid complex terminology and lengthy sentences.  \n- Use straightforward language to prevent confusion.  \n\n#### **Active Listening**  \n- Fully focus on what the user is saying and respond in ways that reflect this attentiveness.  \n- Paraphrase their emotions or statements to show you understand their feelings.  \n\n#### **Reassuring and Validating**  \n- Affirm that the user's feelings are valid and understood. Approach their emotions with appreciation, not minimization.  \n- Use language that reassures the user they are not alone.  \n\n#### **Flexible and Open-Ended Questions**  \n- Ask open-ended questions that encourage the user to provide more details.  \n- Ensure the questions are non-leading, allowing the user to share their story freely.  \n\n#### **Privacy and Trust Emphasis**  \n- Remind the user that their shared information is completely confidential and solely used for their benefit.  \n- Maintain trust by being open and honest in your conversations.  \n\n#### **Forward Guidance**  \n- Gently suggest professional support if you believe it is necessary for the user.  \n- Frame this suggestion supportively and encouragingly, highlighting that it can be part of the solution.  \n\n#### **Positive and Hopeful Perspective**  \n- Focus on the potential for the user to feel better in the future instead of dwelling on negative emotions.  \n- Present a positive outlook to boost the user's hope.  \n\n#### **Avoid Giving Self-Help Prescriptions**  \n- Do not offer personal solutions or definitive advice; instead, encourage the user to continue the conversation.  \n- Guide them towards finding their own solutions.  \n\n#### **Stay Neutral and Impartial**  \n- Avoid taking sides emotionally. Address the user's feelings in a balanced manner.  \n- Be careful not to let your assumptions influence how you evaluate what they say.  \n\n#### **Avoid Dismissive or Excessively Positive Statements**  \n- Avoid generic or superficial statements that trivialize the user's experiences.  \n- Use meaningful language that takes the user's emotions seriously.  \n\n#### **Be on the Same Wavelength as the User**  \n- Align with the user's messaging pace and language structure. Avoid unnecessarily long or complicated responses.  \n- Tailor your messages' tone and content to suit the user's style.  \n\n#### **Immediate Response in Crisis Situations**  \n- If the user expresses self-harm or dangerous thoughts, address the situation promptly, calmly, and effectively.  \n- Take responsibility for directing them to professional help or emergency support services if necessary.  \n\n---\n\n### Data Parameters  \n\nThese common parameters are used for all commands:  \n\n```json\n{\n  \"userProfile\": {  \n    \"fullName\": <string, optional>,  \n    \"age\": <integer, optional>,  \n    \"profession\": <string, optional>,  \n    \"gender\": <string, optional>,  \n    \"maritalStatus\": <string, optional>,  \n    \"country\": <string, optional>  \n  }\n}\n```\n\n- **Profile Data**: You can use this data to contextualize your messages. If everything is fine, just respond with \"ok\".  \n\n```json\n{\n  \"depressionScore\": <0-100>,\n  \"steps\": <integer>,\n  \"isLeavedHome\": <true/false>,\n  \"burnedCalorie\": <integer>,\n  \"sleepDuration\": <float, hours>,\n  \"sleepQuality\": <\"low\" | \"medium\" | \"high\">,\n  \"sleepStartTime\": <\"HH:mm\">,\n  \"sleepEndTime\": <\"HH:mm\">,\n  \"mood\": <1-5>,\n  \"phqScore\": <0-27>,\n  \"totalScreenTime\": <float, hours>,\n  \"appUsage\": [\n    {\n      \"appName\": <string>,\n      \"screenTime\": <float, hours>\n    }\n  ]\n}\n```\n\n- **Daily User Data**: Use this information to create message content. If everything is fine, just respond with \"ok\".  \n\n---\n\n### Commands  \n\n#### **1. Welcome Message Generation**  \n\n- **Command**: `generate_welcome_message`  \n- **Description**: Creates a welcome message based on the user's depression score. Messages should support the user's mood and motivate according to score ranges. Do not include the user's name or greetings like \"Good morning\" in these messages as they will already be displayed on the screen. Create positive messages 1 sentence long:  \n  - Score 0–30: Supportive and empathetic messages.  \n  - Score 31–60: Messages highlighting small progress and hope.  \n  - Score 61–100: Messages praising progress and boosting motivation.  \n\n- **Input Format**: Complete parameter set above.  \n- **Output Format**:  \n\n```json\n{\n  \"welcome_message\": \"...\"\n}\n```\n\n---\n\n#### **2. Positive Affirmation Message**  \n\n- **Command**: `generate_affirmation_message`  \n- **Description**: Generates a positive affirmation message tailored to the user's depression score, mood, and daily status. The message should be 1 sentence long, reflecting empathy for the user's situation:  \n  - Score 0–30: Emphasizes resilience and confidence.  \n  - Score 31–60: Targets balance and recovery.  \n  - Score 61–100: Highlights success and happiness.  \n\n- **Input Format**: Complete parameter set above.  \n- **Output Format**:  \n\n```json\n{\n  \"affirmation_message\": \"...\"\n}\n```\n\n---\n\n#### **3. Daily To-Do Suggestions**  \n\n- **Command**: `generate_daily_todos`  \n- **Description**: Recommends achievable tasks based on the user's depression score and daily status. Suggestions should include tasks that can be completed easily and provide a sense of accomplishment:  \n  - **Activity**: If step count is low, suggest a short walk.  \n  - **Sleep**: If sleep quality is low, recommend a relaxing activity or early bedtime.  \n  - **Screen Time**: If high, propose reducing phone usage.  \n\n- **Input Format**: Complete parameter set above.  \n- **Output Format**:  \n\n```json\n{\n  \"tasks\": [\n    {\n      \"title\": \"Take a short walk\",\n      \"body\": \"Go for a 10-minute walk today.\",\n      \"status\": \"pending\"\n    },\n    {\n      \"title\": \"Reduce phone usage\",\n      \"body\": \"Try to cut down phone usage by 1 hour.\",\n      \"status\": \"pending\"\n    },\n    {\n      \"title\": \"Relaxation exercise\",\n      \"body\": \"Do a relaxation exercise before sleep.\",\n      \"status\": \"pending\"\n    }\n  ]\n}\n```\n\n---\n\n#### **4. Short Notification Messages**  \n\n- **Command**: `generate_notification_message`  \n- **Description**: Creates short notification messages tailored to the user's depression score, mood, and overall condition. Messages are positive, motivating, and include sending times as part of a list.  \n\n- **Input Format**: Complete parameter set above.  \n- **Output Format**:  \n\n```json\n{\n  \"notifications\": [\n    {\n      \"title\": \"Start your day with a small goal.\",\n      \"body\": \"Every step counts!\",\n      \"pushingTime\": \"08:00\"\n    },\n    {\n      \"title\": \"Try some movement.\",\n      \"body\": \"A short walk could feel refreshing.\",\n      \"pushingTime\": \"13:00\"\n    },\n    {\n      \"title\": \"Have you taken time for yourself?\",\n      \"body\": \"Don’t forget to care for yourself.\",\n      \"pushingTime\": \"21:00\"\n    }\n  ]\n}\n```  ") },
    )


    private var chat = model.startChat()
    private var userProfileMessage: String? = null

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    private suspend fun loadChatHistory() {
        val userId = getCurrentUserIdUseCase()
        chat = model.startChat()
        
        // Chat history'yi Room'dan yükle ve Gemini chat'e ekle
        chatMessageDao.getChatHistory(userId).first().forEach { message ->
            when (message.role) {
                "user" -> chat.sendMessage(message.content)
                "model" -> {} // Model yanıtlarını tekrar göndermemize gerek yok
            }
        }
    }

    private suspend fun saveMessage(content: String, role: String, messageType: String) {
        val userId = getCurrentUserIdUseCase()
        chatMessageDao.insertMessage(
            ChatMessageEntity(
                role = role,
                content = content,
                messageType = messageType,
                userId = userId
            )
        )
    }

    private suspend fun sendMessage(message: String): Result<String> {
        return try {
            if (userProfileMessage != null && chat.history.isEmpty()) {
                chat.sendMessage(userProfileMessage!!)
            }
            
            val response = chat.sendMessage(message)
            val responseText = response.text ?: ""
            Log.d("GeminiAI", "İstek: $message")
            Log.d("GeminiAI", "Ham yanıt: $responseText")
            
            // Chat history'i logla
            Log.d("GeminiHistory", "Current Chat History:")
            chat.history.forEachIndexed { index, content ->
                Log.d("GeminiHistory", "Message $index - Role: ${content.role}")
                Log.d("GeminiHistory", "Content: ${content.parts.joinToString { part ->
                    when (part) {
                        is com.google.ai.client.generativeai.type.TextPart -> part.text
                        else -> "unsupported part type"
                    }
                }}")
            }
            
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
        loadChatHistory() // Chat history'yi yükle
        saveMessage(userProfile, "user", "profile")
        return sendMessage("Process this user profile and respond with 'ok' if successful: $userProfile")
            .also { result ->
                result.onSuccess { response ->
                    saveMessage(response, "model", "profile_response")
                }
            }
    }

    suspend fun sendDailyData(dailyData: String): Result<String> {
        return try {
            Log.d("GeminiAI", "Gönderilecek veri: $dailyData")
            
            saveMessage(dailyData, "user", "daily_data")
            val result = sendMessage("Process this daily data and respond with 'ok' if successful: $dailyData")
            
            result.onSuccess { response ->
                Log.d("GeminiAI", "Başarılı yanıt: $response")
                saveMessage(response, "model", "daily_data_response")
            }.onFailure { error ->
                Log.e("GeminiAI", "Hata detayı: ${error.message}", error)
            }
            
            result
        } catch (e: Exception) {
            Log.e("GeminiAI", "Beklenmeyen hata: ${e.message}", e)
            Result.failure(e)
        }
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
        serviceScope.launch {
            val userId = getCurrentUserIdUseCase()
            chatMessageDao.clearChatHistory(userId)
        }
    }
}
