package com.github.nwillc.kretry

import java.util.concurrent.TimeUnit

data class Config<T>(
    var attempts: Int = Int.MAX_VALUE,
    var delay: Delay = Delay(),
    var backOff: BackOff = BackOff.NONE,
    var predicate: (T) -> Boolean = { true }
)

enum class BackOff {
    NONE,
    LINER,
    FIBONACHI
}

data class Delay (
    val unit: TimeUnit = TimeUnit.MILLISECONDS,
    val amount: Long = 500
)
