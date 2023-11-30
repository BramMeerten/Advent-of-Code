package be.brammeerten.y2022

import be.brammeerten.readFile
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class Day5Test {

    @Test
    fun `part 1`() {
        val input = readFile("2022/day5/exampleInput.txt")
        val stacks = readStacks(input)
        val moves = readMoves(input)

        moves.forEach{ it.execute(stacks)}
        val result = stacks.map { it.peek() }.joinToString("")
        assertEquals("CMZ", result)
    }

    @Test
    fun `part 2`() {
        val input = readFile("2022/day5/exampleInput.txt")
        val stacks = readStacks(input)
        val moves = readMoves(input)

        moves.forEach{ it.executePart2(stacks)}
        val result = stacks.map { it.peek() }.joinToString("")
        assertEquals("MCD", result)
    }

    private fun readMoves(input: List<String>): List<Move> {
        val regex = "move (\\d+) from (\\d+) to (\\d+)".toRegex()
        val moves = input
            .filter { it.startsWith("move") }
            .map {
                val matches = regex.find(it)
                Move(matches!!.groupValues[1].toInt(), matches.groupValues[2].toInt(), matches.groupValues[3].toInt())
            }
        return moves
    }

    private fun readStacks(input: List<String>): List<Stack<Char>> {
        val num = (input[0].length + 1) / 4
        val stacks = (1..num).map { Stack<Char>() }.toList()
        input
            .takeWhile { !it.startsWith(" 1   2   3") }
            .forEach { stacks.forEachIndexed { i, stack -> pushIfPresent(it, i, stack) } }
        stacks.forEach { it.reverse() }
        return stacks
    }

    private fun pushIfPresent(line: String, index: Int, stack: Stack<Char>) {
        val i = index*4 + 1
        val char = line[i]
        if (char != ' ') {
            stack.push(char)
        }
    }

    class Move(val amount: Int, val from: Int, val to: Int) {
        fun execute(stacks: List<Stack<Char>>) {
            for (c in 1..amount) {
                val elem = stacks[from-1].pop()
                stacks[to-1].push(elem)
            }
        }

        fun executePart2(stacks: List<Stack<Char>>) {
            (1..amount)
                .map { stacks[from-1].pop() }
                .reversed()
                .forEach{ stacks[to-1].push(it) }
        }
    }
}