package ru.emkn.kotlin.sms.view

import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.controller.CompetitionController

object StateSwitcher {

    fun setTossed(gui: GUI) {
        gui.tables.forEach {
            it.header.setReadOnly(true)
        }
        gui.participantsTable.header.setVisibility(ObjectFields.StartTime, true)
        AppTopBar.buttons.first { it.text == "Timestamps" }.visible = true
    }

    fun setResulted(gui: GUI) {
        gui.tables.forEach {
            it.header.setVisibility(true)
        }
        gui.timestampTable.header.setReadOnly(true)
    }

    fun setUnResulted(gui: GUI) {
        gui.teamTable.header.setVisibility(ObjectFields.ResultType, false)
        gui.participantsTable.header.setVisibility(ObjectFields.FinishTime, false)
        gui.participantsTable.header.setVisibility(ObjectFields.Penalty, false)
        gui.participantsTable.header.setVisibility(ObjectFields.PlaceInGroup, false)
        gui.participantsTable.header.setVisibility(ObjectFields.DeltaFromLeader, false)
        gui.timestampTable.header.setReadOnly(false)
    }

    fun setUnTossed(gui: GUI) {
        if (CompetitionDataPresenter.state == CompetitionDataPresenter.Table.Timestamps)
            CompetitionDataPresenter.state = CompetitionDataPresenter.Table.Event
        gui.tables.forEach {
            it.header.setReadOnly(false)
        }
        gui.participantsTable.header.setVisibility(ObjectFields.StartTime, false)
        AppTopBar.buttons.first { it.text == "Timestamps" }.visible = false

    }

    fun doToss(gui: GUI, bottomBar: BottomAppBar) {
        CompetitionController.toss()
        setTossed(gui)
        bottomBar.setMessage("Tossed completed")
        gui.pushState(GUI.State.EditRuntimeDump)
    }

    fun undoToss(gui: GUI, bottomBar: BottomAppBar) {
        CompetitionController.undoToss()
        setUnTossed(gui)
        bottomBar.setMessage("Rollback")
        gui.popState()
    }

    fun doResulted(gui: GUI, bottomBar: BottomAppBar) {
        CompetitionController.result()
        setResulted(gui)
        bottomBar.setMessage("Results calculated")
        gui.pushState(GUI.State.ShowResults)
    }

    fun undoResulted(gui: GUI, bottomBar: BottomAppBar) {
        CompetitionController.undoResult()
        setUnResulted(gui)
        bottomBar.setMessage("Rollback")
        gui.popState()
    }

}