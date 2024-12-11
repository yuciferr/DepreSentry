package com.example.depresentry.presentation.phq9

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.depresentry.domain.usecase.auth.GetCurrentUserIdUseCase
import com.example.depresentry.domain.usecase.userData.local.UpdatePHQ9UseCase
import com.example.depresentry.presentation.composables.DetailAppBar
import com.example.depresentry.presentation.composables.GradientBackground
import com.example.depresentry.presentation.composables.SurveyButton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PHQ9ViewModel @Inject constructor(
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val updatePHQ9UseCase: UpdatePHQ9UseCase
) : ViewModel() {
    fun savePHQ9Result(score: Int, answers: List<Int>) {
        val userId = getCurrentUserIdUseCase() ?: run {
            Log.e("PHQ9ViewModel", "UserId bulunamadı, PHQ9 sonuçları kaydedilemedi")
            return
        }
        viewModelScope.launch {
            try {
                updatePHQ9UseCase(userId, score, answers)
                Log.d("PHQ9ViewModel", """
                    PHQ9 sonuçları başarıyla kaydedildi:
                    - userId: $userId
                    - Toplam skor: $score
                    - Cevaplar: $answers
                """.trimIndent())
            } catch (e: Exception) {
                Log.e("PHQ9ViewModel", "PHQ9 sonuçları kaydedilirken hata oluştu: ${e.message}", e)
            }
        }
    }
}

@Composable
fun PHQ9Screen(
    navController: NavController,
    viewModel: PHQ9ViewModel = hiltViewModel()
) {
    // List of questions
    val questions = listOf(
        "Little interest or pleasure in doing things",
        "Feeling down, depressed, or hopeless",
        "Trouble falling or staying asleep, or sleeping too much",
        "Feeling tired or having little energy",
        "Poor appetite or overeating",
        "Feeling bad about yourself — or that you are a failure or \n" +
                "have let yourself or your family down",
        "Trouble concentrating on things, such as reading the \n" +
                "newspaper or watching television",
        "Moving or speaking so slowly that other people could have \n" +
                "noticed? Or the opposite — being so fidgety or restless \n" +
                "that you have been moving around a lot more than usual",
        "Thoughts that you would be better off dead or of hurting \n" +
                "yourself in some way"
    )

    // State variables
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var totalScore by remember { mutableIntStateOf(0) }
    val answers = remember { mutableListOf<Int>() }
    val progress = (currentQuestionIndex + 1) / 9f

    // Function to handle answer clicks
    fun handleAnswerClick(score: Int) {
        answers.add(score)
        totalScore += score
        
        Log.d("PHQ9Screen", """
            Soru ${currentQuestionIndex + 1} cevaplandı:
            - Skor: $score
            - Toplam skor: $totalScore
            - Soru: "${questions[currentQuestionIndex]}"
        """.trimIndent())
        
        if (currentQuestionIndex == 8) {
            Log.d("PHQ9Screen", """
                PHQ9 tamamlandı:
                - Toplam skor: $totalScore
                - Tüm cevaplar: ${answers.toList()}
            """.trimIndent())
            viewModel.savePHQ9Result(totalScore, answers.toList())
            navController.popBackStack()
        } else {
            currentQuestionIndex += 1
        }
    }

    GradientBackground()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        DetailAppBar(
            title = "PHQ-9 Depression",
            detail = "${currentQuestionIndex + 1} to 9",
            onBackClick = {
                navController.popBackStack()
            }
        )

        // Progress indicator
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            color = Color(0xFFD6BBFB),
            trackColor = Color(0xFF2E2E3F),
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Question Animation
        AnimatedContent(
            targetState = currentQuestionIndex,
            transitionSpec = {
                // Slide animation
                if (targetState > initialState) {
                    slideInHorizontally(animationSpec = tween(500)) { width -> width } togetherWith
                            slideOutHorizontally(animationSpec = tween(500)) { width -> -width }
                } else {
                    slideInHorizontally(animationSpec = tween(500)) { width -> -width } togetherWith
                            slideOutHorizontally(animationSpec = tween(500)) { width -> width }
                }
            }, label = ""
        ) { targetIndex ->
            Text(
                text = questions[targetIndex],
                color = Color(0xFFE3CCF2),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Survey buttons for answers
        SurveyButton(text = "Not at all", onClick = { handleAnswerClick(0) })
        SurveyButton(text = "Several days", onClick = { handleAnswerClick(1) })
        SurveyButton(text = "More than half the days", onClick = { handleAnswerClick(2) })
        SurveyButton(text = "Nearly everyday", onClick = { handleAnswerClick(3) })
    }

}

@Preview
@Composable
fun PHQ9ScreenPreview() {
    //PHQ9Screen()
}
