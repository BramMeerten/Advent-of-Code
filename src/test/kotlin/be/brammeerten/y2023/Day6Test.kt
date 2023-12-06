package be.brammeerten.y2023

import be.brammeerten.readFile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.regex.Pattern
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt


class Day6Test {

    @Test
    fun `part 1`() {
        val lines = readFile("2023/day6/exampleInput.txt");
        val times = doRegexI("([0-9]+)", lines[0])
        val distances = doRegexI("([0-9]+)", lines[1])
        val games = times.zip(distances)

        val result = games.map { game ->
            (1 until game.first).map { holdForSeconds ->
                (game.first - holdForSeconds) * holdForSeconds
            }.count { it > game.second }
        }.reduce { acc, elem -> acc * elem }

        assertThat(result).isEqualTo(288);
//         assertThat(result).isEqualTo(2065338);
    }

    @Test
    fun `part 2`() {
        val lines = readFile("2023/day6/exampleInput.txt");
        val time = doRegex("([0-9]+)", lines[0]).joinToString("").toLong()
        val dist = doRegex("([0-9]+)", lines[1]).joinToString("").toLong()

        // dist = (time - x) * x
        // -x^2 + time*x -dist = 0
        val d = ((time * time) - (-4 * -dist)).toDouble()
        val max = floor((-time - sqrt(d)) / -2).toLong()
        val min = ceil((-time + sqrt(d)) / -2).toLong()

        assertThat(max - min +1).isEqualTo(71503);
//        assertThat(max - min +1).isEqualTo(34934171);
    }


    fun doRegexI(regex: String, text: String): List<Int> {
        val r = Pattern.compile(regex);
        val matcher = r.matcher(text);
        val result = mutableListOf<Int>()

        while (matcher.find()) {
            result.add(matcher.group().toInt())
        }

        return result
    }

    fun doRegex(regex: String, text: String): List<String> {
        val r = Pattern.compile(regex);
        val matcher = r.matcher(text);
        val result = mutableListOf<String>()

        while (matcher.find()) {
            result.add(matcher.group())
        }

        return result
    }

}