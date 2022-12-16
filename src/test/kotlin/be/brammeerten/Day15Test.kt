package be.brammeerten

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Day15Test {

    @Test
    fun `part 1a`() {
        val readings = parseSensorReadings(readFile("day15/exampleInput.txt"))
        Assertions.assertEquals(26, solve(readings, 10))
    }

    @Test
    fun `part 1b`() {
        val readings = parseSensorReadings(readFile("day15/input.txt"))
        Assertions.assertEquals(26, solve(readings, 2000000)) // < 4876694
    }

    @Test
    fun `part 2a`() {
        val readings = parseSensorReadings(readFile("day15/exampleInput.txt"))
        Assertions.assertEquals(C(14, 11), solve2c(readings, C(0,0), C(20, 20)))
    }

    @Test
    fun `part 2b`() {
        val readings = parseSensorReadings(readFile("day15/input.txt"))
        val yes = readings.all { canHavePointOutOfReach(it, C(2911363, 2855041), C(2911363, 2855041)) }
        Assertions.assertEquals(C(2911363, 2855041), solve2c(readings, C(0,0), C(4000000,4000000)))
        val result: Long = 2911363L * 4000000L + 2855041L
        println(yes.toString() + " " + result)
    }

    fun solve(readings: List<Reading>, row: Int): Int {
        val beacons = readings.filter { it.beacon.y == row }.map { it.beacon.x }.toHashSet()
        val add = hashSetOf<Int>()
        readings
            .filter { isInRange(it, row) }
            .forEach { getBlockingCols(it, row, add) }
//        return add.size // minus beacons
        return (add - beacons).size // minus beacons
    }

    fun solve2(readings: List<Reading>, maxCo: Int): C {
        val blocked = Array(maxCo) {Array(maxCo){false} }
        readings
//            .filter { isInArea(0, maxCo, it) }
            .forEach { reading ->
                if (reading.beacon.y in 0 until maxCo && reading.beacon.x in 0 until maxCo)
                    blocked[reading.beacon.y][reading.beacon.x] = true
                for (xOff in -reading.range..reading.range) {
                    val x = reading.sensor.x + xOff
                    if (x !in 0 until maxCo) continue
                    val left = reading.range - abs(xOff)
                    for (yOff in -left..left) {
                        val y = reading.sensor.y + yOff
                        if (y in 0 until maxCo)
                            blocked[y][x] = true
                    }
                }
            }

//        for (row in blocked) {
//            for (cell in row) {
//                print(if(cell) "X " else ". ")
//            }
//            println()
//        }

        for (y in blocked.indices) {
            for (x in blocked[y].indices) {
                if (!blocked[y][x]) return C(x, y)
            }
        }
        throw IllegalStateException("All blcoked!")
    }

    fun solve2b(readings: List<Reading>, maxCo: Int): C {
        val notBlocked = Array(maxCo) { listOf(0..maxCo) }
        readings
//            .filter { isInArea(0, maxCo, it) }
            .forEach { reading ->
                println("processing sensor ${reading.range}")
                if (reading.beacon.y in 0 until maxCo && reading.beacon.x in 0 until maxCo)
                    notBlocked[reading.beacon.y] = notBlocked[reading.beacon.y].flatMap { it.split(reading.beacon.x..reading.beacon.x)}.filter { !it.isEmpty() }
                for (xOff in 0..reading.range) {
                    println("$xOff / ${reading.range}")
                    val rangeX = max(reading.sensor.x - xOff, 0)..min(reading.sensor.x+xOff, maxCo)
                    if (rangeX.isEmpty()) continue
                    val left = reading.range - abs(xOff)
                    val range = max(reading.sensor.y-left, 0)..min(reading.sensor.y+left, maxCo-1)
                    for (y in range) {
                            notBlocked[y] = notBlocked[y].flatMap {it.split(rangeX) }.filter { !it.isEmpty() }
                    }
                }
            }

//        for (y in 0 until maxCo) {
//            val dinges = notBlocked[y].flatMap { s -> s.toList() }.sorted()
//            for (x in 0 until maxCo) {
//                if (dinges.find { it == x } != null)
//                    print(". ")
//                else
//                    print("X ")
//            }
//            println()
//        }

        println("looking for unblocked")
        for (y in notBlocked.indices) {
            notBlocked[y].forEach {
                return C(it.first, y)
            }
        }
        throw IllegalStateException("All blcoked!")
    }

    fun solve2c(readings: List<Reading>, topLeft: C, bottomRight: C): C? {
        val w = (bottomRight - topLeft).x+1
        val h = (bottomRight - topLeft).y+1

        if (w==1 && h==1) {
            println("Solution: ${topLeft.x}, ${topLeft.y}")
            return topLeft
        }

//        println("${w+1} x ${h+1}")
        val diff = bottomRight - topLeft
        val mid = topLeft + C(diff.x/2, diff.y/2)
        val parts: Array<Pair<C, C>> = arrayOf(
            topLeft to mid,
            C(mid.x+1, topLeft.y) to C(bottomRight.x, mid.y),
            C(topLeft.x, mid.y+1) to C(mid.x, bottomRight.y),
            C(mid.x+1, mid.y+1) to bottomRight
        )

        val solution =  parts
            .filter { (start, end) -> start.x <= end.x && start.y <= end.y }
            .filter { (start, end) -> readings.all { canHavePointOutOfReach(it, start, end) } }
            .map { (start, end) -> solve2c(readings, start, end) }.filterNotNull()
        if (solution.size > 1)
            throw IllegalStateException("Multiple solutions")
        return if (solution.size == 1) solution[0] else null
    }

}

