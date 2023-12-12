package be.brammeerten.y2023

import be.brammeerten.extractRegexGroups
import be.brammeerten.readFile
import be.brammeerten.toCharList
import org.junit.jupiter.api.Test

class Day8Other2Test {


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

        val history = mutableListOf<Pair<Pair<Int, Int>, List<Int>>>()
        for (posI in 0 until startPositions.size) {
            val temp = findForStartPos(startPositions[posI], instructions, paths)
            val important = temp.second.drop(temp.first)
            history.add((temp.first to important.size) to important.mapIndexed { i, value ->
                if (value[2] == 'Z') i else -1
            }.filter { it != -1 })

            println("Size: " + history[posI].first + ": " + history[posI].second.size + "zs")
        }

        var count: Long = 0

        val posFewestZs = 4
        while (true) {
            if (count == 0L)
                count += history[posFewestZs].first.first + history[posFewestZs].second[0] // TODO assumes there is only 1 z per cycle
            else
                count += history[posFewestZs].first.second
            val found = !history.asSequence()
                .filter {
                    !it.second.contains(((count-it.first.first) % it.first.second).toInt())
                }
                .any()

            if (found) break;
        }
//
        println("Done calculating history: " + count + 1)

    }

    private fun findForStartPos(startPos: String, instructions: List<Char>, paths: Map<String, Path>): Pair<Int, List<String>> {
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
                return searchI to output
            }

            output.add(pos)
            instructionI = (instructionI + 1) % instructions.size
        }
    }
}
