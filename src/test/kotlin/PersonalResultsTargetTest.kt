import org.junit.jupiter.api.Test
import ru.emkn.kotlin.sms.model.*
import ru.emkn.kotlin.sms.targets.personalResultsTarget
import kotlin.io.path.Path
import kotlin.test.assertEquals

internal class PersonalResultsTargetTest {

    private val competition2Path = Path("src/test/resources/competition-2")

    @Test
    fun testPersonalResultsTarget() {
        personalResultsTarget(competition2Path)
        val expected = competition2Path.resolve("protocols/expectedResults.csv").toFile().useLines { it.toList() }
        val actual = competition2Path.resolve("protocols/results.csv").toFile().useLines { it.toList() }
        assertEquals(expected, actual)
    }
}