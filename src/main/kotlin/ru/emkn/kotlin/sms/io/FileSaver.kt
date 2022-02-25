package ru.emkn.kotlin.sms.io

import ru.emkn.kotlin.sms.FileType
import ru.emkn.kotlin.sms.model.Competition
import ru.emkn.kotlin.sms.model.Group
import ru.emkn.kotlin.sms.model.Participant
import ru.emkn.kotlin.sms.model.Team
import ru.emkn.kotlin.sms.view.tables.correctTime
import java.io.File

class FileSaver(file: File) : Saver {
    private val writer: Writer = Writer(file, FileType.CSV)
    override fun saveResults() {
        writer.add(
            listOf(
                "Место",
                "Номер",
                "Имя",
                "Фамилия",
                "Г.р.",
                "Разр.",
                "Время старта",
                "Время финиша",
                "Штраф",
                "Отставание"
            )
        )

        Group.all().forEach { group ->
            writer.add(group.name)
            val sortedGroup = group.personalResult.sort()
            sortedGroup.forEach { participant ->
                writer.add(participant) { Participant.formatterForPersonalResults(it) }
            }
        }
        writer.write()
    }


    override fun saveToss() {
        writer.add(listOf("ID", "Name", "Surname", "Birthday Year", "Team", "Grade", "Start time", "Group"))
        writer.addAll(Participant.all().toList().map {
            listOf(it.id, it.name, it.surname, it.birthdayYear, it.team, it.grade, correctTime(it.startTime), it.group)
        })
        writer.write()
    }

    override fun saveTeamResults() {
        writer.add(listOf("Номер", "Название", "Очки"))
        val sortedTeams = Competition.teamResult.sortTeams(Team.all().toSet())
        sortedTeams.forEachIndexed { index, team ->
            writer.add<SingleLineWritable>(team) { listOf(index + 1) + it.toLine() }
        }
        writer.write()
    }
}
