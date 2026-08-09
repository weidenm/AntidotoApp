package com.antidoto.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.antidoto.R
import java.util.concurrent.TimeUnit

/** Formats a duration in millis as a short "2h 15min" / "45min" label. */
@Composable
fun formatDuration(durationMs: Long): String {
    val totalMinutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
    val hours = (totalMinutes / 60).toInt()
    val minutes = (totalMinutes % 60).toInt()
    return if (hours > 0) {
        stringResource(R.string.usage_duration_hm, hours, minutes)
    } else {
        stringResource(R.string.usage_duration_min, minutes)
    }
}
