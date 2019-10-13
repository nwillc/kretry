package com.github.nwillc.kretry

import java.lang.Thread.sleep

// todo coroutines

@SuppressWarnings("TooGenericExceptionCaught")
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

    throw RetryExceededException("Max retries reached: ${config.attempts}")
}

fun <T> delay(attempt: Int, config: Config<T>): Delay {
    return when (config.backOff) {
        BackOff.NONE -> config.delay
        BackOff.LINER -> config.delay.copy(amount = config.delay.amount * attempt)
        BackOff.FIBONACHI -> config.delay.copy(amount = config.delay.amount * fibonacci(attempt))
    }
}

internal fun fibonacci(n: Int): Int {
    tailrec fun fibTail(n: Int, first: Int, second: Int): Int = if (n == 0)
        first
    else
        fibTail(n - 1, second, first + second)
    return fibTail(n, 0, 1)
}
