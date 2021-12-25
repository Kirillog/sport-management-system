package ru.emkn.kotlin.sms.view.creators

import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.controller.Creator
import ru.emkn.kotlin.sms.model.Route

class RoutesCreator : ItemCreator<Route>() {
    override val fields = listOf(
        ItemCreatorInputField("Название", ObjectFields.Name),
        ItemCreatorInputField("Тип", ObjectFields.Type),
        ItemCreatorInputField("Количество К/П", ObjectFields.Amount),
        ItemCreatorInputField("К/П", ObjectFields.CheckPoints)
    )

    override fun createAction(input: Map<ObjectFields, String>) {
        Creator.createRouteFrom(input)
    }
}