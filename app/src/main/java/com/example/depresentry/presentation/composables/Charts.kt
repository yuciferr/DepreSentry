package com.example.depresentry.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine

import androidx.compose.runtime.remember
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RichTooltip
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset


@Composable
fun MoodLineChart(
    modifier: Modifier = Modifier,
    points: List<Point> = listOf(
        Point(0f, 0f), // Monday - Terrible
        Point(1f, 3f), // Tuesday - Good
        Point(2f, 1f), // Wednesday - Bad
        Point(3f, 2f), // Thursday - Average
        Point(4f, 4f), // Friday - Excellent
        Point(5f, 2f), // Saturday - Average
        Point(6f, 4f)  // Sunday - Excellent
    ),
    lineColor: Color = Color(0xFFE341D5),
    cardBackgroundColor: Color = Color(0x23E8D1F7),
    axisLineColor: Color = Color(0xFF806691),
    axisLabelColor: Color = Color(0xFFE3CCF2)
) {
    // X Axis data
    val xAxisData = AxisData.Builder()
        .axisStepSize(48.dp)
        .steps(points.size - 1)
        .labelData { value ->
            when (value) {
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
        .labelAndAxisLinePadding(8.dp)
        .axisLineColor(axisLineColor)
        .axisLabelColor(axisLabelColor)
        .shouldDrawAxisLineTillEnd(true)
        .backgroundColor(Color.Transparent)
        .build()

    // Y Axis data
    val yAxisData = AxisData.Builder()
        .steps(4)
        .labelAndAxisLinePadding(8.dp)
        .labelData { value ->
            when (value) {
                0 -> "😢"  // Terrible
                1 -> "😔"  // Bad
                2 -> "😐"  // Average
                3 -> "😊"  // Good
                4 -> "😂"  // Excellent
                else -> ""
            }
        }
        .axisLineColor(axisLineColor)
        .axisLabelColor(axisLabelColor)
        .shouldDrawAxisLineTillEnd(true)
        .backgroundColor(Color.Transparent)
        .build()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(352.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor
        ),
        border = BorderStroke(1.dp, axisLineColor),
        shape = RoundedCornerShape(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
        ) {
            LineChart(
                modifier = Modifier.fillMaxWidth(),
                lineChartData = LineChartData(
                    linePlotData = LinePlotData(
                        lines = listOf(
                            Line(
                                dataPoints = points,
                                lineStyle = LineStyle(
                                    color = lineColor,
                                    width = 3f
                                ),
                                shadowUnderLine = ShadowUnderLine(
                                    alpha = 0.5f,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            lineColor.copy(alpha = 0.5f),
                                            lineColor.copy(alpha = 0.1f)
                                        )
                                    )
                                ),
                                selectionHighlightPoint = SelectionHighlightPoint(
                                    color = lineColor
                                ),
                                selectionHighlightPopUp = SelectionHighlightPopUp()
                            )
                        )
                    ),
                    xAxisData = xAxisData,
                    yAxisData = yAxisData,
                    gridLines = GridLines(
                        color = axisLineColor.copy(alpha = 0.2f)
                    ),
                    backgroundColor = Color.Transparent
                ),
            )
        }
    }
}

@Preview
@Composable
fun MoodChartPreview() {

    val customMoodPoints = listOf(
        Point(0f, 2f), // Monday - Average
        Point(1f, 4f), // Tuesday - Excellent
        Point(2f, 1f), // Wednesday - Bad
        Point(3f, 3f), // Thursday - Good
        Point(4f, 0f), // Friday - Terrible
        Point(5f, 2f), // Saturday - Average
        Point(6f, 3f)  // Sunday - Good
    )

    MoodLineChart(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        points = customMoodPoints
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodLineChartWithInteractivity(
    modifier: Modifier = Modifier,
    points: List<Point> = listOf(
        Point(0f, 0f), // Monday - Terrible
        Point(1f, 3f), // Tuesday - Good
        Point(2f, 1f), // Wednesday - Bad
        Point(3f, 2f), // Thursday - Average
        Point(4f, 4f), // Friday - Excellent
        Point(5f, 2f), // Saturday - Average
        Point(6f, 4f)  // Sunday - Excellent
    ),
    lineColor: Color = Color(0xFFE341D5),
    axisLineColor: Color = Color(0xFF806691),
    axisLabelColor: Color = Color(0xFFE3CCF2),
) {
    // Tooltip state
    val tooltipState = remember { mutableStateOf("") }

    // Zoom and Pan state
    val zoomState = remember { mutableStateOf(1f) }
    val offsetState = remember { mutableStateOf(Offset(0f, 0f)) }

    // X Axis data
    val xAxisData = AxisData.Builder()
        .axisStepSize(48.dp)
        .steps(points.size - 1)
        .labelData { value ->
            when (value) {
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
        .axisLineColor(axisLineColor)
        .axisLabelColor(axisLabelColor)
        .shouldDrawAxisLineTillEnd(true)
        .backgroundColor(Color.Transparent)
        .build()

    // Y Axis data
    val yAxisData = AxisData.Builder()
        .steps(4)
        .labelData { value ->
            when (value) {
                0 -> "😢"  // Terrible
                1 -> "😔"  // Bad
                2 -> "😐"  // Average
                3 -> "😊"  // Good
                4 -> "😂"  // Excellent
                else -> ""
            }
        }
        .axisLineColor(axisLineColor)
        .axisLabelColor(axisLabelColor)
        .shouldDrawAxisLineTillEnd(true)
        .backgroundColor(Color.Transparent)
        .build()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(352.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x23E8D1F7)),
        border = BorderStroke(1.dp, axisLineColor),
        shape = RoundedCornerShape(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        // Update pan and zoom state based on gestures
                        zoomState.value *= zoom
                        offsetState.value = offsetState.value.copy(
                            x = offsetState.value.x + pan.x,
                            y = offsetState.value.y + pan.y
                        )
                    }
                }
        ) {
            LineChart(
                modifier = Modifier.fillMaxWidth(),
                lineChartData = LineChartData(
                    linePlotData = LinePlotData(
                        lines = listOf(
                            Line(
                                dataPoints = points,
                                lineStyle = LineStyle(
                                    color = lineColor,
                                    width = 3f
                                ),
                                shadowUnderLine = ShadowUnderLine(
                                    alpha = 0.5f,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            lineColor.copy(alpha = 0.5f),
                                            lineColor.copy(alpha = 0.1f)
                                        )
                                    )
                                ),
                                selectionHighlightPoint = SelectionHighlightPoint(
                                    color = lineColor
                                )
                            )
                        )
                    ),
                    xAxisData = xAxisData,
                    yAxisData = yAxisData,
                    backgroundColor = Color.Transparent
                ),
            )

            // Tooltip for point details
            if (tooltipState.value.isNotEmpty()) {
                RichTooltip {
                    tooltipState.value
                }
            }
        }
    }
}

@Preview
@Composable
fun MoodChartWithInteractivityPreview() {
    val points = listOf(
        Point(0f, 2f), // Monday - Average
        Point(1f, 4f), // Tuesday - Excellent
        Point(2f, 1f), // Wednesday - Bad
        Point(3f, 3f), // Thursday - Good
        Point(4f, 0f), // Friday - Terrible
        Point(5f, 2f), // Saturday - Average
        Point(6f, 3f)  // Sunday - Good
    )

    MoodLineChartWithInteractivity(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        points = points
    )
}
