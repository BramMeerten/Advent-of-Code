package be.swsb.coderetreat

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import readFile


class Day1Test {

    @Test
    fun `part 1`() {
        val lines = readFile("day1/exampleInput.txt");
        val result = lines
                .fold(mutableListOf(mutableListOf<String>())) { acc, value ->
                    if (value.isBlank()) acc.add(mutableListOf()) else acc.last().add(value)
                    acc
                }
                .map { elf -> elf
                        .map{value -> value.toInt()}
                        .reduce { a, b -> a + b } }
                .maxOrNull() ?: 0
        assertThat(result).isEqualTo(24000)
    }

    @Test
    fun `part 2`() {
        val lines = readFile("day1/exampleInput.txt");
        val result = lines
                .fold(mutableListOf(mutableListOf<String>())) { acc, value ->
                    if (value.isBlank()) acc.add(mutableListOf()) else acc.last().add(value)
                    acc
                }
                .map { elf -> elf
                        .map{value -> value.toInt()}
                        .reduce { a, b -> a + b } }
                .sortedDescending()
                .subList(0, 3)
                .reduce{a, b -> a + b}
        assertThat(result).isEqualTo(45000)
    }


}