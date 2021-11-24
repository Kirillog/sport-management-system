package ru.emkn.kotlin.sms.objects

import ru.emkn.kotlin.sms.io.SingleLineWritable

open class Applicant(
    val name: String,
    val surname: String,
    val birthdayYear: Int,
    val group: String,
    val team: String,
    val grade: String? = null,
) : SingleLineWritable {
    override fun toLine(): List<String> {
        val result = mutableListOf(name, surname, birthdayYear.toString(), group, team)
        if (grade != null)
            result.add(grade)
        return result
    }
}