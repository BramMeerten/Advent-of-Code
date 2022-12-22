package be.brammeerten

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day20Test {

    @Test
    fun `part 1a`() {
        val coordinates = readInput("day20/exampleInput.txt")
        val mix = mix(coordinates)
        assertThat(mix).isEqualTo(listOf(1L, 2L, -3L, 4L, 0L, 3L, -2L))

        assertThat(get(mix, 1000)).isEqualTo(4L)
        assertThat(get(mix, 2000)).isEqualTo(-3L)
        assertThat(get(mix, 3000)).isEqualTo(2L)
        assertThat(get(mix, 1000) + get(mix, 2000) + get(mix, 3000)).isEqualTo(3L)
    }

    @Test
    fun `part 1b`() {
        val coordinates = readInput("day20/input.txt")
        val mix = mix(coordinates)
        assertThat(get(mix, 1000) + get(mix, 2000) + get(mix, 3000)).isEqualTo(7153L)
    }

    @Test
    fun `part 2`() {
        val coordinates = readInput("day20/exampleInput.txt")
            .map { it * 811589153L }
        println(coordinates.map { it%7 })
        println("Expected " + listOf(1, 2, 5, -5, 1, 0, 2).map { it * 811589153L})
        var mix = mix(coordinates)
        assertThat(mix).isEqualTo(listOf(0L, -2434767459L, 3246356612L, -1623178306L, 2434767459L, 1623178306L, 811589153L))

    }

    fun readInput(file: String) = readFile(file).map { it.toLong() }

    fun mix(original: List<Long>): List<Long> {
        val new = ArrayList(original.mapIndexed { i, co -> i to co })
        for (i in original.indices) {
            val cur = new.find { it.first == i }!!
            val curI = new.indexOf(cur)
            val diff = (curI + cur.second)
            val newI = if (diff > 0) (diff % (original.size-1)) else ((original.size-1) + (diff % (original.size-1)))
            new.remove(cur)
            new.add(newI.toInt(), -1 to cur.second)
        }
        return new.map { it.second }
    }

    fun get(list: List<Long>, index: Int): Long {
        val zeroI = list.indexOf(0)
        val i = (zeroI + index) % list.size
        return list[i]
    }

}
