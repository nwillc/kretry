package com.github.nwillc.kretry

import java.lang.Thread.sleep

// todo backoff
// todo validator
// todo coroutines

fun <C : Any, T> C.retry(attempts: Int = 20, block: C.() -> T): T {
    var attempted: Int = 0
    while (attempted < attempts) {
        try {
            return block()
        } catch (e: Exception) {
            println("failure $e")
        }
        sleep(1000)
        attempted++
    }
    throw Exception("Max retries reached: $attempts")
}
