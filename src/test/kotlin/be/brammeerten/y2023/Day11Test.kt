package be.brammeerten.y2023

import be.brammeerten.CL
import be.brammeerten.readFile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class Day11Test {

    @Test
    fun `part 1`() {
        val lines = readFile("2023/day11/exampleInput.txt");
//        val lines = readFile("2023/day11/input.txt");
        val map = createUniverse(lines)
            .expand()
//            .print();
        val sum = map.pairs
            .asSequence()
            .mapIndexed { i, value ->
                val w = Math.abs(value.first.x - value.second.x)
                val h = Math.abs(value.first.y - value.second.y)
                w + h
            }
            .sum()

        assertThat(sum).isEqualTo(374);
//        assertThat(sum).isEqualTo(1000000);
    }

    @Test
    fun `part 2`() {
//        val lines = readFile("2023/day11/exampleInput.txt");
        val lines = readFile("2023/day11/input.txt");
        val map = createUniverse(lines)
//            .print()
//            .expand(1)
//            .expand(10-1)
//            .expand(100-1)
            .expand(1000000-1)
//            .print();
        val sum = map.pairs
            .asSequence()
            .mapIndexed { i, value ->
                val w = Math.abs(value.first.x - value.second.x)
                val h = Math.abs(value.first.y - value.second.y)
                w + h
            }
            .sum()

//        assertThat(sum).isEqualTo(374);
//        assertThat(sum).isEqualTo(1030);
//        assertThat(sum).isEqualTo(8410);                      710674907809
        assertThat(sum).isEqualTo(710674907809)
    }

    fun createUniverse(rows: List<String>): Universe {
        val w = rows[0].length;
        val h = rows.size
        val galaxies: MutableList<CL> = mutableListOf()

        for (x in 0 until w) {
            for (y in 0 until h) {
                if (rows[y][x] == '#')
                    galaxies.add(CL(x.toLong(), y.toLong()))
            }
        }

        return Universe(galaxies, w.toLong(), h.toLong())
    }

    class Universe(val galaxies: List<CL>, val w: Long, val h: Long) {
        val pairs: MutableList<Pair<CL, CL>> = mutableListOf()

        init {
            for (i in galaxies.indices) {
                for (j in (i+1) until galaxies.size) {
                    pairs.add(galaxies[i] to galaxies[j])
                }
            }
        }

        fun expand(n: Int = 1): Universe {
            var newGalaxies = galaxies
            var newWidth = w
            var newHeight = h
            for (x in 0 until w) {
                if (galaxies.count { it.x == x } == 0) {
                    val x2 = x + (newWidth - w)
                    newGalaxies = newGalaxies.map { if (it.x >= x2) CL(it.x+n, it.y) else it }
                    newWidth += n
                }
            }

            for (y in 0 until h) {
                if (galaxies.count { it.y == y } == 0) {
                    val y2 = y + (newHeight - h)
                    newGalaxies = newGalaxies.map { if (it.y >= y2) CL(it.x, it.y+n) else it }
                    newHeight += n
                }
            }

            return Universe(newGalaxies, newWidth, newHeight)
        }

        fun print(): Universe {
            for (y in 0 until h) {
                for (x in 0 until w) {
                    if (galaxies.contains(CL(x, y)))
                        print("#")
                    else print(".")
                }
                println()
            }
            println("\n")
            return this
        }
    }

}