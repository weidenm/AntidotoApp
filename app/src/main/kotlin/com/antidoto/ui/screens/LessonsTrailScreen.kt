package com.antidoto.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.antidoto.R
import com.antidoto.domain.model.LessonStatus
import com.antidoto.domain.model.LessonTrailItem
import com.antidoto.ui.viewmodels.LessonsViewModel

@Composable
fun LessonsTrailScreen(
    viewModel: LessonsViewModel,
    onOpenLesson: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column {
                Text(
                    text = stringResource(R.string.lessons_title),
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.lessons_progress, uiState.completedCount, uiState.total),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        items(uiState.items, key = { it.lesson.id }) { item ->
            LessonTrailRow(item = item, onClick = { onOpenLesson(item.lesson.id) })
        }
    }
}

@Composable
private fun LessonTrailRow(
    item: LessonTrailItem,
    onClick: () -> Unit,
) {
    val locked = item.status == LessonStatus.LOCKED
    val containerColor = when (item.status) {
        LessonStatus.AVAILABLE -> MaterialTheme.colorScheme.primaryContainer
        LessonStatus.COMPLETED -> MaterialTheme.colorScheme.surfaceVariant
        LessonStatus.LOCKED -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {}
            .then(if (locked) Modifier else Modifier.clickable(onClick = onClick)),
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OrderBadge(order = item.lesson.orderIndex + 1, status = item.status)
            Column(Modifier.weight(1f)) {
                Text(
                    text = item.lesson.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = when (item.status) {
                        LessonStatus.COMPLETED -> stringResource(R.string.lessons_completed_badge)
                        LessonStatus.AVAILABLE -> stringResource(R.string.lessons_today)
                        LessonStatus.LOCKED -> stringResource(R.string.lessons_locked_hint)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun OrderBadge(order: Int, status: LessonStatus) {
    val bg = when (status) {
        LessonStatus.COMPLETED -> MaterialTheme.colorScheme.primary
        LessonStatus.AVAILABLE -> MaterialTheme.colorScheme.primary
        LessonStatus.LOCKED -> MaterialTheme.colorScheme.outline
    }
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(bg),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (status == LessonStatus.COMPLETED) "✓" else order.toString(),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}
