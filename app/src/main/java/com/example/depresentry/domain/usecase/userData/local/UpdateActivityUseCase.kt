import com.example.depresentry.domain.repository.UserDataRepository
import javax.inject.Inject

class UpdateActivityUseCase @Inject constructor(
    private val repository: UserDataRepository
) {
    suspend operator fun invoke(
        userId: String,
        steps: Int,
        isLeavedHome: Boolean,
        burnedCalorie: Int
    ) {
        repository.updateActivity(userId, steps, isLeavedHome, burnedCalorie)
    }
} 