package ru.emkn.kotlin.sms.view.creators

import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.controller.Creator
import ru.emkn.kotlin.sms.model.Timestamp

class TimestampCreator : ItemCreator<Timestamp>() {

    override val fields = listOf(
        ItemCreatorInputField("Checkpoint", ObjectFields.Name),
        ItemCreatorInputField("Time", ObjectFields.Time),
        ItemCreatorInputField("Participant ID", ObjectFields.ID)
    )

    override fun createAction(input: Map<ObjectFields, String>) {
        Creator.createTimeStampFrom(input)
    }
}