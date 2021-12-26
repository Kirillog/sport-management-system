package ru.emkn.kotlin.sms.view.tables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import org.jetbrains.exposed.sql.transactions.transaction
import ru.emkn.kotlin.sms.FileType
import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.io.Writer
import ru.emkn.kotlin.sms.view.*

const val tableDeleteButtonWidth = 10

abstract class Table<T> {

    enum class State {
        Updated, Outdated
    }

    abstract inner class TableRow {

        val header
            get() = this@Table.header

        abstract val cells: Map<ObjectFields, TableCell>
        abstract val id: Int

        open val changes: Map<ObjectFields, String>
            get() = header.visibleColumns.associate {
                val cell = cells[it.field] ?: throw IllegalStateException("Cell of ${it.field} not exists")
                it.field to cell.shownText.value
            }

        fun delete() {
            if (!header.deleteButton)
                return
            deleteAction()
        }

        abstract fun saveChanges()
        open fun deleteAction() {}

        fun getData(field: ObjectFields): String {
            val cell = cells[field] ?: throw NoSuchElementException("No field $field in cell")
            return cell.getText()
        }

        fun checkFilter(): Boolean {
            for (column in header.visibleColumns) {
                if (column.filterString.value !in getData(column.field))
                    return false
            }
            return true
        }
    }

    abstract val header: TableHeader<T>
    abstract val rows: List<TableRow>

    var state by mutableStateOf(State.Updated)

    open var addButton: Boolean = true
    open val creatingState: GUI.State? = null
    open val loadAction: () -> Unit = {}
    open var loadButton: Boolean = true

    val sortedFilteredRows
        get() = transaction {
            rows.sortedWith(header.comparator)
                .filter { it.checkFilter() }
        }

    fun load(bottomAppBar: BottomAppBar) {
        try {
            loadAction()
        } catch (e: Exception) {
            bottomAppBar.setMessage(e.message ?: "undefined error")
        }
    }
}

@Composable
fun <T> draw(tableRow: Table<T>.TableRow, bottomAppBar: BottomAppBar) {
    if (tableRow.header.state == TableHeader.State.Outdated)
        tableRow.header.state = TableHeader.State.Updated
    var rowSize by remember { mutableStateOf(IntSize.Zero) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged {
                rowSize = it
            },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (tableRow.header.deleteButton)
            Box(modifier = Modifier.border(BorderStroke(1.dp, Color.Black))) {
                IconButton(
                    onClick = { tableRow.delete() },
                    modifier = Modifier.size(tableDeleteButtonWidth.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Delete", tint = Color.Black)
                }
            }
        val elementsInRow = tableRow.header.columns.count { it.visible }

        val cellWidth = if (tableRow.header.deleteButton)
            ((rowSize.width - tableDeleteButtonWidth) / elementsInRow).dp
        else
            (rowSize.width / elementsInRow).dp

        for (columnHeader in tableRow.header.columns) {
            if (columnHeader.visible)
                draw(
                    tableRow.cells[columnHeader.field]
                        ?: throw IllegalStateException("Cell of ${columnHeader.field} not exists"),
                    cellWidth,
                    columnHeader.readOnly,
                    bottomAppBar
                )
        }
    }
}

@Composable
fun <F> draw(gui: GUI, bottomAppBar: BottomAppBar, table: Table<F>) {
    if (table.state == Table.State.Outdated)
        table.state = Table.State.Updated

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Box(modifier = Modifier.border(BorderStroke(1.dp, Color.Black))) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                val createState = table.creatingState
                if (table.loadButton) {
                    draw(
                        ActionButton("Load") {
                            table.load(bottomAppBar)
                        }
                    )
                }
                if (createState != null && table.addButton) {
                    draw(
                        ActionButton("Add") {
                            gui.pushState(createState)
                        }
                    )
                }
                draw(
                    ActionButton("Export to csv") {
                        PathChooser("Save to csv table...", ".csv", "Csv table").choose()?.let { file ->
                            val writer = Writer(file, FileType.CSV)
                            transaction {
                                writer.add(table)
                            }
                            writer.write()
                            bottomAppBar.setMessage("File was successfully saved to $file")
                        } ?: bottomAppBar.setMessage("File not selected")
                    }
                )
            }
        }
        draw(table.header)

        table.sortedFilteredRows.forEach {
            draw(it, bottomAppBar)
        }
        Box(Modifier.height(BottomAppBar.height))
    }
}
