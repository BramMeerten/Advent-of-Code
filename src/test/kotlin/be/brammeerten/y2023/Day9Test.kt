package be.brammeerten.y2023

import be.brammeerten.extractRegexGroupsL
import be.brammeerten.rangeOverlap
import be.brammeerten.readFile
import be.brammeerten.readFileSplitted
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.math.abs


class Day9Test {

    @Test
    fun `part 1`() {
        val histories = readFile("2023/day9/exampleInput.txt")
        val sum = histories
            .map { it.split(" ").map { num -> num.toInt() } }
            .map { solvePt1(it) }
            .sum()

        assertThat(sum).isEqualTo(114);
//         assertThat(sum).isEqualTo(1806615041);
    }

    @Test
    fun `part 2`() {
        val histories = readFile("2023/day9/input.txt")
        val sum = histories
            .map { it.split(" ").map { num -> num.toInt() } }
            .map { solvePt2(it) }
            .sum()

        assertThat(sum).isEqualTo(2);
//         assertThat(sum).isEqualTo(1806615041);
    }

    private fun solvePt1(originalHistory: List<Int>): Int {
        val history = mutableListOf<MutableList<Int>>()
        val orMutable = mutableListOf<Int>()
        orMutable.addAll(originalHistory)
        history.add(orMutable)

        while (true) {
            history.add(history.last()
                .zipWithNext { a, b -> (b-a) }
                .toMutableList())

            if (!history.last().any { it != 0 }) break;
        }

        history.reverse()
        val x = history.fold(0) { acc, ints ->
            acc + ints.last()
        }

        println(x)

        return x
    }

    private fun solvePt2(originalHistory: List<Int>): Int {
        val history = mutableListOf<MutableList<Int>>()
        val orMutable = mutableListOf<Int>()
        orMutable.addAll(originalHistory)
        history.add(orMutable)

        while (true) {
            history.add(history.last()
                .zipWithNext { a, b -> (b-a) }
                .toMutableList())

            if (!history.last().any { it != 0 }) break;
        }

        history.reverse()
        val x = history.fold(0) { acc, ints ->
//            println(ints.first() - acc)
            ints.first() - acc
        }

//        println(x)
//        println("--")

        return x
    }


}