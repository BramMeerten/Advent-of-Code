package be.brammeerten.y2023

import be.brammeerten.Co
import be.brammeerten.extractRegexGroups
import be.brammeerten.readFile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.regex.Pattern
import kotlin.math.max
import kotlin.streams.toList


class Day3Test {

    @Test
    fun `part 1`() {
        val inputLines = readFile("2023/day3/input.txt")
        val lines = inputLines
            .map { line ->
                val numbers = mutableListOf<Pair<Int, String>>()
                for (i in line.indices) {
                    if (line[i].isDigit()) {
                        if (numbers.isNotEmpty() && (numbers.last().first + numbers.last().second.length) == i) {
                            numbers[numbers.size-1] = numbers.last().first to numbers.last().second + line[i]
                        } else {
                            numbers.add(i to line[i] + "")
                        }
                    }
                }
                numbers
            }

        var sum = 0
        for (y in lines.indices) {
            for (number in lines[y]) {
                var found = false
                for (i in 0 until number.second.length) {
                    if (isSurrounded(inputLines, y, number.first+i)) {
                        found = true
                    }
                }
                if (found) sum += Integer.parseInt(number.second)
            }
        }

        assertThat(sum).isEqualTo(4361);
//         assertThat(sum).isEqualTo(2169);
    }

    @Test
    fun `part 2`() {
        val inputLines = readFile("2023/day3/input.txt")
        val lines = inputLines
            .map { line ->
                val numbers = mutableListOf<Pair<Int, String>>()
                for (i in line.indices) {
                    if (line[i].isDigit()) {
                        if (numbers.isNotEmpty() && (numbers.last().first + numbers.last().second.length) == i) {
                            numbers[numbers.size-1] = numbers.last().first to numbers.last().second + line[i]
                        } else {
                            numbers.add(i to line[i] + "")
                        }
                    }
                }
                numbers
            }

        var sum = 0
        for (y in inputLines.indices) {
            for (x in 0 until inputLines[y].length) {
                if (inputLines[y][x] == '*') {
                    val nums = getNumbersSurrounding(y, x, lines)
                    if (nums.size == 2) {
                        sum += (nums[0] * nums[1])
                    }
                }
            }
        }

        assertThat(sum).isEqualTo(467835);
//         assertThat(sum).isEqualTo(2169);
    }

    private fun getNumbersSurrounding(y: Int, x: Int, lines: List<List<Pair<Int, String>>>): List<Int> {
        val sum = mutableListOf<Int>()
        if (y-1 >= 0) {
            sum.addAll(lines[y - 1].filter { line ->
                val start = line.first
                val end = line.first + line.second.length-1
                val result  = (x in start..end || (x-1) in start..end || (x+1) in start..end)
                result
            }.map { Integer.parseInt(it.second) })
        }

        sum.addAll(lines[y].filter { line ->
            val start = line.first
            val end = line.first + line.second.length-1
            val result  = (x in start..end || (x-1) in start..end || (x+1) in start..end)
            result
        }.map { Integer.parseInt(it.second) })

        sum.addAll(lines[y + 1].filter { line ->
            val start = line.first
            val end = line.first + line.second.length-1
            val result  = (x in start..end || (x-1) in start..end || (x+1) in start..end)
            result
        }.map { Integer.parseInt(it.second) })

        return sum
    }

    private fun isSurrounded(lines: List<String>, row: Int, col: Int): Boolean {
        val cos: List<Co> = listOf(Co(-1, -1), Co.UP, Co(-1, 1), Co.LEFT, Co(0, 0), Co.RIGHT, Co(1, -1), Co.DOWN, Co(1, 1))
        for (co in cos) {
            val y = co.row + row
            val x = co.col + col
            if (y >= 0 && y < lines.size) {
                if (x >= 0 && x < lines[y].length) {
                    val char = lines[y][x]
                    if (!char.isDigit() && char != '.') {
                        return true
                    }
                }
            }
        }
        return false;
    }

}