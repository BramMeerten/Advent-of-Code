package be.swsb.coderetreat

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import readFile


class Day3Test {

    @Test
    fun `part 1`() {
        val result = readFile("day3/exampleInput.txt")
                .map { l -> l.substring(0, l.length / 2) to l.substring(l.length / 2) }
                .flatMap { (c1, c2) -> c1.toCharArray().filter { c2.contains(it) }.toSet() }
                .sumOf { toScore(it) }

        assertEquals(157, result)
    }

    @Test
    fun `part 2`() {
        val result = readFile("day3/exampleInput.txt")
                .windowed(3, 3)
                .flatMap {(r1, r2, r3) -> r1.toCharArray().filter{r2.contains(it) && r3.contains(it)}.toSet() }
                .sumOf { toScore(it) }

        assertEquals(70, result)
    }

    private fun toScore(char: Char): Int {
        if (char in 'a'..'z')
            return char.toByte().toInt() - 'a'.toByte().toInt() + 1
        else
            return char.toByte().toInt() - 'A'.toByte().toInt() + 27
    }
}