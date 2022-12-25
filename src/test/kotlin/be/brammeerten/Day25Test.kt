package be.brammeerten

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day25Test {

    @Test
    fun `part 0`() {
        val numbers = mapOf(
            1 to Num("1"),
            2 to Num("2"),
            3 to Num("1="),
            4 to Num("1-"),
            5 to Num("10"),
            6 to Num("11"),
            7 to Num("12"),
            8 to Num("2="),
            9 to Num("2-"),
            10 to Num("20"),
            15 to Num("1=0"),
            20 to Num("1-0"),
            2022 to Num("1=11-2"),
            12345 to Num("1-0---0"),
            314159265 to Num("1121-1110-1=0"),
        )

        assertThat(numbers[1]!! + numbers[4]!!).isEqualTo(numbers[5])
        assertThat(numbers[4]!! + numbers[6]!!).isEqualTo(numbers[10])
        assertThat(numbers[3]!! + numbers[7]!!).isEqualTo(numbers[10])
        assertThat(numbers[10]!! + numbers[10]!!).isEqualTo(numbers[20])
        assertThat(numbers[15]!! + numbers[5]!!).isEqualTo(numbers[20])
    }

    @Test
    fun `part 1a`() {
        val result = readFile("day25/exampleInput.txt")
            .map { Num(it) }
            .reduce { a, b -> a + b }
        assertThat(result).isEqualTo(Num("2=-1=0"))
    }

    @Test
    fun `part 1b`() {
        val result = readFile("day25/input.txt")
            .map { Num(it) }
            .reduce { a, b -> a + b }
        assertThat(result).isEqualTo(Num("2=020-===0-1===2=020"))
    }

    data class Num(val number: String) {

        val decValues = mapOf('=' to -2, '-' to -1, '0' to 0, '1' to 1, '2' to 2)
        val solutions = mapOf(
            -5 to ('0' to -1),
            -4 to ('1' to -1),
            -3 to ('2' to -1),
            -2 to ('=' to 0),
            -1 to ('-' to 0),
            0 to ('0' to 0),
            1 to ('1' to 0),
            2 to ('2' to 0),
            3 to ('=' to 1),
            4 to ('-' to 1),
            5 to ('0' to 1)
        )

        operator fun plus(other: Num): Num {
            val x = number.padStart(other.number.length, '0')
            val y = other.number.padStart(number.length, '0')

            var solution = ""

            var remainder = 0
            for (i in x.indices.reversed()) {
                val xDec = decValues[x[i]]
                val yDec = decValues[y[i]]
                val sol = solutions[xDec!! + yDec!! + remainder]!!
                remainder = sol.second
                solution = sol.first + solution
            }

            if (remainder != 0) {
                val p = decValues.filter { it.value == remainder }.map { it.key }.first()
                solution = p + solution
            }

            return Num(solution)
        }
    }

}