package be.brammeerten.y2023

import be.brammeerten.extractRegexGroups
import be.brammeerten.readFile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.regex.Pattern
import kotlin.math.max


class Day2Test {

    @Test
    fun `part 1`() {
        val lines = readFile("2023/day2/exampleInput.txt");
        val sum = lines
            .map { line ->
                val matcher = extractRegexGroups("^Game ([0-9]+): (.+)$", line)
                val games = matcher[1].split("; ")
                (matcher[0] to extractGames(games))
            }
            .filter { games ->
                games.second.none {
                    it["red"]!! > 12 || it["green"]!! > 13 || it["blue"]!! > 14
                }
            }
            .sumOf { Integer.parseInt(it.first) }

        assertThat(sum).isEqualTo(8);
//         assertThat(sum).isEqualTo(2169);
    }

    @Test
    fun `part 2`() {
        val lines = readFile("2023/day2/exampleInput.txt");
        val sum = lines
            .map { line ->
                val matcher = extractRegexGroups("^Game ([0-9]+): (.+)$", line)
                val games = matcher[1].split("; ")
                (matcher[0] to extractGames(games))
            }
            .map { games ->
                val acc = games.second.fold(games.second[0]) { acc, game ->
                    return acc.keys.forEach {
                        acc[it] = max(acc[it]!!, game[it]!!)
                    }
                }
                acc.values.reduce { acc, elem -> acc * elem }
            }
            .sumOf { it }

        assertThat(sum).isEqualTo(2286);
//        assertThat(sum).isEqualTo(60948);
    }

    private fun extractGames(games: List<String>): List<MutableMap<String, Int>> {
        return games.map {
            val dice = mutableMapOf("red" to 0, "green" to 0, "blue" to 0);
            it.split(", ").forEach { g ->
                val s = g.split(" ")
                dice[s[1]] = Integer.parseInt(s[0])
            }
            dice
        }
    }
}