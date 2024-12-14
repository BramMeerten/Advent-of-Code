package be.brammeerten.y2024

import be.brammeerten.Co
import be.brammeerten.Co.Companion.DOWN
import be.brammeerten.Co.Companion.LEFT
import be.brammeerten.Co.Companion.RIGHT
import be.brammeerten.Co.Companion.UP
import be.brammeerten.readFile
import be.brammeerten.readFileAndSplitLines
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class Day7Test {

    private val OPERATORS: Set<(a: Long, b: Int) -> Long> = setOf({ a, b -> a + b }, { a, b -> a * b })
    private val OPERATORS2: Set<(a: Long, b: Int) -> Long> = setOf(
        { a, b -> a + b },
        { a, b -> a * b },
        { a, b -> (a.toString() + b.toString()).toLong()}
    )

    @Test
    fun `part 1`() {
        val sum = readFile("2024/day7/exampleInput.txt")
            .filter { it.isNotEmpty() }
            .map {
                val split = it.split(":")
                val total = split[0].toLong()
                val numbers = split[1].trim().split(" ").map { v -> v.toInt() }
                Line(total, numbers)
            }
            .filter { isValid(it, OPERATORS) }
            .sumOf { it.total }

        assertThat(sum).isEqualTo(3749);
//        assertThat(sum).isEqualTo(3351424677624L);
    }

    @Test
    fun `part 2`() {
        val sum = readFile("2024/day7/input.txt")
            .filter { it.isNotEmpty() }
            .map {
                val split = it.split(":")
                val total = split[0].toLong()
                val numbers = split[1].trim().split(" ").map { v -> v.toInt() }
                Line(total, numbers)
            }
            .filter { isValid(it, OPERATORS2) }
            .sumOf { it.total }

        assertThat(sum).isEqualTo(11387);
//        assertThat(sum).isEqualTo(204976636995111L);
    }

    private fun isValid(line: Line, operators: Set<(a: Long, b: Int) -> Long>): Boolean {
        return isValid(line.total, line.numbers[0].toLong(), line.numbers.subList(1, line.numbers.size), operators)
    }

    private fun isValid(targetTotal: Long, currentTotal: Long, remainingNumbers: List<Int>, operators: Set<(a: Long, b: Int) -> Long>): Boolean {
        if (currentTotal > targetTotal) {
            return false
        } else if (currentTotal == targetTotal && remainingNumbers.isEmpty()) {
            return true
        } else if (remainingNumbers.isEmpty()) {
            return false
        }

        operators.forEach {
            if (isValid(targetTotal, it.invoke(currentTotal, remainingNumbers[0]), remainingNumbers.subList(1, remainingNumbers.size), operators)) {
                return true
            }
        }
        return false;
    }

    private data class Line (val total: Long, val numbers: List<Int>) {}
}
