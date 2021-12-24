package ru.emkn.kotlin.sms.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.select
import ru.emkn.kotlin.sms.MAX_TEXT_FIELD_SIZE
import ru.emkn.kotlin.sms.io.MultilineWritable
import kotlin.reflect.KFunction1

enum class ResultType(val russian: String) {
    WEIGHT("Стоимость") {
        override val implementation = ::PersonalResultByWeight
    },
    TIME("Время") {
        override val implementation = ::PersonalResultByTime
    };

    abstract val implementation: KFunction1<Group, PersonalResult>
}

object GroupTable : IntIdTable("groups") {
    val name: Column<String> = varchar("name", MAX_TEXT_FIELD_SIZE)
    val routeID: Column<EntityID<Int>> = reference("routes", RouteTable)
    // val resultType: Column<ResultType> = enumerationByName("resultType", MAX_TEXT_FIELD_SIZE, ResultType::class)
}

/**
 *  Class for saving data about one sports group whose members follow the same route
 */
class Group(id: EntityID<Int>) : IntEntity(id), MultilineWritable {

    companion object : IntEntityClass<Group>(GroupTable) {

        fun findByName(name: String): Group {
            return Group.find { GroupTable.name eq name }.first()
        }

        fun checkByName(name: String): Boolean = !Group.find { GroupTable.name eq name }.empty()

        fun create(name: String, route: Route): Group {
            return Group.new {
                this.name = name
                this.route = route
            }
        }

        fun create(name: String, resultType: ResultType, routeName: String): Group {
            return Group.new {
                this.name = name
                this.personalResult = resultType.implementation(this)
                this.routeID = RouteTable.select { RouteTable.name eq routeName }.first()[RouteTable.id]
            }
        }
    }

    var name by GroupTable.name
    val members by Participant referrersOn ParticipantTable.groupID
    var routeID by GroupTable.routeID

    // var resultType by GroupTable.resultType
    var personalResult: PersonalResult = PersonalResultByTime(this)

    var route: Route
        get() = Route[routeID]
        set(route) {
            routeID = RouteTable.select { RouteTable.id eq route.id }.first()[GroupTable.id]
        }

    fun change(name: String, routeName: String) {
        this.name = name
        this.route = Route.findByName(routeName)
    }

    override fun toString() = this.name

    override fun toMultiline(): List<List<Any?>> {
        val res: MutableList<List<Any?>> = mutableListOf(listOf(this))
        res.addAll(this.members.toList().map { it.toLine() })
        return res
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Group

        if (name != other.name) return false
        if (route != other.route) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + route.hashCode()
        return result
    }

}
