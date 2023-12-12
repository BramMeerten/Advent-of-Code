package be.brammeerten.y2023

import be.brammeerten.extractRegexGroups
import be.brammeerten.readFile
import be.brammeerten.toCharList
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day8Test {

    @Test
    fun `part 1`() {
        val lines = readFile("2023/day8/exampleInput.txt")
        val instructions = lines[0].toCharList()
        val paths = lines.drop(2).associate {
            val values = extractRegexGroups("^(...) = \\((...), (...)\\)$", it)
            values[0] to Path(values[1], values[2])
        }

        var i = 0
        var pos = "AAA"
        while (pos != "ZZZ") {
            pos = if (instructions[i % instructions.size] == 'L') paths[pos]!!.toLeft else paths[pos]!!.toRight
            i++
        }
        assertThat(i).isEqualTo(6);
//         assertThat(i).isEqualTo(14257);
    }

    @Test
    fun `part 2`() {
        val lines = readFile("2023/day8/input.txt")
        val instructions = lines[0].toCharList()
        val positions = mutableListOf<String>()
        val paths = lines.drop(2).associate {
            val values = extractRegexGroups("^(...) = \\((...), (...)\\)$", it)
            if (values[0][2] == 'A') positions.add(values[0])
            values[0] to Path(values[1], values[2])
        }

        println("Start: " + positions.size)

        var instructionI = 0
        var count: Long = 0
        while (!allZ(positions)) {
            val isLeft = instructions[instructionI] == 'L'
            for (i in positions.indices) {
                val pos = positions[i]
                positions[i] = if (isLeft) paths[pos]!!.toLeft else paths[pos]!!.toRight
            }
            count++
            instructionI = (instructionI + 1) % instructions.size
        }
        assertThat(count).isEqualTo(6);
//         assertThat(i).isEqualTo(14257);
    }

    private fun allZ(positions: List<String>): Boolean {
        return !positions
            .asSequence()
            .filter { it[2] != 'Z' }
            .any()
    }
}

data class Path(val toLeft: String, val toRight: String)