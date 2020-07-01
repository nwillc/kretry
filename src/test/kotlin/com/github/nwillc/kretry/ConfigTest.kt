package com.github.nwillc.kretry

import com.github.nwillc.kretry.Config.Companion.DEFAULT_ATTEMPTS
import com.github.nwillc.kretry.Config.Companion.DEFAULT_DELAY
import com.github.nwillc.kretry.Config.Companion.DEFAULT_DELAY_MILLIS
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.temporal.ChronoUnit

class ConfigTest {
    @Test
    fun `should instantiate a default config`() {
        val config = Config<String>()
        assertThat(config.attempts).isEqualTo(DEFAULT_ATTEMPTS)
        assertThat(config.delay).isEqualTo(DEFAULT_DELAY)
        assertThat(config.backOff).isEqualTo(BackOff.NONE)
        assertThat(config.predicate("")).isTrue()
    }
}
