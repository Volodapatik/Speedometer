package com.volodapatik.speedometer.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.volodapatik.speedometer.SpeedState
import kotlin.math.min

private val BackgroundColor = Color(0xFF0D0D0D)
private val CircleStrokeColor = Color(0xFF2A2A2A)
private val ArcTrackColor = Color(0xFF1E1E1E)
private val TextPrimary = Color.White
private val TextSecondary = Color(0xFFB0B0B0)

@Composable
fun SpeedometerScreen(speedState: SpeedState) {
    val displaySpeed = when {
        speedState.speedKmh == null -> "—"
        else -> speedState.speedKmh.toString()
    }

    val speedValue = speedState.speedKmh ?: 0
    val maxSpeedForArc = 160f // visual scale

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.widthIn(max = 400.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(280.dp)
            ) {
                // Decorative circular speedometer
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 12.dp.toPx()
                    val radius = (min(size.width, size.height) - strokeWidth) / 2f
                    val center = Offset(size.width / 2f, size.height / 2f)

                    // Outer decorative ring
                    drawCircle(
                        color = CircleStrokeColor,
                        radius = radius + 8.dp.toPx(),
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Background arc track (almost full circle)
                    val startAngle = 135f
                    val sweepAngle = 270f

                    drawArc(
                        color = ArcTrackColor,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Progress arc based on speed
                    val progress = (speedValue / maxSpeedForArc).coerceIn(0f, 1f)
                    val progressSweep = sweepAngle * progress

                    val arcColor = when {
                        speedValue < 40 -> Color(0xFF4CAF50) // green
                        speedValue < 90 -> Color(0xFFFFC107) // yellow
                        else -> Color(0xFFF44336) // red
                    }

                    if (progress > 0f) {
                        drawArc(
                            color = arcColor,
                            startAngle = startAngle,
                            sweepAngle = progressSweep,
                            useCenter = false,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                }

                // Center speed text
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = displaySpeed,
                        color = TextPrimary,
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 76.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "км/ч",
                        color = TextSecondary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
