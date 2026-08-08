package com.antidoto.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.antidoto.R
import com.antidoto.domain.model.Mood
import com.antidoto.domain.model.Trigger
import com.antidoto.ui.util.labelRes
import com.antidoto.ui.viewmodels.CheckInViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CheckInScreen(viewModel: CheckInViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var selectedMood by remember { mutableStateOf<Mood?>(null) }
    var selectedTrigger by remember { mutableStateOf<Trigger?>(null) }

    val savedMessage = stringResource(R.string.checkin_saved)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            StreakCard(streakDays = uiState.streakDays)

            Text(
                text = stringResource(R.string.checkin_mood_section),
                style = MaterialTheme.typography.titleMedium,
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Mood.entries.forEach { mood ->
                    FilterChip(
                        selected = selectedMood == mood,
                        onClick = { selectedMood = mood },
                        label = { Text(stringResource(mood.labelRes())) },
                    )
                }
            }

            Text(
                text = stringResource(R.string.checkin_trigger_section),
                style = MaterialTheme.typography.titleMedium,
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Trigger.entries.forEach { trigger ->
                    FilterChip(
                        selected = selectedTrigger == trigger,
                        onClick = { selectedTrigger = trigger },
                        label = { Text(stringResource(trigger.labelRes())) },
                    )
                }
            }

            Button(
                onClick = {
                    val mood = selectedMood
                    val trigger = selectedTrigger
                    if (mood != null && trigger != null) {
                        viewModel.recordCheckIn(mood, trigger)
                        selectedMood = null
                        selectedTrigger = null
                        scope.launch { snackbarHostState.showSnackbar(savedMessage) }
                    }
                },
                enabled = selectedMood != null && selectedTrigger != null,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.checkin_save))
            }

            Text(
                text = stringResource(R.string.checkin_today_count, uiState.todayCount),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun StreakCard(streakDays: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.streak_label),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(4.dp))
            if (streakDays > 0) {
                Text(
                    text = pluralStringResource(R.plurals.streak_days_plural, streakDays, streakDays),
                    style = MaterialTheme.typography.headlineMedium,
                )
            } else {
                Text(
                    text = stringResource(R.string.streak_zero_hint),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
