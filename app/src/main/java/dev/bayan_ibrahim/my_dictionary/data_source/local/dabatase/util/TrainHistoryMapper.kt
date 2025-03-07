package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util

import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.date.asEpochMillisecondsInstant
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.TrainHistoryEntity
import dev.bayan_ibrahim.my_dictionary.domain.model.train_history.TrainHistory
import dev.bayan_ibrahim.my_dictionary.domain.model.train_history.WordTrainHistory

fun Collection<TrainHistoryEntity>.toTrainHistoryModels(
    /**
     * the factor that time millis would be divided to for grouping, higher values means less accuracy
     * for example for value 1000 that means for each different second it would be considered a separated History
     * and the total history would take the first date between grouped dates
     */
    timeGroupByFactor: Long = 60_000,
): List<TrainHistory> = groupBy {
    it.time / timeGroupByFactor
}.map { (factoredTime, entities) ->
    TrainHistory(
        time = (factoredTime * timeGroupByFactor).asEpochMillisecondsInstant(),
        // entities is not empty cause it is from group by
        words = entities.map {
            WordTrainHistory(
                id = it.id,
                wordId = it.wordId,
                questionWord = it.questionWord,
                trainResult = it.trainResult,
                trainType = it.trainType,
            )
        }
    )
}

fun TrainHistory.toTrainHistoryEntities(): List<TrainHistoryEntity> {
    return words.map { item ->
        TrainHistoryEntity(
            id = item.id,
            wordId = item.wordId,
            time = time.toEpochMilliseconds(),
            questionWord = item.questionWord,
            trainType = item.trainType,
            trainResult = item.trainResult
        )
    }
}