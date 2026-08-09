package com.antidoto.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.antidoto.R
import com.antidoto.domain.model.DashboardData
import com.antidoto.ui.components.AppUsageRow
import com.antidoto.ui.components.AttentionCostCard
import com.antidoto.ui.components.UsagePermissionBanner
import com.antidoto.ui.components.WeeklyTrendChart
import com.antidoto.ui.util.formatDuration
import com.antidoto.ui.viewmodels.DashboardUiState
import com.antidoto.ui.viewmodels.DashboardViewModel

@Composable
fun HomeScreen(viewModel: DashboardViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Refresh when returning to the screen (e.g. after granting usage access in Settings).
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.refresh()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val onGrantAccess = {
        context.startActivity(
            Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        )
    }

    Scaffold(
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
    ) { innerPadding ->
        when (val state = uiState) {
            is DashboardUiState.Loading -> CenteredContent(innerPadding) {
                CircularProgressIndicator()
            }

            is DashboardUiState.Error -> CenteredContent(innerPadding) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.dashboard_error_title),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(text = state.message, style = MaterialTheme.typography.bodyMedium)
                }
            }

            is DashboardUiState.Ready -> DashboardContent(
                data = state.data,
                usageAccessGranted = state.usageAccessGranted,
                contentPadding = innerPadding,
                onRefresh = viewModel::refresh,
                onGrantAccess = onGrantAccess,
            )
        }
    }
}

@Composable
private fun DashboardContent(
    data: DashboardData,
    usageAccessGranted: Boolean,
    contentPadding: PaddingValues,
    onRefresh: () -> Unit,
    onGrantAccess: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.dashboard_title),
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.dashboard_total_today) +
                            ": " + formatDuration(data.totalTodayMs),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                FilledTonalButton(onClick = onRefresh) {
                    Text(stringResource(R.string.dashboard_refresh))
                }
            }
        }

        if (!usageAccessGranted) {
            item { UsagePermissionBanner(onGrantClick = onGrantAccess) }
        }

        item { StreakCard(streakDays = data.currentStreakDays) }

        item { AttentionCostCard(cost = data.attentionCost) }

        item { WeeklyTrendChart(weeklyUsage = data.weeklyUsage) }

        if (data.todayEntries.isEmpty()) {
            item { EmptyUsageState() }
        } else {
            item {
                Text(
                    text = stringResource(R.string.dashboard_apps_title),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            val maxDuration = data.todayEntries.first().durationMs
            items(data.todayEntries, key = { it.id }) { entry ->
                AppUsageRow(entry = entry, maxDurationMs = maxDuration)
            }
        }
    }
}

@Composable
private fun EmptyUsageState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.dashboard_empty_title),
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.dashboard_empty_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun CenteredContent(
    contentPadding: PaddingValues,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
