package be.brammeerten

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

private const val ADD_X = "addx (-?\\d+)"

class Day10Test {

    @Test
    fun `part 1a`() {
        val cpu = Cpu()
        val instructionSet = readInstructions("day10/exampleInputA.txt")
        cpu.exec(instructionSet)
        Assertions.assertEquals(-1, cpu.x)
    }

    @Test
    fun `part 1b`() {
        val cpu = Cpu()
        val instructionSet = readInstructions("day10/exampleInput.txt")
        cpu.exec(instructionSet)

        val sum = cpu.historyX.entries.sumOf { (cycle, x) -> cycle * x }
        Assertions.assertEquals(13140, sum)
    }

    @Test
    fun `part 1c`() {
        val cpu = Cpu()
        val instructionSet = readInstructions("day10/input.txt")
        cpu.exec(instructionSet)

        val sum = cpu.historyX.entries.sumOf { (cycle, x) -> cycle * x }
        Assertions.assertEquals(13680, sum)
    }

    @Test
    fun `part 2a`() {
        val cpu = Cpu()
        val instructionSet = readInstructions("day10/exampleInput.txt")
        cpu.exec(instructionSet)

        val screen = generateSequence { generateSequence { "." }.take(40).toMutableList() }.take(6).toList()

        cpu.historyX.toSortedMap(compareBy { it }).entries.forEach{(cycle, x) ->
            val spriteI = (x-1)..(x+1)
            val col = (cycle-1) % 40
            val row = (cycle-1) / 40
            if (col in spriteI) screen[row][col] = "#"
        }

        Assertions.assertEquals("""
            ##..##..##..##..##..##..##..##..##..##..
            ###...###...###...###...###...###...###.
            ####....####....####....####....####....
            #####.....#####.....#####.....#####.....
            ######......######......######......####
            #######.......#######.......#######.....
        """.trimIndent(), screenToString(screen))
    }

    @Test
    fun `part 2b`() {
        val cpu = Cpu()
        val instructionSet = readInstructions("day10/input.txt")
        cpu.exec(instructionSet)

        val screen = generateSequence { generateSequence { "." }.take(40).toMutableList() }.take(6).toList()

        cpu.historyX.toSortedMap(compareBy { it }).entries.forEach{(cycle, x) ->
            val spriteI = (x-1)..(x+1)
            val col = (cycle-1) % 40
            val row = (cycle-1) / 40
            if (col in spriteI) screen[row][col] = "#"
        }

       println(screenToString(screen))
    }

    fun screenToString(screen: List<List<String>>) =
        screen.map { it.joinToString("") }.joinToString("\n")

    fun readInstructions(file: String) =
        readFile(file)
            .map {
                if (it == "noop")
                    OpCode("noop", 1)
                else if (Regex(ADD_X).matches(it))
                    OpCode("addx", 2, extractRegexGroupsI(ADD_X, it))
                else
                    throw IllegalStateException("Unknown opcode: $it")
            }

    class Cpu {
        var x = 1
        var cycle = 1
        val historyX: HashMap<Int, Int> = HashMap()

        init {
            historyX.put(cycle, x)
        }

        fun exec(instructionSet: List<OpCode>) {
            instructionSet.forEach { exec(it) }
        }

        fun exec(opCode: OpCode) {
            when(opCode.code) {
                "noop" -> cycle()
                "addx" -> {
                    cycle()
                    x += opCode.args[0]
                    cycle()
                } else -> throw IllegalStateException("Unexpected opcode: " + opCode.code)
            }
        }

        private fun cycle() {
            cycle++
//            if (listOf(20, 60, 100, 140, 180, 220).contains(cycle))
                historyX.put(cycle, x)
        }
    }

    data class OpCode(val code: String, val cycles: Int, val args: List<Int> = emptyList())
}