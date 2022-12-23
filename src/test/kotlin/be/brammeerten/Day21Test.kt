package be.brammeerten

import be.brammeerten.Day21Test.MonkeyOperation.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.math.exp

class Day21Test {

    @Test
    fun `part 1a`() {
        val monkeys = readInput("day21/exampleInput.txt")
        assertThat(getRootMonkeyNumber(monkeys)).isEqualTo(152L)
    }

    @Test
    fun `part 1b`() {
        val monkeys = readInput("day21/input.txt")
        assertThat(getRootMonkeyNumber(monkeys)).isEqualTo(3L)
    }

    // sjmn=150, czwc = 99433652936583
    @Test
    fun `part 2a`() {
        val monkeys = readInput("day21/exampleInput.txt").filter { it.name != "humn" }.toMutableList()
        assertThat(solve(monkeys, "pppw", "150")).isEqualTo(301)
    }

    // sjmn=150, czwc = 99433652936583
    @Test
    fun `part 2b`() {
        val monkeys = readInput("day21/input.txt").filter { it.name != "humn" }.toMutableList()
        assertThat(solve(monkeys, "wvbw", "99433652936583")).isEqualTo(301)
    }

    fun getRootMonkeyNumber(monkeys: List<Monkey>): Long {
        val solutions = monkeys
            .filter { it.operation == ABSOLUTE }
            .map { it.name to it.arg1!!.toLong() }
            .toMap().toMutableMap()
        val unsolved = monkeys.filter { it.operation != ABSOLUTE }.toMutableList()

        var noodrem = 0
        while (solutions["root"] == null) {
            unsolved
                .filter { m -> solutions.containsKey(m.arg1) && solutions.containsKey(m.arg2) }
                .forEach { m ->
                    val solution = m.solve(solutions[m.arg1]!!, solutions[m.arg2]!!)
                    solutions.put(m.name, solution)
                    unsolved.remove(m)
                }


            noodrem++
            if (noodrem > 1000)
                throw IllegalStateException("DUURDE TE LANG, solutions: " + solutions.keys)
        }
        return solutions["root"]!!
    }

    fun whatToYell(monkeys: List<Monkey>, solveFor: String): Long {
        val solutions = monkeys
            .filter { it.operation == ABSOLUTE }
            .map { it.name to it.arg1!!.toLong() }
            .toMap().toMutableMap()
        solutions.remove("humn")
        val unsolved = monkeys.filter { it.operation != ABSOLUTE }.toMutableList()

        var noodrem = 0
        while (solutions[solveFor] == null) {
            unsolved
                .filter { m -> solutions.containsKey(m.arg1) && solutions.containsKey(m.arg2) }
                .forEach { m ->
                    val solution = m.solve(solutions[m.arg1]!!, solutions[m.arg2]!!)
                    solutions.put(m.name, solution)
                    unsolved.remove(m)
                }


            noodrem++
            if (noodrem > 1000)
                throw IllegalStateException("DUURDE TE LANG, solutions: " + solutions.keys)
        }
        return solutions[solveFor]!!
    }

    fun solve(monkeys: List<Monkey>, startName: String, value: String): Long {
        // Los zoveel mogelijk op
        val solutions = monkeys
            .filter { it.operation == ABSOLUTE }
            .map { it.name to it.arg1!!.toLong() }
            .toMap().toMutableMap()
        val unsolved = monkeys.filter { it.operation != ABSOLUTE }.map { it.name to it }.toMap().toMutableMap()

        while (true) {
            val initialSize = solutions.size
            unsolved.values
                .filter { m -> solutions.containsKey(m.arg1) && solutions.containsKey(m.arg2) }
                .forEach { m ->
                    val solution = m.solve(solutions[m.arg1]!!, solutions[m.arg2]!!)
                    solutions.put(m.name, solution)
                    unsolved.remove(m.name)
                }

            if (initialSize == solutions.size) break
        }

        var next = unsolved[startName]!!.whatToSolve(value.toLong(), solutions)
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
