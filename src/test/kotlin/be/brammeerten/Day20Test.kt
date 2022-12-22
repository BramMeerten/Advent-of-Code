package be.brammeerten

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day20Test {

    @Test
    fun `part 1a`() {
        val coordinates = readInput("day20/exampleInput.txt")
        val mix = mix(coordinates.mapIndexed { i, c -> i to c })
        assertThat(mix.map { it.second }).isEqualTo(listOf(1L, 2L, -3L, 4L, 0L, 3L, -2L))

        assertThat(get(mix, 1000)).isEqualTo(4L)
        assertThat(get(mix, 2000)).isEqualTo(-3L)
        assertThat(get(mix, 3000)).isEqualTo(2L)
        assertThat(get(mix, 1000) + get(mix, 2000) + get(mix, 3000)).isEqualTo(3L)
    }

    @Test
    fun `part 1b`() {
        val coordinates = readInput("day20/input.txt")
        val mix = mix(coordinates.mapIndexed { i, c -> i to c })
        assertThat(get(mix, 1000) + get(mix, 2000) + get(mix, 3000)).isEqualTo(7153L)
    }

    @Test
    fun `part 2`() {
        val coordinates = readInput("day20/exampleInput.txt")
            .map { it * 811589153L }
        val input = coordinates.mapIndexed { i, c -> i to c }
        var mix = mix(input)
        mix = mix(mix)
        mix = mix(mix)
        mix = mix(mix)
        mix = mix(mix)
        mix = mix(mix)
        mix = mix(mix)
        mix = mix(mix)
        mix = mix(mix)
        mix = mix(mix)
        assertThat(get(mix, 1000) + get(mix, 2000) + get(mix, 3000)).isEqualTo(1623178306L)
    }

    fun readInput(file: String) = readFile(file).map { it.toLong() }

    fun mix(original: List<Pair<Int, Long>>): List<Pair<Int,Long>> {
        val copy = ArrayList(original)
        for (i in original.indices) {
            val cur = copy.find { it.first == i }!!
            val curI = copy.indexOf(cur)
            val diff = (curI + cur.second)
            val newI = if (diff > 0) (diff % (original.size-1)) else ((original.size-1) + (diff % (original.size-1)))
            copy.remove(cur)
            copy.add(newI.toInt(), i to cur.second)
        }
        return copy
    }

    fun get(list: List<Pair<Int, Long>>, index: Int): Long {
        val ll = list.map { it.second }
        val zeroI = ll.indexOf(0)
        val i = (zeroI + index) % ll.size
        return ll[i]
    }

}
