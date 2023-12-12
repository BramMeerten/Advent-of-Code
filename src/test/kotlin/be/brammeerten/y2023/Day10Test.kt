package be.brammeerten.y2023

import be.brammeerten.C
import be.brammeerten.readFile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.math.min


class Day10Test {

    @Test
    fun `part 1`() {
//        val map = PipeMap(readFile("2023/day10/exampleInput.txt"))
//        val map = PipeMap(readFile("2023/day10/exampleInput2.txt"))
        val map = PipeMap(readFile("2023/day10/input.txt"))
        val distances = listOfNotNull(
            map.getDistanceMapNonStack(map.start + C.DOWN),
            map.getDistanceMapNonStack(map.start + C.UP),
            map.getDistanceMapNonStack(map.start + C.RIGHT),
            map.getDistanceMapNonStack(map.start + C.LEFT),
        )
//        if (distances.size != 2) throw RuntimeException("Not expected")

        var max = -1
        for (key in distances[1].keys) {
            val num = min(distances[1][key]!!, distances[3][key]!!)
            if (num > max) max = num
        }

//        assertThat(max).isEqualTo(4);
//         assertThat(max).isEqualTo(8);
        assertThat(max).isEqualTo(7030);
    }


    class PipeMap(val rows: List<String>) {
        val UP_DOWN = '|'
        val LEFT_RIGHT = '-'
        val UP_RIGHT = 'L'
        val UP_LEFT = 'J'
        val LEFT_DOWN = '7'
        val RIGHT_DOWN = 'F'


        val start: C;
        val w: Int = rows[0].length;
        val h: Int = rows.size

        init {
            var s: C? = null;
            for (y in 0 until h) {
                for (x in 0 until w) {
                    if (rows[y][x] == 'S') {
                        s = C(x, y);
                        break;
                    }
                }
            }
            if (s != null) start = s else throw RuntimeException("No start")
        }

        fun getSurrounding(): List<C> {
            val surrounding = mutableListOf<C>()

            val down = get(start + C.DOWN)
            if (down == UP_DOWN || down == UP_LEFT || down == UP_RIGHT)
                surrounding.add(start + C.DOWN)

            val left = get(start + C.LEFT)
            if (left == RIGHT_DOWN || left == UP_RIGHT || left == LEFT_RIGHT)
                surrounding.add(start + C.LEFT)

            val right = get(start + C.RIGHT)
            if (right == LEFT_RIGHT || right == LEFT_DOWN || right == UP_LEFT)
                surrounding.add(start + C.RIGHT)

            val up = get(start + C.UP)
            if (up == UP_DOWN || up == RIGHT_DOWN || up == LEFT_DOWN)
                surrounding.add(start + C.UP)

            return surrounding
        }

        fun get(c: C): Char? {
            if (c.y < rows.size && c.x < rows[0].length && c.y >= 0 && c.x >= 0)
                return rows[c.y][c.x]
            else return null
        }

        fun getDistanceMap(cur: C, next: C, curVal: Int = 0, map: MutableMap<C, Int> = mutableMapOf()): Map<C, Int>? {
            val nextVal = get(next)

            if (nextVal == 'S') return map
            map[next] = curVal + 1

            if (nextVal == UP_DOWN && cur != next + C.DOWN)
                return getDistanceMap(next, next + C.DOWN, curVal + 1, map)
            else if (nextVal == UP_DOWN)
                return getDistanceMap(next, next + C.UP, curVal + 1, map)

            if (nextVal == LEFT_RIGHT && cur != next + C.LEFT)
                return getDistanceMap(next, next + C.LEFT, curVal + 1, map)
            else if (nextVal == LEFT_RIGHT)
                return getDistanceMap(next, next + C.RIGHT, curVal + 1, map)

            if (nextVal == UP_RIGHT && cur != next + C.UP)
                return getDistanceMap(next, next + C.UP, curVal + 1, map)
            else if (nextVal == UP_RIGHT)
                return getDistanceMap(next, next + C.RIGHT, curVal + 1, map)

            if (nextVal == UP_LEFT && cur != next + C.UP)
                return getDistanceMap(next, next + C.UP, curVal + 1, map)
            else if (nextVal == UP_LEFT)
                return getDistanceMap(next, next + C.LEFT, curVal + 1, map)

            if (nextVal == LEFT_DOWN && cur != next + C.LEFT)
                return getDistanceMap(next, next + C.LEFT, curVal + 1, map)
            else if (nextVal == LEFT_DOWN)
                return getDistanceMap(next, next + C.DOWN, curVal + 1, map)

            if (nextVal == RIGHT_DOWN && cur != next + C.RIGHT)
                return getDistanceMap(next, next + C.RIGHT, curVal + 1, map)
            else if (nextVal == RIGHT_DOWN)
                return getDistanceMap(next, next + C.DOWN, curVal + 1, map)

            return null
        }

        fun getDistanceMapNonStack(nnnn: C): Map<C, Int>? {
            var cur = start
            var curVal = 0
            var next = nnnn
            var map: MutableMap<C, Int> = mutableMapOf()

            while (true) {
                val nextVal = get(next)

                if (nextVal == 'S') return map
                map[next] = curVal + 1

                if (nextVal == UP_DOWN && cur != next + C.DOWN) {
                    cur = next; next += C.DOWN; curVal++; continue;
                } else if (nextVal == UP_DOWN) {
                    cur = next; next += C.UP; curVal++; continue;
                }

                if (nextVal == LEFT_RIGHT && cur != next + C.LEFT) {
                    cur = next; next += C.LEFT; curVal++; continue;
                } else if (nextVal == LEFT_RIGHT) {
                    cur = next; next += C.RIGHT; curVal++; continue;
                }

                if (nextVal == UP_RIGHT && cur != next + C.UP) {
                    cur = next; next += C.UP; curVal++; continue;
                } else if (nextVal == UP_RIGHT) {
                    cur = next; next += C.RIGHT; curVal++; continue;
                }

                if (nextVal == UP_LEFT && cur != next + C.UP) {
                    cur = next; next += C.UP; curVal++; continue;
                } else if (nextVal == UP_LEFT) {
                    cur = next; next += C.LEFT; curVal++; continue;
                }

                if (nextVal == LEFT_DOWN && cur != next + C.LEFT) {
                    cur = next; next += C.LEFT; curVal++; continue;
                } else if (nextVal == LEFT_DOWN) {
                    cur = next; next += C.DOWN; curVal++; continue;
                }

                if (nextVal == RIGHT_DOWN && cur != next + C.RIGHT) {
                    cur = next; next += C.RIGHT; curVal++; continue;
                } else if (nextVal == RIGHT_DOWN) {
                    cur = next; next += C.DOWN; curVal++; continue;
                }

                return null
            }
        }
    }
}