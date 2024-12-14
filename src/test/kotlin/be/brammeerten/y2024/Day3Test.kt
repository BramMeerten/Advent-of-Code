package be.brammeerten.y2024

import be.brammeerten.readAllText
import be.brammeerten.readFile
import be.brammeerten.readSingleLine
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.math.abs


class Day3Test {

    @Test
    fun `part 1`() {
        val text = readAllText("2024/day3/exampleInput.txt")
        val sum = Regex("mul\\((\\d+),(\\d+)\\)")
            .findAll(text)
            .map { it.groupValues[1].toInt() * it.groupValues[2].toInt() }
            .sum()

        assertThat(sum).isEqualTo(161);
//        assertThat(total).isEqualTo(184122457);
    }

    @Test
    fun `part 2`() {
        val text = readAllText("2024/day3/exampleInput2.txt")
        val matches = Regex("(?:mul\\((\\d+),(\\d+)\\))|(?:don't\\(\\))|(?:do\\(\\))")
            .findAll(text)

        var sum = 0;
        var enabled = true
        matches.forEach {
            if (it.groupValues[0] == "don't()") {
                enabled = false
            } else if (it.groupValues[0] == "do()") {
                enabled = true;
            } else if (enabled) {
                sum += it.groupValues[1].toInt() * it.groupValues[2].toInt()
            }
        }

        assertThat(sum).isEqualTo(48);
//        assertThat(total).isEqualTo(107862689);
    }
}
