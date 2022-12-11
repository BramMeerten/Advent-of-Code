package be.brammeerten

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class Day11Test {

    val ITEMS_REGEX = "^ {2}Starting items: ((-?\\d+, )*(-?\\d+)+)$"
    val OP_REGEX = "^ {2}Operation: new = (.+)$"
    val DIV_REGEX = "^ {2}Test: divisible by (-?\\d+)$"
    val TRUE_REGEX = "^ {4}If true: throw to monkey (\\d+)$"
    val FALSE_REGEX = "^ {4}If false: throw to monkey (\\d+)$"

    @Test
    fun `part 1`() {
        val monkeys = parseMonkeys("day11/input.txt")
        for (round in (1..20))
            doRound(monkeys, relieve = true)

        val counts = monkeys.map { it.count }.sortedDescending()
        Assertions.assertEquals(61503L, counts[0] * counts[1])
    }

    @Test
    fun `part 2`() {
        val monkeys = parseMonkeys("day11/input.txt")
        for (round in (1..10000))
            doRound(monkeys)

        monkeys.forEachIndexed { i, m -> println("Monkey $i: ${m.count}") }
        val counts = monkeys.map { it.count }.sortedDescending()

        Assertions.assertEquals(14081365540L, counts[0] * counts[1])
    }

    fun doRound(monkeys: List<Monkey>, relieve: Boolean = false) {
        val optimizeFactor = calcOptimizeFactor(monkeys)
        monkeys.forEachIndexed { i, monkey ->
            monkey.items.forEach { item ->
                // Inspect
                var worry = monkey.operation(item)
                monkey.count++

                // Optimize
                worry %= optimizeFactor

                // Relieve
                if (relieve) worry /= 3

                // Test and throw
                val newMonk = monkey.nextMonkey(worry)
                monkeys[newMonk].items.add(worry)
            }
            monkey.items.clear()
        }
    }

    fun parseMonkeys(fileName: String) = readFileSplitted(fileName, "\n\n").map { parseMonkey(it) }

    fun parseMonkey(lines: List<String>): Monkey {
        val items = extractRegexGroups(ITEMS_REGEX, lines[1])[0].split(", ").map { it.toLong() }
        val div = extractRegexGroups(DIV_REGEX, lines[3])[0].toInt()
        val trueMonkey = extractRegexGroups(TRUE_REGEX, lines[4])[0].toInt()
        val falseMonkey = extractRegexGroups(FALSE_REGEX, lines[5])[0].toInt()

        return Monkey(
            readOperation(lines[2]),
            div, trueMonkey, falseMonkey,
            items.toMutableList()
        )
    }

    fun readOperation(line: String): (Long) -> Long {
        val opString = extractRegexGroups(OP_REGEX, line)[0]
        return { old ->
            val ops = opString.replace("old", old.toString(), false).split(" ")
            val start = ops[0].toLong()
            ops.drop(1)
                .windowed(2, 2)
                .fold(start) { result, (op, num) ->
                    when (op) {
                        "+" -> result + num.toLong()
                        "*" -> result * num.toLong()
                        else -> throw IllegalStateException("Unknown operation: $op")
                    }
                }
        }
    }

    fun calcOptimizeFactor(monkeys: List<Monkey>) =
        monkeys.map { it.divByTest }.reduce{a, b -> a * b}

    class Monkey(
        val operation: (Long) -> Long,
        val divByTest: Int,
        private val monkeyDivTrue: Int,
        private val monkeyDivFalse: Int,
        val items: MutableList<Long>) {
        var count = 0L

        fun nextMonkey(item: Long): Int {
            return if (item % divByTest == 0L) monkeyDivTrue else monkeyDivFalse
        }
    }

}