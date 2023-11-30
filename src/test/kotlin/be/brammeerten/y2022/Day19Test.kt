package be.brammeerten.y2022

import be.brammeerten.extractRegexGroups
import be.brammeerten.extractRegexGroupsI
import be.brammeerten.readFile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.math.max

val POTENTIAL_EXTRA_GEODES_PER_MINUTE_LEFT = createSequence(listOf(0), 0, 32)
fun createSequence(total: List<Int>, robots: Int, timeLeft: Int): List<Int> {
    if (timeLeft == 0) return total
    val newTotal = (total.lastOrNull() ?: 0) + robots
    return createSequence(total + newTotal, robots+1, timeLeft-1)
}

class Day19Test {

    @Test
    fun `part 1a`() {
        val bluePrints = readBlueprints("2022/day19/exampleInput.txt")
        assertThat(getTotalQualityLevel(bluePrints)).isEqualTo(33)
    }

    @Test
    fun `part 1b`() {
        val bluePrints = readBlueprints("2022/day19/input.txt")
        assertThat(getTotalQualityLevel(bluePrints)).isEqualTo(600)
    }

    @Test
    fun `part 2`() {
        val bluePrints = readBlueprints("2022/day19/exampleInput.txt")
        assertThat(getGeodesMultiplied(bluePrints, minutes = 32)).isEqualTo(56 * 62)
    }

    @Test
    fun `part 2b`() {
        val bluePrints = readBlueprints("2022/day19/input.txt")
        assertThat(getGeodesMultiplied(bluePrints.subList(0, 3), minutes = 32)).isEqualTo(0)
    }

    fun getTotalQualityLevel(blueprints: List<Blueprint>): Int {
        return blueprints
            .sumOf {
                val bestState = it.getMaxGeodes()
                println("Blueprint ${it.id}: ${bestState.geode} geodes")
                bestState.geode * it.id
            }
    }

    fun getGeodesMultiplied(blueprints: List<Blueprint>, minutes: Int): Int {
        return blueprints
            .map {
                val bestState = it.getMaxGeodes(minutes)
                println("Blueprint ${it.id}: ${bestState.geode} geodes")
                bestState.geode
            }.reduce { acc, g -> acc * g }
    }

    fun readBlueprints(file: String): List<Blueprint> {
        return readFile(file)
            .map { blueprint ->
                Blueprint(
                    extractRegexGroupsI("Blueprint (\\d+):.*", blueprint)[0],
                    readRobotCost("ore", blueprint),
                    readRobotCost("clay", blueprint),
                    readRobotCost("obsidian", blueprint),
                    readRobotCost("geode", blueprint)
                )
            }
    }

    fun readRobotCost(type: String, line: String): RobotCost {
        val regex = "Each $type robot costs ([^.]+)."
        return RobotCost(
            extractRegexGroups(regex, line)[0].split(" and ")
            .map { cost -> extractRegexGroups("(\\d+) (.+)", cost) }
            .map { it[1] to it[0].toInt() }.toMap())
    }

    data class Blueprint(
        val id: Int,
        val oreRobotCost: RobotCost,
        val clayRobotCost: RobotCost,
        val obsidianRobotCost: RobotCost,
        val geodeRobotCost: RobotCost
    ) {
        val robotCosts = mapOf("ore" to oreRobotCost, "clay" to clayRobotCost, "obsidian" to obsidianRobotCost, "geode" to geodeRobotCost)

        fun getMaxGeodes(minutes: Int = 24): State {
            val start = State(minute = 0, 0, 0, 0, 0, oreRobots = 1, 0, 0, 0, null, /*emptyList()*/)
            return sim(start, minutes, 0)
        }

        fun sim(state: State, minutes: Int, curMax: Int): State {
            if (state.minute >= minutes)
                return state

            var result = state
            var currentMax = curMax
            for (option in getOptions(state)) {
                if (getPotential(option, minutes) < currentMax) continue
                val simulation = sim(option, minutes, currentMax)

                result = if (simulation.geode > result.geode) simulation else result
                currentMax = max(result.geode, currentMax)
            }

            return result
        }

        fun getPotential(state: State, minutes: Int): Int {
            val potential = state.geode + (state.geodeRobots * (minutes - state.minute))
            return potential + POTENTIAL_EXTRA_GEODES_PER_MINUTE_LEFT[minutes - state.minute]
        }

        fun getOptions(state: State): List<State> {
            val canAfford: Map<String, RobotCost> = this.robotCosts
                .filter { it.value.canAfford(state) }

            // Koop of spaar als nog iets beschikbaar kan komen
            // Koop niet wat al vorige ronde gekocht had kunnen worden (tenzij er iets anders gekocht is vorige ronde)
            if (canAfford.isEmpty()) {
                return listOf(state.advance())

            } else {
                val options = canAfford
                    .filter { !it.value.couldAffordInPrevRound(state) || state.hasNewBots() }
                    .map { state.advance().buyRobot(it.key, it.value)}

                if (canAfford.size < this.robotCosts.size)
                    return options + state.advance()
                else if (options.isEmpty())
                    throw IllegalStateException("Zou nooit mogen")
                else
                    return options
            }
        }
    }

    data class RobotCost(val costs: Map<String, Int>) {
        fun canAfford(state: State): Boolean {
            return costs.all { cost -> cost.value <= state.get(cost.key) }
        }

        fun couldAffordInPrevRound(state: State): Boolean {
            return if (state.oldState == null) false else canAfford(state.oldState)
        }
    }

    data class State(val minute: Int,
                     val ores: Int, val clay: Int, val obsidian: Int, val geode: Int,
                     val oreRobots: Int, val clayRobots: Int, val obsidianRobots: Int, val geodeRobots: Int,
                     val oldState: State? = null) {
        fun get(resource: String): Int {
            return when (resource) {
                "ore" -> ores
                "clay" -> clay
                "obsidian" -> obsidian
                "geode" -> geode
                else -> throw IllegalStateException("Unknown resource $resource")
            }
        }

        fun advance(): State {
            return State(
                minute+1,
                ores+oreRobots, clay+clayRobots, obsidian+obsidianRobots, geode+geodeRobots,
                oreRobots, clayRobots, obsidianRobots, geodeRobots,
                this
            )
        }

        override fun toString(): String {
            return "Minute ${minute}: ore($oreRobots, $ores), clay($clayRobots, $clay), obsid(${obsidianRobots}, $obsidian), geode($geodeRobots, $geode), "
        }

        fun hasNewBots(): Boolean {
            if (oldState == null)
                return clayRobots > 0 || oreRobots > 0 || obsidianRobots > 0 || geodeRobots > 0
            return oldState.oreRobots != oreRobots
                    || oldState.clayRobots != clayRobots
                    || oldState.obsidianRobots != obsidianRobots
                    || oldState.geodeRobots != geodeRobots
        }

        fun buyRobot(robot: String, cost: RobotCost): State {
            return State(
                minute,
                ores - (cost.costs["ore"] ?: 0),
                clay - (cost.costs["clay"] ?: 0),
                obsidian - (cost.costs["obsidian"] ?: 0),
                geode - (cost.costs["geode"] ?: 0),
                oreRobots + (if (robot == "ore") 1 else 0),
                clayRobots + (if (robot == "clay") 1 else 0),
                obsidianRobots + (if (robot == "obsidian") 1 else 0),
                geodeRobots + (if (robot == "geode") 1 else 0),
                oldState,
            )
        }
    }
}
