package ru.emkn.kotlin.sms.view.creators

import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.controller.Creator
import ru.emkn.kotlin.sms.controller.Editor
import ru.emkn.kotlin.sms.model.Group
import ru.emkn.kotlin.sms.model.Participant

class GroupCreator : ItemCreator<Group>() {

    override val fields = listOf(
        ItemCreatorInputField("Name", ObjectFields.Name),
        ItemCreatorInputField("Result type", ObjectFields.ResultType),
        ItemCreatorInputField("Route name", ObjectFields.RouteName),
    )

    override fun createAction(input: Map<ObjectFields, String>) {
        Creator.createGroupFrom(input)
    }
}
