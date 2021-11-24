
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import org.junit.Test
import ru.emkn.kotlin.sms.FileType
import ru.emkn.kotlin.sms.io.Writer
import ru.emkn.kotlin.sms.objects.Participant
import java.io.File
import kotlin.test.assertEquals


class WriterTest {
    private val fileName = "WriterTest.csv"
    private val file = File(fileName).also(File::deleteOnExit)
    private val writer = Writer(file, FileType.CSV)

    @Test
    fun testWriteApplicant() {
        val appA = Participant("Vasya", "Pupkin", 1998, "30M", "Byaka", "I")
        val appB = Participant("Petya", "Loopkin", 2189, "18M", "Buka")

        writer.add(appA)
        writer.add(appB)
        writer.addAll(listOf(appB, appA))
        writer.write()

        val correct = listOf(
            listOf("Vasya", "Pupkin", "1998", "30M", "Byaka", "I"),
            listOf("Petya", "Loopkin", "2189", "18M", "Buka", ""),
            listOf("Petya", "Loopkin", "2189", "18M", "Buka", ""),
            listOf("Vasya", "Pupkin", "1998", "30M", "Byaka", "I")
        )
        val csvData = csvReader().readAll(file)
        assertEquals(correct, csvData)
    }

    @Test
    fun testWriteString() {
        writer.add("Hello")
        writer.add("CSV")
        writer.add(listOf("string", "input"))
        writer.write()

        val correct = listOf(
            listOf("Hello", ""),
            listOf("CSV", ""),
            listOf("string", "input")
        )
        val csvData = csvReader().readAll(file)
        assertEquals(correct, csvData)
    }
}

