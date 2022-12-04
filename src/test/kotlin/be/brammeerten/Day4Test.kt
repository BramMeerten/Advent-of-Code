package be.brammeerten

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import be.brammeerten.readFile


class Day4Test {

    @Test
    fun `part 1`() {
        val result = readFile("day4/exampleInput.txt")
                .map { l -> l.split(",") }
                .map { pair -> pair
                        .map { it.split("-") }
                        .map { (a, b) -> a.toInt() to b.toInt() } }
                .count { (a, b) -> a.first >= b.first && a.second <= b.second || b.first >= a.first && b.second <= a.second }

        assertEquals(2, result)
    }

    @Test
    fun `part 2`() {
        val result = readFile("day4/exampleInput.txt")
                .map { l -> l.split(",") }
                .map { pair -> pair
                        .map { it.split("-") }
                        .map { (a, b) -> a.toInt() to b.toInt() } }
                .count { (a, b) -> a.first >= b.first && a.first <= b.second || b.first >= a.first && b.first <= a.second }

        assertEquals(4, result)
    }
}