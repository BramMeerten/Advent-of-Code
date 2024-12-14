package be.brammeerten.y2024

import be.brammeerten.readFile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.math.abs


class Day1Test {

    @Test
    fun `part 1`() {
        val lines = readFile("2024/day1/exampleInput.txt");
        val (col1, col2) = readColumns(lines)
        col1.sort()
        col2.sort()
        val total = col1
            .zip(col2) { a, b -> abs(a.toInt() - b.toInt()) }
            .sum()

        assertThat(total).isEqualTo(11);
//        assertThat(total).isEqualTo(2367773);
    }

    @Test
    fun `part 2`() {
        val lines = readFile("2024/day1/exampleInput.txt");
        val (col1, col2) = readColumns(lines)
        val result = col1.sumOf { col2.count { v -> v == it } * it.toInt() }

        assertThat(result).isEqualTo(31);
//        assertThat(sum).isEqualTo(21271939);
    }

    private fun readColumns(lines: List<String>): Pair<MutableList<String>, MutableList<String>> {
        return lines.fold(Pair(mutableListOf(), mutableListOf())) { acc, s ->
            val split = s.split("   ");
            acc.first.add(split[0]);
            acc.second.add(split[1]);
            acc;
        }
    }


}
