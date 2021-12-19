package ru.emkn.kotlin.sms.view.creators

import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.controller.Creator
import ru.emkn.kotlin.sms.model.Checkpoint

class CheckpointCreator : ItemCreator<Checkpoint>() {

    override val fields = listOf(
        ItemCreatorInputField("Name", ObjectFields.Name),
        ItemCreatorInputField("Weight", ObjectFields.Weight)
    )

    override fun createAction(input: Map<ObjectFields, String>) {
        Creator.createCheckPointFrom(input)
    }
}