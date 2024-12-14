package be.brammeerten.y2024

import be.brammeerten.Co
import be.brammeerten.Co.Companion.DOWN
import be.brammeerten.Co.Companion.LEFT
import be.brammeerten.Co.Companion.RIGHT
import be.brammeerten.Co.Companion.UP
import be.brammeerten.readFile
import be.brammeerten.readFileSplitted
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class Day5Test {

    @Test
    fun `part 1`() {
        val (rules, updates) = readFileSplitted("2024/day5/exampleInput.txt", "\n\n");
        val x = updates
            .filter { it.isNotEmpty() } // Intellij adds blank newline at end
            .map { it.split(",") }
            .filter { rules.all { rule -> matchesRule(it, rule) } }
            .sumOf { it[it.size / 2].toInt() }

        assertThat(x).isEqualTo(143);
//        assertThat(sum).isEqualTo(7074);
    }

    @Test
    fun `part 2`() {
        val (rules, updates) = readFileSplitted("2024/day5/exampleInput.txt", "\n\n");

        val rulesMap = HashMap<Int, MutableSet<Int>>()
        rules
            .map { it.split("|") }
            .map { Pair(it[0].toInt(), it[1].toInt()) }
            .forEach {
                val set = rulesMap.getOrDefault(it.first, mutableSetOf())
                set.add(it.second)
                rulesMap[it.first] = set
            }

         val x = updates
             .filter { it.isNotEmpty() } // Intellij adds blank newline at end
             .map { it.split(",") }
             .filter { !rules.all { rule -> matchesRule(it, rule) } }
             .map {
                 val fixed = it.map { v -> v.toInt() }.toMutableList()
                 fixed.sortWith { a, b ->
                     if (rulesMap.getOrDefault(a, mutableSetOf()).contains(b)) {
                         -1
                     } else if (rulesMap.getOrDefault(b, mutableSetOf()).contains(a)) {
                         1
                     } else {
                         0
                     }
                 }
                 fixed
             }
             .sumOf { it[it.size / 2] }

         assertThat(x).isEqualTo(123);
//        assertThat(sum).isEqualTo(4828);
    }

    private fun matchesRule(pages: List<String>, rule: String): Boolean {
        val (p1, p2) = rule.split("|")
        val p1I = pages.indexOf(p1)
        val p2I = pages.indexOf(p2)
        if (p1I < 0 || p2I < 0) {
            return true
        } else {
            return p1I < p2I
        }
    }
}
