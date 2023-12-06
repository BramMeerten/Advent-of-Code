package be.brammeerten.y2023

import be.brammeerten.extractRegexGroupsL
import be.brammeerten.rangeOverlap
import be.brammeerten.readFileSplitted
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class Day5Test {

    @Test
    fun `part 1`() {
        val inputBlocks = readFileSplitted("2023/day5/exampleInput.txt", "\n\n")
        var seeds: List<Long> = emptyList()
        val regex = Regex("^(?<from>.+)-to-(?<to>.+) map:$")
        val maps: MutableMap<String, Pair<String, MutableList<Mapping>>> = mutableMapOf();
        for (block in inputBlocks) {
            val match = regex.find(block[0]);
            if (block[0].startsWith("seeds: ")) {
                seeds = block[0].substring("seeds: ".length)
                    .split(" ").map { it.toLong() };

            } else if (match != null) {
                val from = match.groups["from"]?.value!!;
                val to = match.groups["to"]?.value!!;
                maps[from] = to to mutableListOf()
                for (i in 1 until block.size) {
                    val vals = extractRegexGroupsL("^([0-9]+) ([0-9]+) ([0-9]+)$", block[i])
                    maps[from]!!.second.add(Mapping(vals[0], vals[1], vals[2]))
                }
            }
        }

        var name = "seed";
        var source = seeds;
        while (true) {
            val newName = maps[name]!!.first
            val newSource = mutableListOf<Long>()
            for (srcNum in source) {
                var found = false;
                for (mapping in maps[name]!!.second) {
                    if (srcNum >= mapping.sourceRangeStart && srcNum <= mapping.sourceRangeStart + mapping.rangeLength) {
                        val offset = srcNum - mapping.sourceRangeStart
                        newSource.add(mapping.destinationRangeStart + offset)
                        found = true
                        break;
                    }
                }
                if (!found) {
                    newSource.add(srcNum)
                }
            }

            name = newName
            source = newSource
            if (newName.equals("location")) {
                break;
            }
        }

        assertThat(source.min()).isEqualTo(35);
//         assertThat(source.min()).isEqualTo(107430936);
    }

    @Test
    fun `part 2`() {
        val inputBlocks = readFileSplitted("2023/day5/exampleInput.txt", "\n\n")
        var seeds: List<LongRange> = emptyList()
        val regex = Regex("^(?<from>.+)-to-(?<to>.+) map:$")
        val maps: MutableMap<String, Pair<String, MutableList<Mapping2>>> = mutableMapOf();
        for (block in inputBlocks) {
            val match = regex.find(block[0]);
            if (block[0].startsWith("seeds: ")) {
                seeds = block[0].substring("seeds: ".length)
                    .split(" ").map { it.toLong() }.chunked(2).map { (start, length) ->
                        (start until (start + length))
                    }

            } else if (match != null) {
                val from = match.groups["from"]?.value!!;
                val to = match.groups["to"]?.value!!;
                maps[from] = to to mutableListOf()
                for (i in 1 until block.size) {
                    val vals = extractRegexGroupsL("^([0-9]+) ([0-9]+) ([0-9]+)$", block[i])
                    maps[from]!!.second.add(
                        Mapping2(
                            (vals[0] until vals[0] + vals[2]),
                            vals[1] until (vals[1] + vals[2])
                        )
                    )
                }
            }
        }

        var name = "seed";
        var source = seeds;
        while (true) {
            val newName = maps[name]!!.first
            val newSource = mutableListOf<LongRange>()
            for (curRange in source) {
                val unmapped = mutableListOf(curRange)
                val mapped = mutableListOf<LongRange>()
                for (mapping in maps[name]!!.second) {
                    val toIter = mutableListOf<LongRange>()
                    toIter.addAll(unmapped)
                    for (unmappedRange in toIter) {
                        val overlap = rangeOverlap(mapping.sourceRange, unmappedRange)
                        if (overlap.overlap != null) {
                            val offset = overlap.overlap!!.first - mapping.sourceRange.first
                            val mappedOverlap = (mapping.destinationRange.first + offset)..(mapping.destinationRange.first + offset + (overlap.overlap!!.last - overlap.overlap!!.first))
                            mapped.add(mappedOverlap)
                            unmapped.remove(unmappedRange)
                            unmapped.addAll(overlap.remaindersRange2)
                        }
                    }
                }
                newSource.addAll(mapped);
                newSource.addAll(unmapped);
            }

            name = newName
            source = newSource
            if (newName == "location") {
                break;
            }
        }

        val sum = source.minOfOrNull { it.first }

        assertThat(sum).isEqualTo(46);
//        assertThat(sum).isEqualTo(23738616);
    }

    data class Mapping(val destinationRangeStart: Long, val sourceRangeStart: Long, val rangeLength: Long)

    data class Mapping2(val destinationRange: LongRange, val sourceRange: LongRange)
}