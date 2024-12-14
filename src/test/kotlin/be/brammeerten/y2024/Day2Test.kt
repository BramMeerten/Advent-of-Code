package be.brammeerten.y2024

import be.brammeerten.readFile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.math.abs


class Day2Test {

    @Test
    fun `part 1`() {
        val reports = readFile("2024/day2/exampleInput.txt")
            .map { it.split(" ").map { v -> v.toInt() } }
            .filter { isValidReport(it) }

        assertThat(reports.size).isEqualTo(2);
//        assertThat(total).isEqualTo(479);
    }

    @Test
    fun `part 2`() {
        val reports = readFile("2024/day2/exampleInput.txt")
            .map { it.split(" ").map { v -> v.toInt() } }
        val reports2 = reports.filter {
            val range = 0..it.size

            for (i in range) {
                if (i == 0) {
                    if (isValidReport(it)) {
                        return@filter true
                    }
                } else {
                    val copy = it.toMutableList();
                    copy.removeAt(i - 1);
                    if (isValidReport(copy)) {
                        return@filter true
                    }
                }
            }
            false
        }

        assertThat(reports2.size).isEqualTo(4);
//        assertThat(total).isEqualTo(531);
    }

    private fun isValidReport(report: List<Int>): Boolean {
        val diffs = report
            .windowed(2).map { pair -> pair[0] - pair[1] }

        val sameDirection = diffs.all { it > 0 } || diffs.all { it < 0 }
        val inRange = diffs.all { abs(it) in 1..3 }
        return sameDirection && inRange
    }
}
