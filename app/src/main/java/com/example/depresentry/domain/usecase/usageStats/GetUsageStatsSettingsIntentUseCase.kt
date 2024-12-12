import android.content.Intent
import com.example.depresentry.domain.repository.UsageStatsRepository
import javax.inject.Inject

class GetUsageStatsSettingsIntentUseCase @Inject constructor(
    private val repository: UsageStatsRepository
) {
    operator fun invoke(): Intent {
        return repository.getUsageStatsSettingsIntent()
    }
} 