fun canHavePointOutOfReach(reading: Reading, start: C, end: C): Boolean {
    val corners = arrayOf(
        start, C(end.x, start.y), C(start.x, end.y), end)
    return corners.map { distance(it, reading.sensor) }.max() > reading.range
}

fun isInArea(start: Int, end: Int, reading: Reading): Boolean {
    val sensor = reading.sensor
    return (sensor.x + reading.range >= start || sensor.x - reading.range <= end)
            && (sensor.y + reading.range >= start || sensor.y - reading.range <= end)
}

fun isInRange(reading: Reading, row: Int): Boolean {
    val range = distance(reading.beacon, reading.sensor)
    return abs(row - reading.sensor.y) <= range
}

fun getBlockingCols(reading: Reading, y: Int, found: HashSet<Int>) {
    val range = distance(reading.beacon, reading.sensor)
    val xDiffLeftOver = range - abs(y - reading.sensor.y)
    (-xDiffLeftOver.. xDiffLeftOver)
        .map { C(reading.sensor.x+it, y) }
        .filter { distance(reading.sensor, it) <= range }
        .forEach() { found.add(it.x) }
}

fun distance(start: C, end: C): Int {
    val diff = start - end
    return abs(diff.x) + abs(diff.y)
}

fun parseSensorReadings(lines: List<String>): List<Reading> {
    return lines.map { parseSensorReading(it) }
}

fun parseSensorReading(line: String): Reading {
    var matches = extractRegexGroupsI("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)", line)
    return Reading(C(matches[0], matches[1]), C(matches[2], matches[3]))
}

data class Reading(val sensor: C, val beacon: C) {
    val range = distance(beacon, sensor)
}

fun IntRange.split(range: IntRange): List<IntRange> {
    if (range.first > this.first && range.last >= this.last) {
        return listOf(this.first..(Math.min(range.first-1, this.last)))
    }
    if (range.first<= this.first && range.last < this.last) {
        return listOf(Math.max(range.last+1, this.first)..this.last)
    }
    if (range.first<=this.first && range.last>= this.last)
        return emptyList()

    return listOf(
        this.first until range.first,
        range.last+1..this.last
    )
}