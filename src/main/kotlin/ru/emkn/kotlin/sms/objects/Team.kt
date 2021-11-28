package ru.emkn.kotlin.sms.objects


import ru.emkn.kotlin.sms.io.MultilineWritable
import java.time.format.DateTimeFormatter

/**
 * Class for representing all information about one team, with read from single application file
 */
data class Team(val name: String, val members: List<Participant>) : MultilineWritable {
    var result : Long? = null


    fun getResult() :Long {
        val result = this.result
        requireNotNull(result)
        return result
    }

    /**
     * Sets default output format for using with [ru.emkn.kotlin.sms.io.Writer] object.
     * It returns format compatible with toss.csv format
     */
    override fun toMultiline(): List<List<String?>> {
        val result = mutableListOf(listOf<String?>(name))
        result.addAll(members.map {
            listOf(
                it.id?.toString(),
                it.name,
                it.surname,
                it.birthdayYear.toString(),
                it.group,
                it.grade,
                it.startTime?.format(DateTimeFormatter.ISO_LOCAL_TIME)
            )
        })
        return result
    }
}
