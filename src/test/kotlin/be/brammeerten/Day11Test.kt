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
        println(monkeys[0].test(47))

        for (round in (1..20)) {
            doRound(monkeys)
//            println("After round $round, the monkeys are holding items with these worry levels:")
//            monkeys.forEachIndexed {i, m ->
//                println("Monkey $i: " + m.items.map { it.toString() }.joinToString (", "))
//            }
//            println()
        }
            val counts = monkeys.map { it.count }.sortedDescending()

        Assertions.assertEquals(10605, counts[0] * counts[1])
    }

    fun doRound(monkeys: List<Monkey>) {
        monkeys.forEachIndexed { i, monkey ->
            monkey.items.forEach{ item ->
                // Inspect
                var worry = monkey.operation(item)
                monkey.count++
//                println("worry after inspect: $worry")

                // Relieve
                worry /= 3
//                println("Relieve: $worry")

                // test and throw
                val newMonk = monkey.test(worry)
//                println("Throw to: $newMonk")
                if (newMonk == i) throw IllegalStateException("Monkey throws to itself")
                monkeys[newMonk].items.add(worry)
            }
            monkey.items.clear()
//            println("")
        }
    }

    fun parseMonkeys(fileName: String): List<Monkey> {
        return readFileSplitted(fileName, "\n\n")
            .map { parseMonkey(it) }
    }

    fun parseMonkey(lines: List<String>): Monkey {
        if (!(Regex("^Monkey \\d+:$").matches(lines[0]))) throw IllegalStateException("Expected different first line: ${lines[0]}")
        val items = extractRegexGroups(ITEMS_REGEX, lines[1])[0].split(", ").map { it.toInt() }
        val operation = readOperation(lines[2])
        val test = readTest(lines.drop(3))

        return Monkey (operation, test, items.toMutableList())
    }

    fun readOperation(line: String): (Int) -> Int {
        val opString = extractRegexGroups(OP_REGEX, line)[0]
        return { old ->
            val ops = opString.replace("old", ("" + old), false)
                .split(" ")
            val start = ops[0].toInt()
            ops.drop(1)
                .windowed(2, 2)
                .fold(start) { result, (op, num) ->
                    when (op) {
                        "+" -> result + num.toInt()
                        "*" -> result * num.toInt()
                        else -> throw IllegalStateException("Unknown operation: $op")
                    }
                }
        }
    }

    fun readTest(lines: List<String>): (Int) -> Int {
        val div =  extractRegexGroups(DIV_REGEX, lines[0])[0]
        val trueMonkey =  extractRegexGroups(TRUE_REGEX, lines[1])[0]
        val falseMonkey =  extractRegexGroups(FALSE_REGEX, lines[2])[0]
        return { item ->
            if (item % div.toInt() == 0) trueMonkey.toInt() else falseMonkey.toInt()
        }
    }

    class Monkey(
        val operation: (Int) -> Int,
        val test: (Int) -> Int,
        val items: MutableList<Int>) {

        var count = 0
    }

}