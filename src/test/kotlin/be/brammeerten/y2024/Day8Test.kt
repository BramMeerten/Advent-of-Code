package be.brammeerten.y2024

import be.brammeerten.Co
import be.brammeerten.combinations
import be.brammeerten.readFile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class Day8Test {

    @Test
    fun `part 1`() {
        val (antennas, dimensions) = readAntennas("2024/day8/exampleInput.txt")

        val antinodes: MutableSet<Co> = mutableSetOf();
        antennas.keys.forEach {
            combinations(antennas[it]!!).forEach { (a, b) ->
                antinodes.add(a + (a - b))
                antinodes.add(b + (b - a))
            }
        }

        val antinodesInsideBoard = antinodes.filter { it.col >= 0 && it.col < dimensions.first && it.row >= 0 && it.row < dimensions.second }

        assertThat(antinodesInsideBoard.size).isEqualTo(14);
//        assertThat(sum).isEqualTo(220);
    }

    @Test
    fun `part 2`() {
        val (antennas, dimensions) = readAntennas("2024/day8/exampleInput.txt")

        val antinodes: MutableSet<Co> = mutableSetOf();
        antennas.keys.forEach {
            combinations(antennas[it]!!).forEach { (a, b) ->
                var anti = a;
                while(anti.col >= 0 && anti.col < dimensions.first && anti.row >= 0 && anti.row < dimensions.second) {
                    antinodes.add(anti);
                    anti += (a - b)
                }

                anti = b;
                while(anti.col >= 0 && anti.col < dimensions.first && anti.row >= 0 && anti.row < dimensions.second) {
                    antinodes.add(anti);
                    anti += (b - a)
                }
            }
        }

        assertThat(antinodes.size).isEqualTo(34);
//        assertThat(sum).isEqualTo(813);
    }

    private fun readAntennas(fileName: String): Pair<MutableMap<Char, MutableSet<Co>>, Pair<Int, Int>> {
        val lines = readFile(fileName).filter { it.isNotEmpty() }
        val antennas = mutableMapOf<Char, MutableSet<Co>>()
        val h = lines.size
        val w = lines[0].length

        for (row in lines.indices) {
            for (col in lines[row].indices) {
                if (lines[row][col] == '.') continue;
                val set = antennas.getOrDefault(lines[row][col], mutableSetOf())
                set.add(Co(row, col))
                antennas[lines[row][col]] = set
            }
        }

        return Pair(antennas, Pair(w, h))
    }
}
