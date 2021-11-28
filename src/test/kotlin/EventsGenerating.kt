import java.time.LocalDate
import ru.emkn.kotlin.sms.FileType
import ru.emkn.kotlin.sms.io.Writer
import ru.emkn.kotlin.sms.objects.Event
import java.nio.file.Path
import kotlin.random.Random

fun generateEvents(path: Path, amountOfEvents: Int = 3, random: Random = Random(0)) : List<Event> {
    val events = List(amountOfEvents) {
        Event(
            "event$it",
            LocalDate.of(random.nextInt(2022, 2050), random.nextInt(1, 13), random.nextInt(1, 29))
        )
    }
    val writer = Writer(path.resolve("event.csv").toFile(), FileType.CSV)
    writer.add(listOf("Название", "Дата"))
    writer.addAllLines(events)
    writer.write()
    return events
}