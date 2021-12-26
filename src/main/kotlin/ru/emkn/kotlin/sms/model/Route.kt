package ru.emkn.kotlin.sms.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import ru.emkn.kotlin.sms.MAX_TEXT_FIELD_SIZE
import ru.emkn.kotlin.sms.io.SingleLineWritable

enum class RouteType(val russian: String) {
    FULL("Полный"),
    SELECTIVE("Выборочный")
}

object RouteTable : IntIdTable("routes") {
    val name: Column<String> = varchar("name", MAX_TEXT_FIELD_SIZE)
    val checkpointAmount: Column<Int> = integer("checkpoint_amount")
    val type: Column<RouteType> = enumerationByName("type", MAX_TEXT_FIELD_SIZE, RouteType::class)
}

/**
 * A class for storing a route along which one group of participants runs.
 */
class Route(id: EntityID<Int>) : IntEntity(id), SingleLineWritable {
    companion object : IntEntityClass<Route>(RouteTable) {
        fun findByName(name: String): Route =
            Route.find { RouteTable.name eq name }.first()

        fun checkByName(name: String): Boolean =
            !Route.find { RouteTable.name eq name }.empty()

        fun create(name: String, type: RouteType, checkpointAmount: Int): Route {
            return Route.new {
                this.name = name
                this.type = type
                this.amountOfCheckpoint = checkpointAmount
            }
        }


        fun create(name: String, checkpoints: List<Checkpoint>, type: RouteType, checkpointAmount: Int): Route {
            val res = create(name, type, checkpointAmount)
            checkpoints.forEach { it.addToRoute(res) }
            return res
        }
    }

    var name by RouteTable.name
    var checkpoints by Checkpoint via RouteCheckpointsTable
    var amountOfCheckpoint by RouteTable.checkpointAmount
    var type by RouteTable.type

    fun change(name: String, checkpoints: List<Checkpoint>) {
        this.name = name
        RouteCheckpointsTable.deleteWhere { RouteCheckpointsTable.route eq this@Route.id }
        checkpoints.forEachIndexed { index, checkpoint ->
            RouteCheckpointsTable.insert {
                it[this.route] = this@Route.id
                it[this.positionInRoute] = index
                it[this.checkpoint] = checkpoint.id
            }
        }
    }

    fun checkCorrectness(timestamps: List<Timestamp>): Boolean =
        when (type) {
            RouteType.FULL ->
                checkpoints.toList() == timestamps.map { it.checkpoint }
            RouteType.SELECTIVE ->
                checkpoints.toSet() == timestamps.map { it.checkpoint }.toSet()
        }

    override fun toLine(): List<Any?> {
        val amount = if (type == RouteType.FULL)
            null
        else
            amountOfCheckpoint
        return listOf(name, type.russian, amount) + checkpoints.map { it.name }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Route

        if (name != other.name) return false
        if (checkpoints.toSet() != other.checkpoints.toSet()) return false
        if (amountOfCheckpoint != other.amountOfCheckpoint) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + checkpoints.toSet().hashCode()
        result = 31 * result + amountOfCheckpoint
        result = 31 * result + type.hashCode()
        return result
    }
}

object RouteCheckpointsTable : IntIdTable("route_checkpoints") {
    val route: Column<EntityID<Int>> = reference("routes", RouteTable)
    val checkpoint: Column<EntityID<Int>> = reference("checkpoints", CheckpointTable)
    val positionInRoute = integer("position")
}

object CheckpointTable : IntIdTable("checkpoints") {
    val weight: Column<Int> = integer("weight")
    val name: Column<String> = varchar("name", MAX_TEXT_FIELD_SIZE)
}

class Checkpoint(id: EntityID<Int>) : IntEntity(id), SingleLineWritable {
    companion object : IntEntityClass<Checkpoint>(CheckpointTable) {
        fun create(name: String, weight: Int): Checkpoint =
            Checkpoint.new {
                this.name = name
                this.weight = weight
            }


        fun findByName(name: String): Checkpoint {
            return Checkpoint.find { CheckpointTable.name eq name }.let {
                if (it.empty()) throw IllegalStateException("No checkpoint with name $name")
                else it.first()
            }
        }

        fun checkByName(name: String): Boolean =
            !Checkpoint.find { CheckpointTable.name eq name }.empty()
    }

    var weight by CheckpointTable.weight
    var name by CheckpointTable.name
    var routes by Route via RouteCheckpointsTable

    fun addToRoute(route: Route) {
        val positionInRoute = route.checkpoints.toList().size
        RouteCheckpointsTable.insert {
            it[this.checkpoint] = this@Checkpoint.id
            it[this.route] = route.id
            it[this.positionInRoute] = positionInRoute
        }
    }

     fun change(name: String, weight: Int) {
         this.name = name
         this.weight = weight
     }

    override fun toLine(): List<Any?> =
        listOf(name, weight)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Checkpoint

        if (weight != other.weight) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = weight
        result = 31 * result + name.hashCode()
        return result
    }
}