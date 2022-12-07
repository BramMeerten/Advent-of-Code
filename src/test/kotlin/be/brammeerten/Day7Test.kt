package be.brammeerten

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.math.min

class Day7Test {

    @Test
    fun `part 1`() {
        val root = readDirectory()

        Assertions.assertEquals(95437, root.getLimitedSize(100000))
    }

    @Test
    fun `part 2`() {
        val root = readDirectory()
        val extraSpaceNeeded = 30000000 - (70000000 - root.totalSize())

        Assertions.assertEquals(24933642, root.findSmallestDirectoryWithMinimumSpace(extraSpaceNeeded))
    }

    private fun readDirectory(): Dir {
        var dir: Dir? = null
        var root: Dir? = null

        val lines = readFile("day7/exampleInput.txt")
        val regex = Regex("^(\\d+) (.+)$")

        for (line in lines) {
            if (line.startsWith("$ cd ..")) {
                dir = dir!!.parent
            } else if (line.startsWith("$ cd ")) {
                if (dir == null) {
                    dir = Dir(line.substring("$ cd ".length))
                    root = dir
                } else {
                    dir = dir.addDir(line.substring("$ cd ".length))
                }
            } else if (!line.startsWith("$ ls")) {
                val result = regex.find(line)
                if (result != null) dir!!.addFile(result.groupValues[2], result.groupValues[1].toInt())
            }
        }
        return root!!
    }

    class Dir(
        val name: String, val parent: Dir? = null,
        val subDirs: HashMap<String, Dir> = HashMap(),
        var files: HashMap<String, Int> = HashMap()
    ) {

        fun addDir(name: String): Dir {
            val newDir = Dir(name, this)
            return subDirs.putIfAbsent(name, newDir) ?: newDir
        }

        fun addFile(name: String, value: Int) {
            files.putIfAbsent(name, value)
        }

        fun totalSize(): Int {
            return files.values.sum() + subDirs.map { it.value.totalSize() }.sum()
        }

        fun getLimitedSize(limit: Int): Int {
            val total = totalSize()
            var value = if (total < limit) total else 0

            for (subDir in subDirs.values) {
                value += subDir.getLimitedSize(limit)
            }

            return value
        }

        fun findSmallestDirectoryWithMinimumSpace(minSpace: Int, maxSpace: Int = Int.MAX_VALUE): Int? {
            val total = totalSize()
            if (total < minSpace)
                return null

            val smallest = min(total, maxSpace)
            return min(
                subDirs.values
                    .mapNotNull { it.findSmallestDirectoryWithMinimumSpace(minSpace, smallest) }
                    .minOrNull() ?: Int.MAX_VALUE,
                smallest)
        }
    }
}