package com.antidoto.ui.util

import androidx.annotation.StringRes
import com.antidoto.R
import com.antidoto.domain.model.Mood
import com.antidoto.domain.model.Trigger

@StringRes
fun Mood.labelRes(): Int = when (this) {
    Mood.WELL -> R.string.checkin_well
    Mood.NEUTRAL -> R.string.checkin_neutral
    Mood.ANXIOUS -> R.string.checkin_anxious
    Mood.TIRED -> R.string.checkin_tired
}

@StringRes
fun Trigger.labelRes(): Int = when (this) {
    Trigger.BOREDOM -> R.string.checkin_trigger_boredom
    Trigger.ANXIETY -> R.string.checkin_trigger_anxiety
    Trigger.HABIT -> R.string.checkin_trigger_habit
    Trigger.WORK -> R.string.checkin_trigger_work
}
