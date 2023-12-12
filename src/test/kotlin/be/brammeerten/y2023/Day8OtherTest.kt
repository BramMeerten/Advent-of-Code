package be.brammeerten.y2023

import be.brammeerten.extractRegexGroups
import be.brammeerten.readFile
import be.brammeerten.toCharList
import org.junit.jupiter.api.Test

class Day8OtherTest {

    @Test
    fun `part 2`() {
        val lines = readFile("2023/day8/input.txt")
        val instructions = lines[0].toCharList()
        val startPositions = mutableListOf<String>()
        val paths = lines.drop(2).associate {
            val values = extractRegexGroups("^(...) = \\((...), (...)\\)$", it)
            if (values[0][2] == 'A') startPositions.add(values[0])
            values[0] to Path(values[1], values[2])
        }

        println("Start: " + startPositions.size)

        val history = mutableListOf<Pair<Int, List<Int>>>()
        for (posI in 0 until startPositions.size) {
            val temp = findForStartPos(startPositions[posI], instructions, paths)
            history.add(temp.size to temp.mapIndexed { i, value ->
                if (value[2] == 'Z') i+1 else -1
            }.filter { it != -1 })
//            println(temp)
            println(history[posI])
            println("----")
        }

        var count = 0
        var indexOfFewest = 0

        val posFewestZs = 4
        while (true) {
            count += history[posFewestZs].second[indexOfFewest]
            val found = !history.asSequence()
                .filter {
                    !it.second.contains(((count-1) % it.first)+1)
                }
                .any()

            if (found) break;

            indexOfFewest = (indexOfFewest+1) % history[posFewestZs].second.size
        }

        println("Done calculating history: " + count)

        /*var instructionI = 0
        var count: Long = 0
        while (!allZ(positions)) {
            val isLeft = instructions[instructionI] == 'L'
            val pos = positions[POS_I]
            positions[i] = if (isLeft) paths[pos]!!.toLeft else paths[pos]!!.toRight
            count++
            instructionI = (instructionI + 1) % instructions.size
        }
        assertThat(count).isEqualTo(6);*/
//         assertThat(i).isEqualTo(14257);
    }

    private fun findForStartPos(startPos: String, instructions: List<Char>, paths: Map<String, Path>): List<String> {
        val output = mutableListOf<String>()
        var instructionI = 0
        var pos = startPos
        while(true) {
            val isLeft = instructions[instructionI] == 'L'
            pos = if (isLeft) paths[pos]!!.toLeft else paths[pos]!!.toRight

            var searchI = instructionI
            while(output.size > searchI) {
                if (output[searchI] == pos) break;
                searchI += instructions.size
            }

            // TODO gevonden
            if (output.size > searchI) {
                break;
            }

            output.add(pos)
            instructionI = (instructionI + 1) % instructions.size
        }

        return output
    }

    private fun allZ(positions: List<String>): Boolean {
        return !positions
            .asSequence()
            .filter { it[2] != 'Z' }
            .any()
    }
}
