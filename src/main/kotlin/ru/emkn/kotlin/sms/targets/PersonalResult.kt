package ru.emkn.kotlin.sms.targets

import ru.emkn.kotlin.sms.FileType
import ru.emkn.kotlin.sms.io.Writer
import ru.emkn.kotlin.sms.io.formTimestamps
import ru.emkn.kotlin.sms.objects.Competition
import ru.emkn.kotlin.sms.objects.Participant
import ru.emkn.kotlin.sms.objects.makeCompetitionFromStartingProtocol
import java.nio.file.Path
import java.time.format.DateTimeFormatter


fun prepareCompetition(path: Path): Competition {
    val competition = makeCompetitionFromStartingProtocol(path)
    val timestamps = formTimestamps(competition.path)
    val groups = competition.groups
    fillTimestamps(groups, timestamps)
    val trueParticipants = getNotCheaters(groups)
    fillFinishData(trueParticipants)
    sortGroupsByPlace(groups)
    return competition
}

fun personalResultsTarget(path: Path) {
    val competition = prepareCompetition(path)
    val writer = Writer(competition.path.resolve("protocols/results.csv").toFile(), FileType.CSV)
    writer.add(
        listOf(
            "Место", "Номер", "Имя", "Фамилия", "Г.р.", "Разр.", "Время старта", "Время финиша", "Отставание"
        )
    )
    competition.groups.forEach { group ->
        writer.add(group.name)
        group.members.forEach { participant ->
            writer.add(participant, ::formatterForPersonalResults)
        }
    }
    writer.write()
}


private fun formatterForPersonalResults(participant: Participant) = listOf(
    participant.positionInGroup?.place?.toString(),
    participant.id?.toString(),
    participant.name,
    participant.surname,
    participant.birthdayYear.toString(),
    participant.grade,
    participant.startTime?.format(DateTimeFormatter.ISO_LOCAL_TIME),
    participant.finishTime?.format(DateTimeFormatter.ISO_LOCAL_TIME),
    participant.positionInGroup?.laggingFromLeader?.toIntervalString()
)
