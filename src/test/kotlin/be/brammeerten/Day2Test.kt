package be.brammeerten

import be.brammeerten.Day2Test.Shape.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class Day2Test {

    private val scores = mapOf(
            ROCK to Score(SCISSORS, ROCK, PAPER),
            PAPER to Score(ROCK, PAPER, SCISSORS),
            SCISSORS to Score(PAPER, SCISSORS, ROCK)
    )

    @Test
    fun `part 1`() {
        val lines = readFile("day2/exampleInput.txt")
        val result = lines
                .map { it.split(" ") }
                .map { (a, b) -> mapABC(a) to mapXYZ(b) }
                .sumOf { (a, b) -> wonScore(a, b) + shapeScore(b) }
        assertEquals(15, result)
    }

    @Test
    fun `part 2`() {
        val lines = readFile("day2/exampleInput.txt")
        val result = lines
                .map { it.split(" ") }
                .map { (a, b) -> mapABC(a) to chooseShapePart2(b, mapABC(a)) }
                .sumOf { (a, b) -> wonScore(a, b) + shapeScore(b) }
        assertEquals(12, result)
    }

    private fun wonScore(opponent: Shape, me: Shape): Int {
        return scores[me]!!.score(opponent)
    }

    private fun shapeScore(chosenShape: Shape) = mapOf(ROCK to 1, PAPER to 2, SCISSORS to 3)[chosenShape]!!

    private fun mapXYZ(value: String) = mapOf("X" to ROCK, "Y" to PAPER, "Z" to SCISSORS)[value]!!

    private fun mapABC(value: String) = mapOf("A" to ROCK, "B" to PAPER, "C" to SCISSORS)[value]!!

    private fun chooseShapePart2(what: String, value: Shape): Shape {
        return when (what) {
            "X" -> scores[value]!!.winsFrom // lose
            "Y" -> scores[value]!!.drawsFrom // draw
            else -> scores[value]!!.losesFrom // from
        }
    }

    enum class Shape {
        ROCK, PAPER, SCISSORS
    }

    class Score(val winsFrom: Shape, val drawsFrom: Shape, val losesFrom: Shape) {
        fun score(other: Shape): Int {
            if (this.winsFrom == other) return 6
            if (this.losesFrom == other) return 0
            return 3
        }
    }
}