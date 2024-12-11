import com.example.depresentry.domain.repository.UserDataRepository
import javax.inject.Inject

class UpdateScreenTimeUseCase @Inject constructor(
    private val repository: UserDataRepository
) {
    suspend operator fun invoke(
        userId: String,
        total: Double,
        byApp: Map<String, Double>
    ) {
        repository.updateScreenTime(userId, total, byApp)
    }
} 