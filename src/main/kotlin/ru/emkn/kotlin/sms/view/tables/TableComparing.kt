package ru.emkn.kotlin.sms.view.tables

import ru.emkn.kotlin.sms.ObjectFields
import java.time.LocalDate
import java.time.LocalTime

class TableComparing {
    companion object {
        fun <T> compareByLocalDate(field: ObjectFields) = compareBy<Table<T>.TableRow> {
            val text = it.getData(field)
            try {
                val (date, month, year) = text.split(".").map(String::toInt)
                return@compareBy LocalDate.of(year, month, date)
            } catch (e: Exception) {
                throw IllegalArgumentException("Can't represent $text as LocalTime")
            }
        }

        fun <T> compareByLocalTime(field: ObjectFields) = compareBy<Table<T>.TableRow> {
            val text = it.getData(field)
            try {
                val values = text.split(":")
                val hours = values[0].toInt()
                val minutes = values[1].toInt()
                val seconds = if (values.size < 3) 0 else values[2].toInt()
                return@compareBy LocalTime.of(hours, minutes, seconds)
            } catch (e: Exception) {
                throw IllegalStateException()
            }
        }

        fun <T> compareByString(field: ObjectFields) = compareBy<Table<T>.TableRow> {
            it.getData(field)
        }

        fun <T> compareByInt(field: ObjectFields) = compareBy<Table<T>.TableRow> {
            val text = it.getData(field)
            text.toIntOrNull() ?: throw IllegalStateException("Can't represent $text as Int")
        }

        fun <T> compareByLong(field: ObjectFields) = compareBy<Table<T>.TableRow> {
            val text = it.getData(field)
            text.toLongOrNull() ?: throw IllegalStateException("Can't represent $text as Int")
        }
    }
}

