package be.brammeerten

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day20Test {

    @Test
    fun `part 1a`() {
        val coordinates = readInput("day20/exampleInput.txt")
        val mix = mix(coordinates)
        assertThat(mix).isEqualTo(listOf(1, 2, -3, 4, 0, 3, -2))

        assertThat(get(mix, 1000)).isEqualTo(4)
        assertThat(get(mix, 2000)).isEqualTo(-3)
        assertThat(get(mix, 3000)).isEqualTo(2)
        assertThat(get(mix, 1000) + get(mix, 2000) + get(mix, 3000)).isEqualTo(3)
    }

    @Test
    fun `part 1b`() {
        val coordinates = readInput("day20/input.txt")
        val mix = mix(coordinates)
        assertThat(get(mix, 1000) + get(mix, 2000) + get(mix, 3000)).isEqualTo(3)
    }

    fun readInput(file: String) = readFile(file).map { it.toInt() }

    fun mix(original: List<Int>): List<Int> {
        val new = ArrayList(original.mapIndexed { i, co -> i to co })
        for (i in original.indices) {
            val cur = new.find { it.first == i }!!
            val curI = new.indexOf(cur)
            val diff = (curI + cur.second)
            val newI = if (diff > 0) (diff % (original.size-1)) else ((original.size-1) + (diff % (original.size-1)))
            new.remove(cur)
            new.add(newI, -1 to cur.second)
        }
        return new.map { it.second }
    }

    fun get(list: List<Int>, index: Int): Int {
        val zeroI = list.indexOf(0)
        val i = (zeroI + index) % list.size
        return list[i]
    }

}
