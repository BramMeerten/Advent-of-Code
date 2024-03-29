package be.brammeerten.y2022

import be.brammeerten.readFile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class Day1Test {

    @Test
    fun `part 1`() {
        val lines = readFile("2022/day1/exampleInput.txt");
        val result = lines
                .fold(mutableListOf(mutableListOf<String>())) { acc, value ->
                    if (value.isBlank()) acc += mutableListOf<String>() else acc.last() += value
                    acc
                }
                .maxOfOrNull { elf -> elf.sumOf(String::toInt) } ?: 0
        assertThat(result).isEqualTo(24000)
    }

    @Test
    fun `part 2`() {
        val lines = readFile("2022/day1/exampleInput.txt");
        val result = lines
                .fold(mutableListOf(mutableListOf<String>())) { acc, value ->
                    if (value.isBlank()) acc += mutableListOf<String>() else acc.last() += value
                    acc
                }
                .map { elf -> elf.sumOf { value -> value.toInt() } }
                .sortedDescending()
                .take(3)
                .sum()
        assertThat(result).isEqualTo(45000)
    }


}