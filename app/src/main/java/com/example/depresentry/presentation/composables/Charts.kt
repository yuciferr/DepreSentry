import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.*

@Composable
fun MoodLineChart(
    modifier: Modifier = Modifier
) {
    // Sample data points
    val points = listOf(
        Point(0f, 0f), // Monday - Terrible
        Point(1f, 3f), // Tuesday - Good
        Point(2f, 1f), // Wednesday - Bad
        Point(3f, 2f), // Thursday - Average
        Point(4f, 4f), // Friday - Excellent
        Point(5f, 2f), // Saturday - Average
        Point(6f, 4f)  // Sunday - Excellent
    )

    // X Axis data
    val xAxisData = AxisData.Builder()
        .axisStepSize(35.dp) // Reduced axisStepSize for better fit
        .steps(points.size - 1)
        .labelData { value ->
            when(value.toInt()) {
                0 -> "M"
                1 -> "T"
                2 -> "W"
                3 -> "T"
                4 -> "F"
                5 -> "S"
                6 -> "S"
                else -> ""
            }
        }
        .labelAndAxisLinePadding(8.dp) // Reduced padding
        .axisLineColor(Color(0xFF806691))
        .axisLabelColor(Color(0xFFE3CCF2))
        .shouldDrawAxisLineTillEnd(true)
        .build()

    // Y Axis data
    val yAxisData = AxisData.Builder()
        .steps(4)
        .labelAndAxisLinePadding(8.dp) // Reduced padding
        .labelData { value ->
            when(value.toInt()) {
                0 -> "😢"  // Terrible
                1 -> "😔"  // Bad
                2 -> "😐"  // Average
                3 -> "😊"  // Good
                4 -> "😂"  // Excellent
                else -> ""
            }
        }
        .axisLineColor(Color(0xFF806691))
        .axisLabelColor(Color(0xFFE3CCF2))
        .shouldDrawAxisLineTillEnd(true)
        .build()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp) // Adjusted height
            .padding(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x23E8D1F7)
        ),
        border = BorderStroke(1.dp, Color(0xFF806691)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().clipToBounds()) { // Clip to bounds to avoid overflow
            LineChart(
                modifier = Modifier.fillMaxWidth(),
                lineChartData = LineChartData(
                    linePlotData = LinePlotData(
                        lines = listOf(
                            Line(
                                dataPoints = points,
                                lineStyle = LineStyle(
                                    color = Color(0xFFE341D5),
                                    width = 3f
                                ),
                                shadowUnderLine = ShadowUnderLine(
                                    alpha = 0.5f,
                                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFFE341D5).copy(alpha = 0.5f),
                                            Color(0xFFE341D5).copy(alpha = 0.1f)
                                        )
                                    )
                                ),
                                selectionHighlightPoint = SelectionHighlightPoint(
                                    color = Color(0xFFE341D5)
                                ),
                                selectionHighlightPopUp = SelectionHighlightPopUp()
                            )
                        )
                    ),
                    xAxisData = xAxisData,
                    yAxisData = yAxisData,
                    gridLines = GridLines(
                        color = Color(0xFF806691).copy(alpha = 0.2f)
                    ),
                    backgroundColor = Color(0x23E8D1F7)
                ),
            )
        }
    }
}

@Composable
@Preview
fun MoodLineChartPreview() {
    MoodLineChart()
}
