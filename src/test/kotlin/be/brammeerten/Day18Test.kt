package be.brammeerten

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class Day18Test {

    @Test
    fun `part 1a`() {
        val droplet = parseDroplet("day18/exampleInput.txt")
        Assertions.assertEquals(64, droplet.countSides())
    }

    @Test
    fun `part 1b`() {
        val droplet = parseDroplet("day18/input.txt")
        Assertions.assertEquals(4340, droplet.countSides())
    }

    fun parseDroplet(file: String): Droplet {
        return Droplet(readFile(file)
            .map { it.split(",").map { c -> c.toInt() } }
            .map { (x, y, z) -> C3(x, y, z) })
    }

    data class Droplet(val cubes: List<C3>) {

        fun countSides(): Int {
            val scanned = arrayListOf<C3>()
            var sides = cubes.size * 6

            for (cube in cubes) {
                for (other in scanned) {
                    if (other.hasSameSide(cube))
                        sides -= 2
                }
                scanned.add(cube)
            }
            return sides
        }

    }

}

fun C3.hasSameSide(other: C3): Boolean {
    return arrayOf(
        this + C3(0, 0, 1),
        this + C3(0, 1, 0),
        this + C3(1, 0, 0),
        this + C3(0, 0, -1),
        this + C3(0, -1, 0),
        this + C3(-1, 0, 0),
    ).contains(other)
}
