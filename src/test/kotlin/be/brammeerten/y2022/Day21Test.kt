package be.brammeerten.y2022

import be.brammeerten.extractRegexGroups
import be.brammeerten.readFile
import be.brammeerten.y2022.Day21Test.MonkeyOperation.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day21Test {

    @Test
    fun `part 1a`() {
        val monkeys = readInput("2022/day21/exampleInput.txt")
        assertThat(getSolutions(monkeys)["root"]).isEqualTo(152L)
    }

    @Test
    fun `part 1b`() {
        val monkeys = readInput("2022/day21/input.txt")
        assertThat(getSolutions(monkeys)["root"]).isEqualTo(364367103397416)
    }

    @Test
    fun `part 2a`() {
        val monkeys = readInput("2022/day21/exampleInput.txt").filter { it.name != "humn" }
        assertThat(findHumanValue(monkeys)).isEqualTo(301)
    }

    @Test
    fun `part 2b`() {
        val monkeys = readInput("2022/day21/input.txt").filter { it.name != "humn" }
        assertThat(findHumanValue(monkeys)).isEqualTo(3782852515583)
    }

    fun getSolutions(monkeys: List<Monkey>): Map<String, Long> {
        val solutions = monkeys
            .filter { it.operation == ABSOLUTE }
            .map { it.name to it.arg1!!.toLong() }
            .toMap().toMutableMap()
        val unsolved = monkeys
            .filter { it.operation != ABSOLUTE }
            .map { it.name to it }
            .toMap().toMutableMap()

        while (solutions["root"] == null) {
            val initialSize = solutions.size
            unsolved.values
                .filter { m -> solutions.containsKey(m.arg1) && solutions.containsKey(m.arg2) }
                .forEach { m ->
                    val solution = m.solve(solutions[m.arg1]!!, solutions[m.arg2]!!)
                    solutions[m.name] = solution
                    unsolved.remove(m.name)
                }

            if (initialSize == solutions.size) break
        }
        return solutions
    }

    fun findHumanValue(monkeys: List<Monkey>): Long {
        val solutions = getSolutions(monkeys)
        val unsolved = monkeys.filter { !solutions.containsKey(it.name) }.map { it.name to it }.toMap()
        val root = monkeys.find { it.name == "root" }!!

        if (solutions.containsKey(root.arg1)) {
            return findHumanValue(solutions, unsolved, root.arg2!!, solutions[root.arg1]!!)
        } else if (solutions.containsKey(root.arg2)) {
            return findHumanValue(solutions, unsolved, root.arg1!!, solutions[root.arg2]!!)
        } else {
            throw IllegalStateException("Can't solve this")
        }
    }

    fun findHumanValue(solutions: Map<String, Long>, unsolved: Map<String, Monkey>, monkeyName: String, monkeyValue: Long): Long {
        var next = unsolved[monkeyName]!!.whatToSolve(monkeyValue, solutions)
        while(next.first != "humn") {
            next = unsolved[next.first]!!.whatToSolve(next.second, solutions)
        }

        return next.second
    }

    fun readInput(file: String): List<Monkey> {
        return readFile(file)
            .map { extractRegexGroups("(.+): (.+)", it) }
            .map { (name, operation) ->
                val toMonkey = {type: MonkeyOperation ->
                    val matches = extractRegexGroups("^(.+) [\\/\\*\\+\\-] (.+)$", operation)
                    Monkey(name, type, matches[0], matches[1])
                }
                if (operation.contains(" / ")) toMonkey(DIVIDE)
                else if (operation.contains(" * ")) toMonkey(MULTIPLY)
                else if (operation.contains(" + ")) toMonkey(ADD)
                else if (operation.contains(" - ")) toMonkey(SUBTRACT)
                else Monkey(name, ABSOLUTE, operation)
            }
    }

    data class Monkey(val name: String, val operation: MonkeyOperation, val arg1: String? = null, val arg2: String? = null) {

        fun solve(a1: Long, a2: Long): Long {
            return when (operation) {
                DIVIDE -> a1 / a2
                MULTIPLY -> a1 * a2
                ADD -> a1 + a2
                SUBTRACT -> a1 - a2
                else -> throw IllegalStateException("NO!")
            }
        }

        fun whatToSolve(expectedValue: Long, solutions: Map<String, Long>): Pair<String, Long> {
            if (solutions.containsKey(arg1)) {
                val a = solutions[arg1]!!
                return when (operation) {
                    DIVIDE -> arg2!! to a / expectedValue
                    MULTIPLY -> arg2!! to expectedValue / a
                    ADD -> arg2!! to expectedValue - a
                    SUBTRACT -> arg2!! to a - expectedValue
                    else -> throw IllegalStateException("OH")
                }
            } else if (solutions.containsKey(arg2)) {
                val b = solutions[arg2]!!
                return when (operation) {
                    DIVIDE -> arg1!! to b * expectedValue
                    MULTIPLY -> arg1!! to expectedValue / b
                    ADD -> arg1!! to expectedValue - b
                    SUBTRACT -> arg1!! to b + expectedValue
                    else -> throw IllegalStateException("OHo")
                }
            } else {
                throw IllegalStateException("SHIT")
            }
        }
    }

    enum class MonkeyOperation {
        ABSOLUTE, ADD, SUBTRACT, MULTIPLY, DIVIDE
    }

}
