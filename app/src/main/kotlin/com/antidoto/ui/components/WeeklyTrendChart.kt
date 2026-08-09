package com.antidoto.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.antidoto.R
import com.antidoto.domain.model.DailyUsage
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import java.util.concurrent.TimeUnit

private val BR = Locale("pt", "BR")

private fun weeklyContentDescription(title: String, weeklyUsage: List<DailyUsage>): String {
    val perDay = weeklyUsage.joinToString(", ") { day ->
        val minutes = TimeUnit.MILLISECONDS.toMinutes(day.totalMs)
        val label = day.date.dayOfWeek.getDisplayName(TextStyle.FULL, BR)
        "$label ${minutes / 60}h ${minutes % 60}min"
    }
    return "$title. $perDay"
}

@Composable
fun WeeklyTrendChart(
    weeklyUsage: List<DailyUsage>,
    modifier: Modifier = Modifier,
) {
    val maxMs = (weeklyUsage.maxOfOrNull { it.totalMs } ?: 0L).coerceAtLeast(1L)
    val today = LocalDate.now()
    val title = stringResource(R.string.dashboard_weekly_title)
    val chartDescription = weeklyContentDescription(title, weeklyUsage)

    Card(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    // The bar chart is decorative; expose one summary to screen readers.
                    .clearAndSetSemantics { contentDescription = chartDescription },
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                weeklyUsage.forEach { day ->
                    DayBar(
                        day = day,
                        maxMs = maxMs,
                        isToday = day.date == today,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun DayBar(
    day: DailyUsage,
    maxMs: Long,
    isToday: Boolean,
    modifier: Modifier = Modifier,
) {
    val fraction = (day.totalMs.toFloat() / maxMs.toFloat()).coerceIn(0f, 1f)
    val barColor = if (isToday) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom,
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight((1f - fraction).coerceAtLeast(0.0001f)),
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(fraction.coerceAtLeast(0.02f))
                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    .background(barColor),
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = day.date.dayOfWeek.getDisplayName(TextStyle.NARROW, BR).uppercase(BR),
            style = MaterialTheme.typography.labelSmall,
            color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
