package be.brammeerten.y2022

import be.brammeerten.readSingleLine
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class Day6Test {

    @Test
    fun `part 1`() {
        assertEquals(5, solvePt1("2022/day6/exampleInput1.txt"))
        assertEquals(6, solvePt1("2022/day6/exampleInput2.txt"))
        assertEquals(10, solvePt1("2022/day6/exampleInput3.txt"))
        assertEquals(11, solvePt1("2022/day6/exampleInput4.txt"))
    }

    @Test
    fun `part 2`() {
        assertEquals(23, solvePt2("2022/day6/exampleInput1.txt"))
        assertEquals(23, solvePt2("2022/day6/exampleInput2.txt"))
        assertEquals(29, solvePt2("2022/day6/exampleInput3.txt"))
        assertEquals(26, solvePt2("2022/day6/exampleInput4.txt"))
    }

    private fun solvePt1(file: String): Int {
        return solve(file, 4)
    }

    private fun solvePt2(file: String): Int {
        return solve(file, 14)
    }

    private fun solve(file: String, uniqueLength: Int): Int {
        val line = readSingleLine(file)
        val unique = line
            .windowed(uniqueLength)
            .first { sub -> sub.toCharArray().toList().toSet().size == uniqueLength }
        return line.indexOf(unique) + uniqueLength
    }
}