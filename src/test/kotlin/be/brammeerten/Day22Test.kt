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

        for (i in instructions)
            map.execute(i, print = true)

        assertThat(map.getPassword()).isEqualTo(6032)
    }

    @Test
    fun `part 1b`() {
        val map = readMap("day22/input.txt")
        val instructions = readInstructions("day22/input.txt")

        for (i in instructions)
            map.execute(i)

        assertThat(map.getPassword()).isEqualTo(55244)
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
