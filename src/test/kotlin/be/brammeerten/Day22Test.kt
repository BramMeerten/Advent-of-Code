package be.brammeerten

import be.brammeerten.Day22Test.Instruction.*
import be.brammeerten.Day22Test.Square.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day22Test {

    @Test
    fun `part 1a`() {
        val map = readMap("day22/exampleInput.txt")
        val instructions = readInstructions("day22/exampleInput.txt")

        instructions.forEach{map.execute(it, print = true)}
        assertThat(map.getPassword()).isEqualTo(6032)
    }

    @Test
    fun `part 1b`() {
        val map = readMap("day22/input.txt")
        val instructions = readInstructions("day22/input.txt")

        instructions.forEach{map.execute(it)}
        assertThat(map.getPassword()).isEqualTo(55244)
    }

    @Test
    fun `part 2a`() {
        val map = read3DMap("day22/exampleInput.txt", 3)
        val instructions = readInstructions("day22/exampleInput.txt")

        instructions.forEach{map.execute(it)}
        assertThat(map.pos).isEqualTo(C3(0, 1, 3))
    }

    @Test
    fun `part 2b`() {
        val map = read3DMap("day22/input.txt", 4)
        val instructions = readInstructions("day22/input.txt")

        instructions.forEach{map.execute(it)}
        assertThat(map.pos).isEqualTo(C3(0, 37, 28)) // --> x=36, y=122 --> (123*1000)+(37*4)+1
    }

    fun readMap(file: String): MMap {
        val lines = readFile(file).dropLast(2)
        val w = lines.maxOf { it.length }

        return MMap(lines.map {
            it.padEnd(w, ' ').toCharList().map { c ->
                when (c) {
                    '#' -> WALL
                    '.' -> OPEN
                    else -> NOTHING
                }
            }.toTypedArray()
        }.toTypedArray())
    }

    fun read3DMap(file: String, sidesInWidth: Int): Map3D {
        val lines = readFile(file).dropLast(2)
        val w = lines.maxOf { it.length }

        return Map3D(lines.map {
            it.padEnd(w, ' ').toCharList().map { c ->
                when (c) {
                    '#' -> WALL
                    '.' -> OPEN
                    else -> NOTHING
                }
            }.toTypedArray()
        }.toTypedArray(), sidesInWidth)
    }

    fun readInstructions(file: String): List<Pair<Instruction, Int>> {
        var line = readFile(file).last()
        if (!line[line.length - 1].isDigit()) throw IllegalStateException("Not implemented")
        if (line.contains("-")) throw IllegalStateException("No negative numbers allowed")
        val instructions = arrayListOf<Pair<Instruction, Int>>()

        while (line.length > 0) {
            if (line[0].isDigit()) {
                val num = line.toCharList().takeWhile { it.isDigit() }.joinToString("")
                instructions.add(WALK to num.toInt())
                line = line.substring(num.length)
            } else {
                instructions.add((if (line[0] == 'L') ROTATE_LEFT else ROTATE_RIGHT) to 0)
                line = line.substring(1)
            }
        }

        return instructions
    }

    class Map3D(val map2d: Array<Array<Square>>, sidesInWidth: Int) {
        val n = map2d.size / sidesInWidth
        val map3d = convert(map2d, sidesInWidth)
        var face: C3 = C3.RIGHT
        var pos: C3 = C3(1, 0, 1)

        fun execute(instruction: Pair<Instruction, Int>) {
            if (instruction.first == ROTATE_RIGHT)
                face = rotateRight(pos, face)
            else if (instruction.first == ROTATE_LEFT)
                face = rotateLeft(pos, face)
            else {
                for (i in 0 until instruction.second) {
                    var new = pos + face
                    val debugNew = new
                    val debugFace = face
                    if (listOf(new.x, new.y, new.z).count { it == 0 || it == n + 1 } >= 2) {
                        if (new.y == 0 && new.z == 0) face = if (face == C3.UP) C3.FRONT else C3.DOWN
                        if (new.y == 0 && new.z == n+1) face = if (face == C3.FRONT) C3.DOWN else C3.BACK
                        if (new.y == 0 && new.x == n+1) face = if (face == C3.RIGHT) C3.DOWN else C3.LEFT
                        if (new.y == 0 && new.x == 0) face = if (face == C3.LEFT) C3.DOWN else C3.RIGHT

                        if (new.y == n+1 && new.z == 0) face = if (face == C3.DOWN) C3.FRONT else C3.UP
                        if (new.y == n+1 && new.z == n+1) face = if (face == C3.FRONT) C3.UP else C3.BACK
                        if (new.y == n+1 && new.x == n+1) face = if (face == C3.RIGHT) C3.UP else C3.LEFT
                        if (new.y == n+1 && new.x == 0) face = if (face == C3.LEFT) C3.UP else C3.RIGHT

                        if (new.x == 0 && new.z == 0) face = if (face == C3.LEFT) C3.FRONT else C3.RIGHT
                        if (new.x == n+1 && new.z == 0) face = if (face == C3.RIGHT) C3.FRONT else C3.LEFT
                        if (new.x == n+1 && new.z == n+1) face = if (face == C3.RIGHT) C3.BACK else C3.LEFT
                        if (new.x == 0 && new.z == n+1) face = if (face == C3.FRONT) C3.RIGHT else C3.BACK

                        new += face
                    }

                    if (map3d[new.z][new.y][new.x]) {
                        face = debugFace
                        break
                    }
                    pos = new
                }
            }
        }

        fun rotateRight(pos: C3, c: C3): C3 {
            if (pos.x == n+1) return C3(c.x, -c.z, c.y)
            if (pos.x == 0) return C3(c.x, c.z, -c.y)
            if (pos.y == 0) return C3(-c.z, c.y, c.x)
            if (pos.y == n+1) return C3(c.z, c.y, -c.x)
            if (pos.z == n+1) return C3(-c.y, c.x, c.z)
            if (pos.z == 0) return C3(c.y, -c.x, c.z)
            throw IllegalStateException("SHOULD NOT EXIST $pos")
        }

        fun rotateLeft(pos: C3, c: C3): C3 {
            if (pos.x == 0) return C3(c.x, -c.z, c.y)
            if (pos.x == n+1) return C3(c.x, c.z, -c.y)
            if (pos.y == n+1) return C3(-c.z, c.y, c.x)
            if (pos.y == 0) return C3(c.z, c.y, -c.x)
            if (pos.z == 0) return C3(-c.y, c.x, c.z)
            if (pos.z == n+1) return C3(c.y, -c.x, c.z)
            throw IllegalStateException("SHOULD NOT EXIST $pos")
        }

        companion object {
            fun convert(map2d: Array<Array<Square>>, sidesInWidth: Int): Array<Array<Array<Boolean>>> {
//                printJs(map2d)
                val n = map2d.size / sidesInWidth
                val matrix = if (sidesInWidth == 4) getConversionMatrix2(n) else getConversionMatrix(n)
                val map = Array(n+2) {Array(n+2) {Array(n+2){false} } }
                for (y in map2d.indices) {
                    for (x in map2d[y].indices) {
                        val convX = x / n
                        val convY = y / n
                        val sideX = x % n
                        val sideY = y % n
                        val co = matrix[convY][convX](C(sideX, sideY))
//                        print(co.toString().padEnd(5))
                        if (co != null)
                            map[co.z][co.y][co.x] = map[co.z][co.y][co.x] || map2d[y][x] == WALL

                    }
//                    println()
                }
                return map
            }

            fun getConversionMatrix(n: Int) = arrayOf(
                arrayOf(
                    { c: C -> null},
                    { c: C -> null},
                    { c: C -> C3(c.x + 1, 0, c.y + 1)},
                    { c: C -> null},
                ),
                arrayOf(
                    { c: C -> C3(n-1 + 1 -c.x, c.y  + 1, 0)},
                    { c: C -> C3(0, c.y + 1, c.x + 1)},
                    { c: C -> C3(c.x +1, c.y +1, n-1 +2)},
                    { c: C -> null},
                ),
                arrayOf(
                    { c: C -> null},
                    { c: C -> null},
                    { c: C -> C3(c.x +1, n-1 +2, n-1 -c.y +1)},
                    { c: C -> C3(n-1 +2, n-1 -c.x +1, n-1 -c.y +1)}
                )
            )

            fun getConversionMatrix2(n: Int) = arrayOf(
                arrayOf(
                    { c: C -> null},
                    { c: C -> C3(c.x +1, 0, c.y +1)},
                    { c: C -> C3(n-1+2, c.x +1, c.y +1)},
                    { c: C -> null},
                ),
                arrayOf(
                    { c: C -> null},
                    { c: C -> C3(c.x +1, c.y +1, n-1+2)},
                    { c: C -> null},
                    { c: C -> null},
                ),
                arrayOf(
                    { c: C -> C3(0, c.x +1, n-1 -c.y +1)},
                    { c: C -> C3(c.x +1, n-1+2, n-1 -c.y +1)},
                    { c: C -> null},
                    { c: C -> null},
                ),
                arrayOf(
                    { c: C -> C3(c.y +1, c.x +1, 0)},
                    { c: C -> null},
                    { c: C -> null},
                    { c: C -> null},
                )
            )
    fun printJs(map2d: Array<Array<Square>>) {
        println("[")
        for (y in map2d.indices) {
            print("[")
            print(map2d[y]
                .map { s -> when(s) {OPEN -> "\".\""; WALL -> "\"#\""; else -> "\" \""} }
                .joinToString(", "))
            println("],")
        }
        println("]")
    }
        }
    }


    class MMap(val map: Array<Array<Square>>) {
        val w = map[0].size
        val h = map.size
        val start = C(map[0].indexOf(OPEN), 0)
        var pos = start
        var face: C = C.RIGHT

        fun execute(instruction: Pair<Instruction, Int>, print: Boolean = false) {
            if (instruction.first == ROTATE_RIGHT)
                face = C(face.y * -1, face.x)
            else if (instruction.first == ROTATE_LEFT)
                face = C(face.y, face.x * -1)
            else {
                for (i in 0 until instruction.second) {
                    var new = pos + face
                    if (new.y < 0 || new.y >= h || new.x < 0 || new.x >= w || map[new.y][new.x] == NOTHING) {
                        if (face == C.RIGHT) new = C(map[new.y].indexOfFirst { it != NOTHING }, new.y)
                        if (face == C.LEFT) new = C(map[new.y].indexOfLast { it != NOTHING }, new.y)
                        if (face == C.DOWN) new = C(new.x, (0 until h).first { y -> map[y][new.x] != NOTHING })
                        if (face == C.UP) new = C(new.x, (0 until h).last { y -> map[y][new.x] != NOTHING })
                    }
                    if (map[new.y][new.x] == WALL) break
                    pos = new
                }
            }

            if (print) {
                instruction.print()
                print()
            }
        }

        fun print() {
            for (y in map.indices) {
                for (x in map[y].indices) {
                    if (pos == C(x, y)) {
                        print(when (face) {
                            C.RIGHT -> ">"
                            C.LEFT -> "<"
                            C.DOWN -> "v"
                            else -> "^"
                        })
                    } else {
                        print(when (map[y][x]) {NOTHING -> " "; OPEN -> "."; else -> "#" })
                    }
                }
                println()
            }
            println()
        }

        fun getPassword(): Int {
            val row = 1000 * (pos.y + 1)
            val col = 4 * (pos.x + 1)
            val face = when(face) { C.RIGHT -> 0; C.DOWN -> 1; C.LEFT -> 2; else -> 3}
            return row + col + face
        }
    }

    enum class Square {
        WALL, OPEN, NOTHING
    }

    enum class Instruction() {
        ROTATE_RIGHT, ROTATE_LEFT, WALK;
    }

}

fun Pair<Day22Test.Instruction, Int>.print() {
    when(first) {
        WALK -> println("Walk $second")
        ROTATE_RIGHT -> println("Rotate Right")
        ROTATE_LEFT -> println("Rotate Left")
    }
}
