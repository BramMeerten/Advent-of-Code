package be.brammeerten.y2024

import be.brammeerten.Co
import be.brammeerten.Co.Companion.DOWN
import be.brammeerten.Co.Companion.LEFT
import be.brammeerten.Co.Companion.RIGHT
import be.brammeerten.Co.Companion.UP
import be.brammeerten.readFile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class Day4Test {

    @Test
    fun `part 1`() {
        val board = readFile("2024/day4/exampleInput.txt")
        val boardSize = Co(board.size, board[0].length)

        var sum = 0;
        for (row in board.indices) {
            for (col in board[row].indices) {
                sum += hasXmas(Co(row, col), board, boardSize)
            }
        }
        assertThat(sum).isEqualTo(18);
//        assertThat(sum).isEqualTo(2454);
    }

    @Test
    fun `part 2`() {
        val board = readFile("2024/day4/exampleInput.txt")
        val boardSize = Co(board.size, board[0].length)

        var sum = 0;
        for (row in board.indices) {
            for (col in board[row].indices) {
                if (board[row][col] == 'A') {
                    val co = Co(row, col)
                    if (
                        (hasString(co + UP + LEFT, board, boardSize, DOWN + RIGHT, "MAS") || hasString(co + DOWN + RIGHT, board, boardSize, UP + LEFT, "MAS"))
                        &&
                        (hasString(co + UP + RIGHT, board, boardSize, DOWN + LEFT, "MAS") || hasString(co + DOWN + LEFT, board, boardSize, UP + RIGHT, "MAS"))
                    ) {
                        sum++;
                    }

                }
            }
        }
        assertThat(sum).isEqualTo(9);
//        assertThat(sum).isEqualTo(1858);
    }

    private fun hasXmas(co: Co, board: List<String>, boardSize: Co): Int {
        return listOf(
            LEFT, DOWN, RIGHT, UP,
            LEFT + UP, LEFT + DOWN,
            RIGHT + UP, RIGHT + DOWN,
        ).count { hasString(co, board, boardSize, it) };
    }

    private fun hasString(co: Co, board: List<String>, boardSize: Co, direction: Co, search: String = "XMAS"): Boolean {
        if (search.isEmpty()) {
            return true;
        } else if (co.col < 0 || co.col >= boardSize.col || co.row < 0 || co.row >= boardSize.row) {
            return false;
        } else if (board[co.row][co.col] != search[0]) {
            return false;
        }

        return hasString(co + direction, board, boardSize, direction, search.substring(1))
    }
}
