package ru.emkn.kotlin.sms.view.tables

import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.controller.Controller
import ru.emkn.kotlin.sms.controller.Deleter
import ru.emkn.kotlin.sms.controller.Editor
import ru.emkn.kotlin.sms.model.Route
import ru.emkn.kotlin.sms.view.PathChooser
import ru.emkn.kotlin.sms.view.View

class RouteTable : Table<Route>() {

    private val routes
        get() = Route.all().toList()

    override val header = TableHeader<Route>(
        listOf(
            TableColumn(
                "Name",
                ObjectFields.Name,
                visible = true, readOnly = false,
                comparator = TableComparing.compareByString(ObjectFields.Name),
                getterGenerator = { { it.name } }
            ),
            TableColumn(
                "Type",
                ObjectFields.Type,
                visible = true, readOnly = false,
                comparator = TableComparing.compareByString(ObjectFields.Type),
                getterGenerator = { { it.type.toString() } }
            ),
            TableColumn(
                "Amount",
                ObjectFields.Amount,
                visible = true, readOnly = false,
                comparator = TableComparing.compareByInt(ObjectFields.Amount),
                getterGenerator = { { it.amountOfCheckpoint.toString() } }
            ),
            TableColumn(
                "Checkpoints",
                ObjectFields.CheckPoints,
                visible = true, readOnly = false,
                comparator = TableComparing.compareByString(ObjectFields.CheckPoints),
                getterGenerator = {
                    {
                        it.checkpoints.toList().joinToString(",") { it.name }
                    }
                }
            )
        ), iconsBar = true)

    inner class RouteTableRow(private val route: Route) : TableRow() {
        override val cells = header.makeTableCells(route, ::saveChanges)

        override val id: Int = route.id.value

        override fun saveChanges() {
            Editor.editRoute(route, changes)
        }

        override fun deleteAction() {
            Deleter.deleteRoute(id)
            state = State.Outdated
        }

    }


    override val rows: List<TableRow>
        get() = routes.map { RouteTableRow(it) }

    override val creatingState = View.State.CreateRoute

    override val loadAction = {
        val routesFile = PathChooser("Choose routes", ".csv", "Routes").choose()
        Controller.loadRoutes(routesFile?.toPath())
        state = State.Outdated
    }
}
