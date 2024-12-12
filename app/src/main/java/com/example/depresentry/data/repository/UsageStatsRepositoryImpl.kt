import android.content.Intent
import com.example.depresentry.data.service.UsageStatsService
import com.example.depresentry.domain.repository.UsageStatsRepository
import javax.inject.Inject

class UsageStatsRepositoryImpl @Inject constructor(
    private val usageStatsService: UsageStatsService
) : UsageStatsRepository {
    
    override fun hasUsageStatsPermission(): Boolean {
        return usageStatsService.hasUsageStatsPermission()
    }

    override fun getUsageStatsSettingsIntent(): Intent {
        return usageStatsService.getUsageStatsSettingsIntent()
    }

    override fun getDailyStats(): Map<String, Long> {
        return usageStatsService.getDailyStats()
    }

    override fun getWeeklyStats(): Map<String, Long> {
        return usageStatsService.getWeeklyStats()
    }

    override fun getMonthlyStats(): Map<String, Long> {
        return usageStatsService.getMonthlyStats()
    }

    override fun formatDuration(millis: Long): String {
        return usageStatsService.formatDuration(millis)
    }
} 