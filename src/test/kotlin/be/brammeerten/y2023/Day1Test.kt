package be.brammeerten.y2023

import be.brammeerten.readFile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class Day1Test {

    @Test
    fun `part 1`() {
        val lines = readFile("2023/day1/exampleInput.txt");
        val sum = lines
            .map { line -> line.toCharArray().filter { it.isDigit() } }
            .sumOf { Integer.parseInt(it.first() + "" + it.last()) }

        assertThat(sum).isEqualTo(142);
        // assertThat(sum).isEqualTo(55130);
    }

    @Test
    fun `part 2`() {
        val lines = readFile("2023/day1/exampleInput2.txt");
        val map: Map<String, Int> = mapOf(
            "one" to 1,
            "two" to 2,
            "three" to 3,
            "four" to 4,
            "five" to 5,
            "six" to 6,
            "seven" to 7,
            "eight" to 8,
            "nine" to 9,
        )

        val sum = lines.sumOf { line ->
            var first: Int? = null
            for (i in line.indices) {
                val sub = line.substring(i)
                first = map.entries
                    .find { sub.startsWith(it.key) }
                    ?.value
                    ?: if (sub[0].isDigit()) Integer.parseInt(sub[0] + "") else null;

                if (first != null) break;
            }

            var last: Int? = null
            for (i in line.indices) {
                val sub = line.substring(0, line.length - i)
                last = map.entries
                    .find { sub.endsWith(it.key) }
                    ?.value
                    ?: if (sub.last().isDigit()) Integer.parseInt(sub.last() + "") else null;

                if (last != null) break;
            }

            (first!! * 10) + last!!;
        }

        assertThat(sum).isEqualTo(281);
//        assertThat(sum).isEqualTo(54985);
    }


}