package ru.emkn.kotlin.sms.targets

import ru.emkn.kotlin.sms.FileType
import ru.emkn.kotlin.sms.Target
import ru.emkn.kotlin.sms.io.Writer
import ru.emkn.kotlin.sms.objects.Competition
import java.nio.file.Path
import java.time.LocalTime

fun tossTarget(competitionPath: Path) : Competition {
    val competition = Competition(competitionPath, Target.TOSS)
    competition.simpleToss(LocalTime.NOON, 5)
    val writer = Writer(competition.path.resolve("protocols/toss.csv").toFile(), FileType.CSV)

    writer.add(listOf("Номер", "Имя", "Фамилия", "Г.р.", "Команда", "Разр.", "Время старта"))
    writer.addAll(competition.groups)
    writer.write()
    return competition
}