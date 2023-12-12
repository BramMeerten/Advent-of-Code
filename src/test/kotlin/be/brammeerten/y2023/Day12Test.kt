package be.brammeerten.y2023

import be.brammeerten.readFileAndSplitLines
import be.brammeerten.toCharList
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class Day12Test {

    @Test
    fun `part 1`() {
        val num1 = readFileAndSplitLines("2023/day12/exampleInput.txt", " ")
            .sumOf { tryIt(it[0], it[1].split(",").map { it.toInt() }) }
        val num2 = readFileAndSplitLines("2023/day12/input.txt", " ")
            .sumOf { tryIt(it[0], it[1].split(",").map { it.toInt() }) }

        assertThat(num1).isEqualTo(21);
        assertThat(num2).isEqualTo(7090);
    }

    @Test
    fun `part 2`() {
        val num1 = readFileAndSplitLines("2023/day12/exampleInput.txt", " ")
            .sumOf {
                val damaged = it[1].split(",").map { it.toInt() }
                val damagedAll = mutableListOf<Int>()
                for (i in 0 until 5) damagedAll.addAll(damaged)
                tryIt((it[0] + "?").repeat(5).dropLast(1), damagedAll)
            }
        val num2 = readFileAndSplitLines("2023/day12/input.txt", " ")
            .sumOf {
                val damaged = it[1].split(",").map { it.toInt() }
                val damagedAll = mutableListOf<Int>()
                for (i in 0 until 5) damagedAll.addAll(damaged)
                tryIt((it[0] + "?").repeat(5).dropLast(1), damagedAll)
            }

        assertThat(num1).isEqualTo(525152)
        assertThat(num2).isEqualTo(6792010726878)  // Too low = 1667431902
    }


    var cache: MutableMap<String, Long> = mutableMapOf()
    // .#.###.#.######
    fun tryIt(conditions: String, damagedGroups: List<Int>): Long {
        val index = conditions + "$" + damagedGroups.joinToString(",")
        val cacheVal = cache[index]
        if (cacheVal != null) return cacheVal

        if (conditions.isEmpty() && damagedGroups.isEmpty()) {
            cache[index] = 1
            return 1
        }
        if ((conditions.isEmpty() && damagedGroups.isNotEmpty())
            || (damagedGroups.isEmpty() && conditions.contains('#'))) {
            cache[index] = 0
            return 0
        }

        if (conditions.length < damagedGroups.sum() + damagedGroups.size - 1) {
            cache[index] = 0
            return 0;
        }

        if (conditions[0] == '.') {
            val cacheVal = tryIt(conditions.drop(1), damagedGroups)
            cache[index] = cacheVal
            return cacheVal

        } else if (conditions[0] == '#') {
            val nextDamagedSize = damagedGroups.first()
            if (conditions.substring(0, nextDamagedSize).contains(".")) {
                cache[index] = 0
                return 0
            }
            if (conditions.length > nextDamagedSize) {
                if (conditions[nextDamagedSize] == '#') {
                    cache[index] = 0
                    return 0
                }

                val cacheVal = tryIt(conditions.substring(nextDamagedSize + 1), damagedGroups.drop(1))
                cache[index] = cacheVal
                return cacheVal
            }

            return tryIt(conditions.substring(nextDamagedSize), damagedGroups.drop(1))

        } else { // == '?'
            val cacheVal = tryIt("." + conditions.substring(1), damagedGroups) + tryIt("#" + conditions.substring(1), damagedGroups)
            cache[index] = cacheVal
            return cacheVal
        }
    }
}