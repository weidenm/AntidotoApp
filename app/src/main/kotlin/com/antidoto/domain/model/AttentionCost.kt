package com.antidoto.domain.model

/**
 * Projected yearly cost of the current daily attention spend, expressed in
 * relatable units. Values are extrapolations meant for awareness, not precise
 * predictions.
 */
data class AttentionCost(
    val annualHours: Int,
    val booksNotRead: Int,
    val coursesNotDone: Int,
)
