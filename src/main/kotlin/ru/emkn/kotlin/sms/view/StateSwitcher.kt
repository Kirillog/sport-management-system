package ru.emkn.kotlin.sms.view

import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.controller.Controller

object StateSwitcher {

    fun setTossed(view: View) {
        view.tables.forEach {
            it.header.setReadOnly(true)
            it.loadButton = false
            it.addButton = false
        }
        view.timestampTable.loadButton = true
        view.timestampTable.addButton = true
        view.participantsTable.header.setVisibility(ObjectFields.StartTime, true)
        TopAppBar.buttons.first { it.text == "Timestamps" }.visible = true
    }

    fun setResulted(view: View) {
        view.tables.forEach {
            it.loadButton = false
            it.addButton = false
            it.header.setVisibility(true)
        }
        view.timestampTable.header.setReadOnly(true)
    }

    fun setUnResulted(view: View) {
        view.teamTable.header.setVisibility(ObjectFields.ResultType, false)
        view.participantsTable.header.setVisibility(ObjectFields.FinishTime, false)
        view.participantsTable.header.setVisibility(ObjectFields.Penalty, false)
        view.participantsTable.header.setVisibility(ObjectFields.PlaceInGroup, false)
        view.participantsTable.header.setVisibility(ObjectFields.DeltaFromLeader, false)
        view.timestampTable.header.setReadOnly(false)
        view.timestampTable.loadButton = true
        view.timestampTable.addButton = true
    }

    fun setUnTossed(view: View) {
        if (TableChooser.state == TableChooser.Table.Timestamps)
            TableChooser.state = TableChooser.Table.Event
        view.tables.forEach {
            it.header.setReadOnly(false)
            it.loadButton = true
            it.addButton = true
        }
        view.participantsTable.header.setVisibility(ObjectFields.StartTime, false)
        TopAppBar.buttons.first { it.text == "Timestamps" }.visible = false

    }

    fun doToss(view: View) {
        Controller.toss()
        setTossed(view)
        BottomAppBar += "Tossed completed"
        view.pushState(View.State.EditRuntimeDump)
    }

    fun doResulted(view: View) {
        Controller.result()
        setResulted(view)
        BottomAppBar += "Results calculated"
        view.pushState(View.State.ShowResults)
    }

    fun undo(view: View) {
        Controller.undo()
        setUnResulted(view)
        BottomAppBar += "Rollback"
        view.popState()
    }

}
