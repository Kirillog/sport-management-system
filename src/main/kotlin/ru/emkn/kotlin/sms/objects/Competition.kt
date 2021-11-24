package ru.emkn.kotlin.sms.objects

import java.nio.file.Path

data class Competition(val event: Event, val path: Path, val teams: List<Team>, val groups: List<Group>)
