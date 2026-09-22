package com.example.cashbookneo.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cashbookneo.model.CategorySpendSlice
import com.example.cashbookneo.model.WeeklySpendBucket
import com.example.cashbookneo.ui.theme.Typography

@Composable
fun DonutChart(
    slices: List<CategorySpendSlice>,
    totalLabel: String,
    modifier: Modifier = Modifier.size(170.dp)
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            val strokeWidth = 24.dp.toPx()
            var startAngle = -90f
            if (slices.isEmpty()) {
                drawArc(
                    color = Color(0xFF1E2638),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth)
                )
            } else {
                slices.forEach { slice ->
                    val sweepAngle = slice.percentage * 360f
                    drawArc(
                        color = slice.color,
                        startAngle = startAngle + 2f,
                        sweepAngle = (sweepAngle - 4f).coerceAtLeast(1f),
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    startAngle += sweepAngle
                }
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Total", color = Color(0xFF94A3B8), style = Typography.labelSmall)
            Text(totalLabel, color = Color.White, style = Typography.headlineSmall)
        }
    }
}

@Composable
fun WeeklyVelocityBarChart(
    buckets: List<WeeklySpendBucket>,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    val maxAmount = buckets.maxOfOrNull { it.amount }?.coerceAtLeast(1.0) ?: 1.0
    val peakColor = Color(0xFF818CF8)
    val regularColor = Color(0xFF434E78)
    
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Weekly Outflow Velocity", color = Color.White, style = Typography.labelLarge)
            val peakWeek = buckets.find { it.isPeakWeek }
            if (peakWeek != null) {
                Text("Peak ${peakWeek.weekLabel}", color = Color(0xFF94A3B8), style = Typography.labelSmall)
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            buckets.forEach { bucket ->
                val fraction = (bucket.amount / maxAmount).toFloat().coerceIn(0.15f, 1.0f)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(fraction)
                        .background(
                            if (bucket.isPeakWeek) peakColor else regularColor,
                            shape = RoundedCornerShape(6.dp)
                        )
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            buckets.forEach { bucket ->
                Text(
                    text = bucket.weekLabel,
                    modifier = Modifier.weight(1f),
                    color = if (bucket.isPeakWeek) peakColor else Color(0xFF64748B),
                    style = Typography.labelSmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
