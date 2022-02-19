package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.controller.StateTable
import ru.emkn.kotlin.sms.model.*

const val MAX_TEXT_FIELD_SIZE = 127
const val DB_HEADER = "jdbc:h2"
const val DB_DRIVER = "org.h2.Driver"

enum class FileType {
    JSON, CSV
}

val DB_TABLES = listOf(
    EventTable,
    RouteCheckpointsTable,
    TossTable,
    PersonalResultTable,
    TeamResultTable,
    TimestampTable,
    CheckpointTable,
    ParticipantTable,
    GroupTable,
    RouteTable,
    TeamTable,
    StateTable
)

val english = mapOf(
    "Полный" to "Full",
    "Стоимость" to "Weight",
    "Время" to "Time",
    "Выборочный" to "Selective"
)


const val maxTextLength = 127

enum class ObjectFields {
    ID,
    Name,
    Surname,
    Group,
    Time,
    Team,
    BirthdayYear,
    Grade,
    StartTime,
    Date,
    RouteName,
    CheckPoints,
    Type,
    Amount,
    Weight,
    ResultType,
    Full,
    Selective,
    Penalty,
    DeltaFromLeader,
    FinishTime,
    PlaceInGroup
}

