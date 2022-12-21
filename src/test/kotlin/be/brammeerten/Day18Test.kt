package be.brammeerten

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class Day18Test {

    @Test
    fun `part 1a`() {
        val droplet = parseDroplet("day18/exampleInput.txt")
        Assertions.assertEquals(64, droplet.countSurface())
    }

    @Test
    fun `part 1b`() {
        val droplet = parseDroplet("day18/input.txt")
        Assertions.assertEquals(4340, droplet.countSurface())
    }

    @Test
    fun `part 2a`() {
        val droplet = parseDroplet("day18/exampleInput.txt")
        Assertions.assertEquals(58, droplet.countExteriorSurface())
    }

    @Test
    fun `part 2b`() {
        val droplet = parseDroplet("day18/input.txt")
        Assertions.assertEquals(2468, droplet.countExteriorSurface())
    }

    fun parseDroplet(file: String): Droplet {
        return Droplet(readFile(file)
            .map { it.split(",").map { c -> c.toInt() } }
            .map { (x, y, z) -> C3(x, y, z) })
    }

    data class Droplet(val cubes: List<C3>) {

        fun countSurface(): Int {
            val scanned = arrayListOf<C3>()
            var sides = cubes.size * 6

            for (cube in cubes) {
                for (other in scanned)
                    if (other.hasSameSide(cube))
                        sides -= 2
                scanned.add(cube)
            }
            return sides
        }

        fun countExteriorSurface(): Int {
            val surrounding = getSurroundingCube()
            val exteriorBlocks = surrounding.grow().getSides().toHashSet()

            val allExt =  getExteriorBlocksOfCuboid(exteriorBlocks, surrounding)
            return cubes.sumOf { cube -> allExt.count{ it.hasSameSide(cube) }}
        }

        fun getExteriorBlocksOfCuboid(ext: Set<C3>, cube: Cuboid): Set<C3> {
            val sides = cube.getSides()

            var exteriorBlocks = ext

            while (true) {
                val newExteriorBlocks = exteriorBlocks + sides
                    .filter { !cubes.contains(it) }
                    .filter { exteriorBlocks.any { c -> c.hasSameSide(it) } }
                if (newExteriorBlocks.size == exteriorBlocks.size) break
                exteriorBlocks = newExteriorBlocks
            }

            if (cube.shrink() == cube)
                return exteriorBlocks
            else
                return getExteriorBlocksOfCuboid(exteriorBlocks, cube.shrink())
        }

        private fun getSurroundingCube(): Cuboid {
            var c1 = C3.MAX
            var c2 = C3.MIN
            cubes.forEach{c ->
                c1 = c1.min(c)
                c2 = c2.max(c)
            }
            return Cuboid(c1, c2)
        }
    }

    data class Cuboid(val minCo: C3, val maxCo: C3) {
        fun getSides(): Set<C3> {
            val side: HashSet<C3> = hashSetOf()

            // x plane
            for (y in minCo.y..maxCo.y) {
                for (z in minCo.z..maxCo.z) {
                    side.add(C3(minCo.x, y, z))
                    side.add(C3(maxCo.x, y, z))
                }
            }

            // y plane
            for (x in minCo.x..maxCo.x) {
                for (z in minCo.z..maxCo.z) {
                    side.add(C3(x, minCo.y, z))
                    side.add(C3(x, maxCo.y, z))
                }
            }

            // y plane
            for (x in minCo.x..maxCo.x) {
                for (y in minCo.y..maxCo.y) {
                    side.add(C3(x, y, minCo.z))
                    side.add(C3(x, y, maxCo.z))
                }
            }

            return side
        }

        fun shrink(): Cuboid {
            val xOff = if (maxCo.x - minCo.x <= 1) 0 else 1
            val yOff = if (maxCo.y - minCo.y <= 1) 0 else 1
            val zOff = if (maxCo.z - minCo.z <= 1) 0 else 1
            return Cuboid(minCo + C3(xOff, yOff, zOff), maxCo - C3(xOff, yOff, zOff))
        }

        fun grow(): Cuboid {
            return Cuboid(minCo - C3(1, 1, 1), maxCo + C3(1, 1, 1))
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
