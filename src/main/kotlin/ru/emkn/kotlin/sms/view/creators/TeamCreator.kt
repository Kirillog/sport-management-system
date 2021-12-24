package ru.emkn.kotlin.sms.view.creators

import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.controller.Creator
import ru.emkn.kotlin.sms.controller.Editor
import ru.emkn.kotlin.sms.model.Participant
import ru.emkn.kotlin.sms.model.Team

class TeamCreator : ItemCreator<Team>() {

    override val fields = listOf(
        ItemCreatorInputField("Name", ObjectFields.Name),
    )

    override fun createAction(input: Map<ObjectFields, String>) {
        Creator.createTeamFrom(input)
    }
}
