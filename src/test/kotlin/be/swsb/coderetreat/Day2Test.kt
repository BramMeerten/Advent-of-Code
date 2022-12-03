package be.swsb.coderetreat

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import readFile


class Day2Test {

    val rockScore = Score(Shape.SCISSORS, Shape.ROCK, Shape.PAPER)
    val paperScore = Score(Shape.ROCK, Shape.PAPER, Shape.SCISSORS)
    val scissorsScore = Score(Shape.PAPER, Shape.SCISSORS, Shape.ROCK)

    @Test
    fun `part 1`() {
        val lines = readFile("day2/exampleInput.txt");
        val result = lines
                .map { line -> Pair(line.split(" ")[0], line.split(" ")[1]) }
                .map { game -> Pair(mapABC(game.first), mapXYZ(game.second)) }
                .sumOf { game -> wonScore(game) + shapeScore(game.second) };
        assertEquals(15, result);
    }

    @Test
    fun `part 2`() {
        val lines = readFile("day2/input.txt");
        val result = lines
                .map { line -> Pair(line.split(" ")[0], line.split(" ")[1]) }
                .map { game -> Pair(mapABC(game.first), chooseShapePart2(game.second, mapABC(game.first))) }
                .sumOf { game -> wonScore(game) + shapeScore(game.second) };
        assertEquals(12, result);
    }

    fun wonScore(game: Pair<Shape, Shape>): Int {
        return when (game.second) {
            Shape.ROCK -> rockScore.score(game.first)
            Shape.PAPER -> paperScore.score(game.first)
            else -> scissorsScore.score(game.first)
        }
    }

    fun shapeScore(chosenShape: Shape): Int {
        return when (chosenShape) {
            Shape.ROCK -> 1
            Shape.PAPER -> 2
            else -> 3
        }
    }

    class Score(val winFrom: Shape, val drawFrom: Shape, val loseFrom: Shape) {
        fun score(other: Shape): Int {
            if (this.winFrom == other) return 6
            if (this.loseFrom == other) return 0
            return 3
        }
    }

    fun mapXYZ(value: String): Shape {
        return when (value) {
            "X" -> Shape.ROCK
            "Y" -> Shape.PAPER
            else -> Shape.SCISSORS
        }
    }

    fun chooseShapePart2(what: String, value: Shape): Shape {
        if (what == "X") { // lose
            return when (value) {
                Shape.ROCK -> rockScore.winFrom
                Shape.PAPER -> paperScore.winFrom
                else -> scissorsScore.winFrom
            }
        }
        else if (what == "Y") { // draw
            return when (value) {
                Shape.ROCK -> rockScore.drawFrom
                Shape.PAPER -> paperScore.drawFrom
                else -> scissorsScore.drawFrom
            }
        }

        else {// win
            return when (value) {
                Shape.ROCK -> rockScore.loseFrom
                Shape.PAPER -> paperScore.loseFrom
                else -> scissorsScore.loseFrom
            }
        }
    }

    fun mapABC(value: String): Shape {
        return when (value) {
            "A" -> Shape.ROCK
            "B" -> Shape.PAPER
            else -> Shape.SCISSORS
        }
    }

    enum class Shape {
        ROCK, PAPER, SCISSORS
    }
}