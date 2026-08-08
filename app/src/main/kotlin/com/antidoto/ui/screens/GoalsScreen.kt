package com.antidoto.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.antidoto.R
import com.antidoto.data.db.entities.AppUsageEntry
import com.antidoto.data.db.entities.Goal
import androidx.compose.ui.res.stringResource
import com.antidoto.ui.viewmodels.GoalsViewModel

@Composable
fun GoalsScreen(viewModel: GoalsViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.goals_screen_title),
                style = MaterialTheme.typography.headlineSmall,
            )
        }

        if (uiState.apps.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.goals_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            items(uiState.apps, key = { it.id }) { app ->
                GoalRow(
                    app = app,
                    goal = uiState.goalsByAppId[app.packageName],
                    onSave = { minutes -> viewModel.setGoal(app.packageName, minutes) },
                )
            }
        }
    }
}

@Composable
private fun GoalRow(
    app: AppUsageEntry,
    goal: Goal?,
    onSave: (Int) -> Unit,
) {
    var input by rememberSaveable(app.id) {
        mutableStateOf(goal?.targetMinutes?.toString() ?: "")
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = app.appName,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = if (goal != null) {
                    stringResource(R.string.goals_target_set, goal.targetMinutes)
                } else {
                    stringResource(R.string.goals_no_target)
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { new -> input = new.filter(Char::isDigit).take(4) },
                    label = { Text(stringResource(R.string.goals_target_minutes)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                )
                TextButton(
                    onClick = { input.toIntOrNull()?.let(onSave) },
                    enabled = input.toIntOrNull()?.let { it > 0 } == true,
                ) {
                    Text(stringResource(R.string.goals_save))
                }
            }
        }
    }
}
