package dev.bayan_ibrahim.my_dictionary.data_source.local.timer

import dev.bayan_ibrahim.my_dictionary.domain.model.MDTimer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration

class MDTimerDataSourceImpl : MDTimerDataSource {
    override fun assignOneShotTimer(
        duration: Duration,
    ): Flow<MDTimer.OneShot> = flow {
        emit(MDTimer.OneShot(duration = duration, completed = false))
        delay(duration)
        emit(MDTimer.OneShot(duration = duration, completed = true))
    }

    override fun assignFixedRateTimer(
        duration: Duration,
        rate: Duration,
    ): Flow<MDTimer.FixedRate> = flow {
        emit(
            MDTimer.FixedRate(
                duration = duration,
                completed = false,
                rate = rate,
                passedDuration = Duration.ZERO
            )
        )
        var passedDuration = Duration.ZERO
        while (passedDuration < duration) {
            delay(rate)
            passedDuration += rate
            emit(
                MDTimer.FixedRate(
                    duration = duration,
                    completed = false,
                    rate = rate,
                    passedDuration = passedDuration
                )
            )
        }

        emit(
            MDTimer.FixedRate(
                duration = duration,
                completed = true,
                rate = rate,
                passedDuration = passedDuration
            )
        )
    }
}