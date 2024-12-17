package dev.bayan_ibrahim.my_dictionary.data_source.local.timer

import dev.bayan_ibrahim.my_dictionary.domain.model.MDTimer
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface MDTimerDataSource {
    fun assignOneShotTimer(duration: Duration): Flow<MDTimer.OneShot>
    fun assignFixedRateTimer(duration: Duration, rate: Duration): Flow<MDTimer.FixedRate>
}