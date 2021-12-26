package ru.emkn.kotlin.sms.view

import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.controller.CompetitionController

object StateSwitcher {

    fun setTossed(gui: GUI) {
        gui.tables.forEach {
            it.header.setReadOnly(true)
        }
        gui.participantsTable.header.setVisibility(ObjectFields.StartTime, true)
    }

    fun setUnTossed(gui: GUI) {
        gui.tables.forEach {
            it.header.setReadOnly(false)
        }
        gui.participantsTable.header.setVisibility(ObjectFields.StartTime, false)
    }

    fun doToss(gui: GUI, bottomBar: BottomAppBar) {
        setTossed(gui)
        CompetitionController.toss()
        bottomBar.setMessage("Tossed competed")
        gui.pushState(GUI.State.EditRuntimeDump)
    }

    fun undoToss(gui: GUI, bottomBar: BottomAppBar) {
        setUnTossed(gui)
        CompetitionController.undoToss()
        bottomBar.setMessage("")
        gui.popState()
    }

}