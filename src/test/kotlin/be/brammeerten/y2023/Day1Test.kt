package be.brammeerten.y2023

import be.brammeerten.readFile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class Day1Test {

    @Test
    fun `part 1`() {
        val lines = readFile("2023/day1/exampleInput.txt");
        var l2 = lines.map { line ->
            String(line.toCharArray().filter { char ->
                var ok = true
                try {
                    Integer.parseInt(char + "");
                } catch (e: Exception) {
                    ok = false
                }
                ok
            }.toCharArray())
        }.map { line ->
            Integer.parseInt(line.first() + "" + line.last())
        }.sum()

        assertThat(l2).isEqualTo(142);
//        assertThat(l2).isEqualTo(55130);
    }

    @Test
    fun `part 2`() {
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
        println(map)
        val lines = readFile("2023/day1/exampleInput2.txt");
        var l2 = lines
            .map { line ->
                var first = -1
                for (i in line.indices) {
                    val sub = line.substring(i)
                    for (key in map.keys) {
                        if (first == -1 && sub.startsWith(key)) {
                            first = map[key]!!
                            break;
                        }
                    }

                    if (first != -1) break;
                    try {
                        first = Integer.parseInt(sub[0] + "")
                    } catch (e: Exception) {}

                    if (first != -1) break;
                }

                var last = -1
                for (i in line.indices) {
                    val sub = line.substring(0, line.length-i)
                    for (key in map.keys) {
                        if (last == -1 && sub.endsWith(key)) {
                            last = map[key]!!
                            break;
                        }
                    }

                    if (last != -1) break;
                    try {
                        last = Integer.parseInt(sub.last() + "")
                    } catch (e: Exception) {}

                    if (last != -1) break;
                }

                (first*10) + last;
            }.sum()

        assertThat(l2).isEqualTo(281);
//        assertThat(l2).isEqualTo(54985);
    }


}