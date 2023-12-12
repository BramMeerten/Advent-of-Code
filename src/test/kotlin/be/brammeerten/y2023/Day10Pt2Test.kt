package be.brammeerten.y2023

import be.brammeerten.C
import be.brammeerten.readFile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class Day10Pt2Test {

    @Test
    fun `part 1`() {
//        val map = PipeMap(readFile("2023/day10/exampleInput3.txt"))
//        val map = PipeMap(readFile("2023/day10/exampleInput4.txt"))
//        val map = PipeMap(readFile("2023/day10/exampleInput5.txt"))
        val map = PipeMap(readFile("2023/day10/input.txt"))
        val distances = listOfNotNull(
            map.getPolygonNonStack(map.start + C.DOWN),
            map.getPolygonNonStack(map.start + C.UP),
            map.getPolygonNonStack(map.start + C.RIGHT),
            map.getPolygonNonStack(map.start + C.LEFT),
        )
//        if (distances.size != 2) throw RuntimeException("Not expected")
        val polygon = distances[1]
        val newPoly = toPolygon(polygon)
        val bounding = getBounding(polygon)

        var inside = 0
        for (row in bounding.first.y .. bounding.second.y) {
            for (col in bounding.first.x .. bounding.second.x) {
                var x = col
                var y = row
                if (polygon.contains(C(col, row))) continue

                var count = 0
                while (x <= bounding.second.x && y <= bounding.second.y) {
                    val partOfPoly = polygon.contains(C(x, y))
                    if (map.isStartWall(C(x, y)) && partOfPoly) {
                        count++
                    }
                    x++
                    y++
                }
                if (count % 2 == 1)
                    inside++
            }
//            println("$row: $count")
        }

//        assertThat(inside).isEqualTo(4);
//         assertThat(inside).isEqualTo(8);
//         assertThat(inside).isEqualTo(10);
        assertThat(inside).isEqualTo(285); // 217 = too low, 298 = too high
    }

    private fun toPolygon(polygon: List<C>): MutableList<C> {
        val newPoly = mutableListOf<C>()
        for (i in polygon.indices) {
            if (i + 1 == polygon.size) {
                newPoly.add(polygon[i])
                break;
            }

            val prev = if (i == 0) polygon.last() else polygon[i - 1]
            val next = polygon[i + 1]
            val cur = polygon[i]

            if (prev.x == cur.x && next.x == cur.x) continue
            if (prev.y == cur.y && next.y == cur.y) continue
            newPoly.add(cur)
        }
        return newPoly
    }

    private fun getBounding(polygon: List<C>): Pair<C, C> {
        var min = C(Int.MAX_VALUE, Int.MAX_VALUE)
        var max = C(Int.MIN_VALUE, Int.MIN_VALUE)

        for (p in polygon) {
            min = C(Math.min(p.x, min.x), Math.min(p.y, min.y))
            max = C(Math.max(p.x, max.x), Math.max(p.y, max.y))
        }

        return min to max
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

        fun isStartWall(c: C): Boolean {
            val value = get(c)!!
//            return value == '|'/* || value == 'L' || value == 'F'*/
//            return value == '|' || value == 'L' || value == 'J'
            return value != '7' && value != 'L'
        }

        fun isEndWall(c: C): Boolean {
            val value = get(c)!!
//            return value == '|' || value == 'J' || value == '7'
            return value == '|' || value == 'L' || value == 'J'
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

        fun getPolygonNonStack(nnnn: C): List<C>? {
            var cur = start
            var next = nnnn
            val map = mutableListOf<C>()

            while (true) {
                val nextVal = get(next)
                map.add(next)

                if (nextVal == 'S') return map

                if (nextVal == UP_DOWN && cur != next + C.DOWN) {
                    cur = next; next += C.DOWN; continue;
                } else if (nextVal == UP_DOWN) {
                    cur = next; next += C.UP; continue;
                }

                if (nextVal == LEFT_RIGHT && cur != next + C.LEFT) {
                    cur = next; next += C.LEFT; continue;
                } else if (nextVal == LEFT_RIGHT) {
                    cur = next; next += C.RIGHT; continue;
                }

                if (nextVal == UP_RIGHT && cur != next + C.UP) {
                    cur = next; next += C.UP; continue;
                } else if (nextVal == UP_RIGHT) {
                    cur = next; next += C.RIGHT; continue;
                }

                if (nextVal == UP_LEFT && cur != next + C.UP) {
                    cur = next; next += C.UP; continue;
                } else if (nextVal == UP_LEFT) {
                    cur = next; next += C.LEFT; continue;
                }

                if (nextVal == LEFT_DOWN && cur != next + C.LEFT) {
                    cur = next; next += C.LEFT; continue;
                } else if (nextVal == LEFT_DOWN) {
                    cur = next; next += C.DOWN; continue;
                }

                if (nextVal == RIGHT_DOWN && cur != next + C.RIGHT) {
                    cur = next; next += C.RIGHT; continue;
                } else if (nextVal == RIGHT_DOWN) {
                    cur = next; next += C.DOWN; continue;
                }

                return null
            }
        }
    }
}