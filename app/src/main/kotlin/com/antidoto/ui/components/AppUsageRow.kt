package com.antidoto.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.antidoto.R
import com.antidoto.data.db.entities.AppUsageEntry
import com.antidoto.ui.util.formatDuration

@Composable
fun AppUsageRow(
    entry: AppUsageEntry,
    maxDurationMs: Long,
    modifier: Modifier = Modifier,
) {
    val fraction = if (maxDurationMs > 0) {
        (entry.durationMs.toFloat() / maxDurationMs.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    Column(modifier = modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = entry.appName,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = formatDuration(entry.durationMs),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = fraction,
            modifier = Modifier.fillMaxWidth().height(6.dp),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.usage_open_count, entry.openCount),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
