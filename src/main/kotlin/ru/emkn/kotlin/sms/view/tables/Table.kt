package ru.emkn.kotlin.sms.view.tables

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
            if (!header.iconsBar)
                return
            deleteAction()
        }

        abstract fun saveChanges()
        open fun deleteAction() {}

        fun getData(field: ObjectFields): String {
            val cell = cells[field] ?: throw NoSuchElementException("No field $field in cell")
            return cell.getText
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
    open var loadButton: Boolean = true

    open val creatingState: View.State? = null
    open val loadAction: () -> Unit = {}

    val sortedFilteredRows
        get() = transaction {
            rows.sortedWith(header.comparator)
                .filter { it.checkFilter() }
        }

    fun load() {
        try {
            loadAction()
        } catch (e: Exception) {
            BottomAppBar += e.message ?: "undefined error"
        }
    }
}

@Composable
fun <T> TableRow(tableRow: Table<T>.TableRow, index: Int) {
    var rowSize by remember { mutableStateOf(IntSize.Zero) }
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .hoverable(interactionSource)
            .background(if (isHovered) Color.LightGray else Color.White)
            .onSizeChanged {
                rowSize = it
            },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val elementsInRow = tableRow.header.visibleColumns.size
        val cellWidth = if (tableRow.header.iconsBar)
            ((rowSize.width - tableRow.header.iconsBarSize.value.width) / elementsInRow).dp
        else
            (rowSize.width / elementsInRow).dp

        for (columnHeader in tableRow.header.visibleColumns) {
            TableCell(
                tableRow.cells[columnHeader.field]
                    ?: throw IllegalStateException("Cell of ${columnHeader.field} not exists"),
                cellWidth,
                columnHeader.readOnly
            )
        }
        if (tableRow.header.iconsBar)
            IconButton(
                onClick = { tableRow.delete() },
                modifier = Modifier.onSizeChanged { tableRow.header.iconsBarSize.value = it }
            ) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Black)
            }
    }
}

@Composable
fun<T> ButtonsBar(view : View, table : Table<T>) {
    Box(modifier = Modifier.border(BorderStroke(1.dp, Color.Black))) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            val createState = table.creatingState
            if (table.loadButton) {
                ActionButton(
                    ActionButton("Load") {
                        table.load()
                    }
                )
            }
            if (createState != null && table.addButton) {
                ActionButton(
                    ActionButton("Add") {
                        view.pushState(createState)
                    }
                )
            }
            ActionButton(
                ActionButton("Export to csv") {
                    val file = PathChooser("Save to csv table...", ".csv", "Csv table").choose()
                    BottomAppBar += if (file != null) {
                        val writer = Writer(file, FileType.CSV)
                        transaction {
                            writer.add(table)
                        }
                        writer.write()
                        "File was successfully saved to $file"
                    } else {
                        "File not selected"
                    }
                }
            )
        }
    }
}

@Composable
fun <F> Table(view: View, table: Table<F>) {
    if (table.state == Table.State.Outdated)
        table.state = Table.State.Updated
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    ButtonsBar(view, table)
    TableHeader(table.header, lazyListState, coroutineScope)
    LazyColumn(state = lazyListState) {
        itemsIndexed(table.sortedFilteredRows) { index, model ->
            TableRow(model, index)
        }
        item {
            Box(Modifier.height(BottomAppBar.height))
        }
    }
}