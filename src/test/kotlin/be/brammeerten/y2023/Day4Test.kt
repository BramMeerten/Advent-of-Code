package be.brammeerten.y2023

import be.brammeerten.extractRegexGroups
import be.brammeerten.readFile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.math.pow


class Day4Test {

    @Test
    fun `part 1`() {
        val inputLines = readFile("2023/day4/exampleInput.txt")
        val sum = inputLines
            .map { line ->
                val split = extractRegexGroups("^Card\\s+[0-9]+: (.+) \\| (.+)$", line)
                (split[0].split(' ').filter { it.isNotBlank() } to split[1].split(' ').filter { it.isNotBlank() })
            }
            .map { (winners, my) ->
                my.count { winners.contains(it) }
            }
            .sumOf { Math.max(0, (2.0).pow((it - 1).toDouble()).toInt()) }
        println(sum)

        assertThat(sum).isEqualTo(13);
//         assertThat(sum).isEqualTo(26218);
    }

    @Test
    fun `part 2`() {
        val inputLines = readFile("2023/day4/exampleInput.txt")
        val cards = inputLines
            .map { line ->
                val split = extractRegexGroups("^Card\\s+[0-9]+: (.+) \\| (.+)$", line)
                (split[0].split(' ').filter { it.isNotBlank() } to split[1].split(' ').filter { it.isNotBlank() })
            }
            .map { (winners, my) -> my.count { winners.contains(it) } }
            .map { (1 to it)}
            .toMutableList()

        for (i in cards.indices) {
            for (n in 0 until cards[i].first) {
                for (j in 1..cards[i].second) {
                    cards[i + j] = cards[i + j].first + 1 to cards[i + j].second
                }
            }
        }

        val sum = cards.sumOf { it.first }

        assertThat(sum).isEqualTo(30);
//         assertThat(sum).isEqualTo(9997537);
    }

}