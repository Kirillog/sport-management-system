package ru.emkn.kotlin.sms.view.creators

import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.controller.Creator
import ru.emkn.kotlin.sms.model.Participant

class ParticipantCreator : ItemCreator<Participant>() {

    override val fields = listOf(
        ItemCreatorInputField("Name", ObjectFields.Name),
        ItemCreatorInputField("Surname", ObjectFields.Surname),
        ItemCreatorInputField("Group", ObjectFields.Group),
        ItemCreatorInputField("Birthday year", ObjectFields.BirthdayYear),
        ItemCreatorInputField("Grade", ObjectFields.Grade),
        ItemCreatorInputField("Team", ObjectFields.Team)
    )

    override fun createAction(input: Map<ObjectFields, String>) {
        Creator.createParticipantFrom(input)
    }
}
