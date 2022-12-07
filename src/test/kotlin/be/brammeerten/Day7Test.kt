package be.brammeerten

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.math.min

class Day7Test {

    @Test
    fun `part 1`() {
        val root1 = readDirectory("day7/exampleInput.txt")
        Assertions.assertEquals(95437, root1.getLimitedSize(100000))
    }

    @Test
    fun `part 2`() {
        val root = readDirectory("day7/exampleInput.txt")
        val extraSpaceNeeded = 30000000 - (70000000 - root.totalSize())
        Assertions.assertEquals(24933642, root.findSmallestDirectoryWithMinimumSpace(extraSpaceNeeded))
    }

    private fun readDirectory(file: String): Dir {
        val root = Dir("/")
        val regex = Regex("^(\\d+) (.+)$")
        val lines = readFile(file)

        var dir = root
        for (line in lines.drop(1)) {
            if (line.startsWith("$ cd ..")) {
                dir = dir.parent!!
            } else if (line.startsWith("$ cd ")) {
                dir = dir.addDir(line.drop("$ cd ".length))
            } else if (regex.matches(line)) {
                val result = regex.find(line)
                dir.addFile(result!!.groupValues[2], result.groupValues[1].toInt())
            }
        }
        return root
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
            return files.values.sum() + subDirs.values.sumOf { it.totalSize() }
        }

        fun getLimitedSize(limit: Int): Int {
            val total = totalSize()
            val value = if (total < limit) total else 0
            return value + subDirs.values.sumOf { it.getLimitedSize(limit) }
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