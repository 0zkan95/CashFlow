package com.example.cashbookneo.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.sqrt

data class RingChartData(
    val value: Float,
    val color: Color,
    val tag: Any? = null
)

@Composable
fun RingChart(
    data: List<RingChartData>,
    modifier: Modifier = Modifier,
    thickness: Dp = 20.dp,
    onSliceClick: (RingChartData) -> Unit = {}
) {
    val total = data.sumOf { it.value.toDouble() }.toFloat()
    
    Canvas(
        modifier = modifier
            .size(200.dp)
            .padding(16.dp)
            .pointerInput(data) {
                detectTapGestures { offset ->
                    val canvasSize = size.width
                    val center = Offset(canvasSize / 2f, canvasSize / 2f)
                    val distance = sqrt((offset.x - center.x).let { it * it } + (offset.y - center.y).let { it * it })
                    
                    val outerRadius = canvasSize / 2f
                    val innerRadius = outerRadius - thickness.toPx()
                    
                    if (distance in innerRadius..outerRadius) {
                        var angle = Math.toDegrees(atan2(offset.y - center.y, offset.x - center.x).toDouble()).toFloat()
                        if (angle < 0) angle += 360f
                        
                        // Adjust angle to match -90f startAngle
                        var adjustedAngle = (angle + 90f) % 360f
                        
                        var currentAngle = 0f
                        data.forEach { item ->
                            val sweepAngle = (item.value / total) * 360f
                            if (adjustedAngle >= currentAngle && adjustedAngle <= currentAngle + sweepAngle) {
                                onSliceClick(item)
                                return@detectTapGestures
                            }
                            currentAngle += sweepAngle
                        }
                    }
                }
            }
    ) {
        var startAngle = -90f
        
        data.forEach { item ->
            val sweepAngle = (item.value / total) * 360f
            drawArc(
                color = item.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = thickness.toPx(), cap = StrokeCap.Round)
            )
            startAngle += sweepAngle
        }
    }
}
