package be.brammeerten

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class Day8Test {

    @Test
    fun `part 1`() {
        val forest = toMap(readFile("day8/exampleInput.txt"))
        Assertions.assertEquals(21, forest.getVisibleTrees().size)
    }

    @Test
    fun `part 2`() {
        val forest = toMap(readFile("day8/exampleInput.txt"))
        Assertions.assertEquals(4, forest.getScenicScore(2, 1))
        Assertions.assertEquals(8, forest.getScenicScore(2, 3))
        Assertions.assertEquals(8, forest.getBestScenicScore())
    }

    fun toMap(lines: List<String>) =
        ForestMap(lines.map { it.toCharArray().map { tree -> tree.digitToInt() } })

    class ForestMap(val trees: List<List<Int>>) {
        val width = trees[0].size
        val height = trees.size

        fun getVisibleTrees(): Set<Tree> {
            val visible = HashSet<Tree>()
            for (row in 0 until height) {
                var heighest = Int.MIN_VALUE
                for (col in 0 until width) {
                    if (trees[row][col] > heighest) {
                        visible.add(Tree(col, row, trees[row][col]))
                        heighest = trees[row][col]
                    }
                }
            }

            for (row in 0 until height) {
                var heighest = Int.MIN_VALUE
                for (col in width-1 downTo 0) {
                    if (trees[row][col] > heighest) {
                        visible.add(Tree(col, row, trees[row][col]))
                        heighest = trees[row][col]
                    }
                }
            }

            for (col in 0 until width) {
                var heighest = Int.MIN_VALUE
                for (row in 0 until height) {
                    if (trees[row][col] > heighest) {
                        visible.add(Tree(col, row, trees[row][col]))
                        heighest = trees[row][col]
                    }
                }
            }

            for (col in 0 until width) {
                var heighest = Int.MIN_VALUE
                for (row in height-1 downTo  0) {
                    if (trees[row][col] > heighest) {
                        visible.add(Tree(col, row, trees[row][col]))
                        heighest = trees[row][col]
                    }
                }
            }
            return visible
        }

        fun getBestScenicScore(): Int {
            var max = -1
            for (row in trees.indices) {
                for (col in trees[0].indices) {
                    max = Math.max(max, getScenicScore(col, row))
                }
            }
            return max
        }

        fun getScenicScore(x: Int, y: Int): Int {
            var score = 1

            // To right
            var count = 0
            for (col in x+1 until width) {
                count++
                if (trees[y][col] >= trees[y][x])
                    break
            }
            score *= count

            // To left
            count = 0
            for (col in x-1 downTo 0) {
                count++
                if (trees[y][col] >= trees[y][x])
                    break
            }
            score *= count

            // Down
            count = 0
            for (row in y+1 until height) {
                count++
                if (trees[row][x] >= trees[y][x])
                    break
            }
            score *= count

            // Up
            count = 0
            for (row in y-1 downTo 0) {
                count++
                if (trees[row][x] >= trees[y][x])
                    break
            }

            return score * count
        }
    }

    data class Tree(val x: Int, val y: Int, val height: Int)
}