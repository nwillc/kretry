package com.github.nwillc.kretry

import java.lang.Thread.sleep
import java.util.concurrent.TimeUnit

// todo coroutines
// todo config builder

fun <C : Any, T> C.retry(config: Config<T> = Config(), block: C.() -> T): T {
    var attempted: Int = 0
    while (attempted < config.attempts) {
        try {
            val result = block()
            if (config.predicate(result))
                return result
            else
                println("failed predicate")
        } catch (e: Exception) {
            println("failure $e")
        }
        attempted++
        val delay = delay(attempted, config)
        sleep(delay.unit.toMillis(delay.amount))
    }

    throw Exception("Max retries reached: ${config.attempts}")
}

enum class BackOff {
    NONE,
    LINER
}

data class Delay (
    val unit: TimeUnit = TimeUnit.MILLISECONDS,
    val amount: Long = 500
)

data class Config<T>(
    val attempts: Int = 20,
    val delay: Delay = Delay(),
    val backOff: BackOff = BackOff.NONE,
    val predicate: (T)->Boolean = { true }
)

fun <T> delay(attempt: Int, config: Config<T>): Delay {
    return when (config.backOff) {
        BackOff.NONE -> config.delay
        BackOff.LINER -> config.delay.copy(amount = config.delay.amount * attempt)
    }
}
