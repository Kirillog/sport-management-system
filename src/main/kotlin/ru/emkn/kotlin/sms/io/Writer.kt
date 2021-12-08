package ru.emkn.kotlin.sms.io

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import mu.KotlinLogging
import ru.emkn.kotlin.sms.FileType
import ru.emkn.kotlin.sms.objects.Group
import ru.emkn.kotlin.sms.objects.Participant
import ru.emkn.kotlin.sms.objects.Team
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

private val logger = KotlinLogging.logger {}

data class OutputBlock(val header: String?, val rows: List<Map<KProperty<*>, String?>>)

enum class OutputFormat(relatedClass: KClass<*>) {
    GROUP(Group::class) {
        override val defaultHeader = listOf("Номер", "Имя", "Фамилия", "Г.р", "Команда", "Разр.", "Время старта")
        override fun<T> getData(group: T): OutputBlock {
            if (group !is Group) throw IllegalStateException()
            val rows = group.members.toString()
            return OutputBlock(group.name)
        }

        override val fieldToStr = mapOf<KProperty<*>, String>(
            Participant::id to "Номер",
            Participant::name to "Имя",
            Participant::surname to "",
            Participant::team to "",

        )
    },
    TEAM(Team::class) {
        override val fieldToStr = mapOf<KProperty<*>, String>(

        )
        override val defaultHeader = listOf<String>()
    };

    abstract fun<T> getData(data: T): OutputBlock
    abstract val defaultHeader: List<String>
    abstract val fieldToStr: Map<KProperty<*>, String>
    val strToField: Map<String, KProperty<*>> = fieldToStr.map { it.value to it.key }.toMap()
}

interface MultilineWritable<T> {
    val header: List<String>
        get() = strToField.keys.toList()

    val strToField: Map<String, KProperty<*>>;
    fun getData(): List<Map<String, String?>>

}

class Writer2(type: MultilineWritable<*>, formatter: Set<KProperty1<*,*>> = setOf()) {
    val header = type.header
    val formattedHeader = type.strToField.filter { !formatter.contains(it.value) }
}

///**
// * Interface for classes that can be printed by [Writer] and takes up more than one line
// */
//interface MultilineWritable {
//    fun toMultiline(): List<List<String?>>
//}

/**
 * Interface for classes that can be printed by [Writer] and takes up one line
 */
interface SingleLineWritable {
    fun toLine(): List<String?>
}

/**
 * Class for comfortable writing objects implemented [MultilineWritable] and [SingleLineWritable] to supported file types.
 * It works on the principle of a buffer. You can add([Writer.add] and [Writer.addAll]) objects to the buffer
 * and write them all when you want with [Writer.write]. After write buffer always clearing
 */
class Writer(private val file: File, val filetype: FileType) {

    val buffer = mutableListOf<List<String?>>()

    fun <T : SingleLineWritable> add(el: T, formatter: (T) -> List<String?> = { it.toLine() }) =
        buffer.add(formatter(el))

    fun <T : SingleLineWritable> addAllLines(el: List<T>, formatter: (T) -> List<String?> = { it.toLine() }) =
        buffer.addAll(el.map { formatter(it) })

    fun <T : MultilineWritable> add(el: T, formatter: (T) -> List<List<String?>> = { it.toMultiline() }) =
        buffer.addAll(formatter(el))

    fun <T : MultilineWritable> addAll(el: List<T>, formatter: (T) -> List<List<String?>> = { it.toMultiline() }) =
        buffer.addAll(el.map { formatter(it) }.flatten())

    fun add(el: String?) {
        if (el != null) buffer.add(listOf(el))
        else logger.warn { "try to write null string" }
    }

    fun add(el: List<String>) = buffer.add(el)

    fun addAll(el: List<List<String>>) = buffer.addAll(el)

    fun clear() = buffer.clear()

    fun write() {
        val rowSize = buffer.maxOf { it.size }
        val shrunkenLines = buffer.map { it + List(rowSize - it.size) { "" } }
        when (filetype) {
            FileType.CSV, FileType.JSON -> csvWriter().writeAll(shrunkenLines, file)
        }
        logger.info { "Written ${buffer.size} objects to ${file.name}" }
        clear()
    }
}