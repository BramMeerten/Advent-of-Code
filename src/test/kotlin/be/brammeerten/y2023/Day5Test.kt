package be.brammeerten.y2023

import be.brammeerten.extractRegexGroups
import be.brammeerten.extractRegexGroupsI
import be.brammeerten.extractRegexGroupsL
import be.brammeerten.readFileSplitted
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.lang.RuntimeException
import kotlin.math.min
import kotlin.math.pow


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
                val from =  match.groups["from"]?.value!!;
                val to =  match.groups["to"]?.value!!;
                maps[from] = to to mutableListOf()
                for (i in 1 until block.size) {
                    val vals = extractRegexGroupsL("^([0-9]+) ([0-9]+) ([0-9]+)$", block[i])
                    maps[from]!!.second.add(Mapping(vals[0], vals[1], vals[2]))
                }
            }
        }

        var name = "seed";
        var source = seeds;
        while(true) {
            var newName = maps[name]!!.first
            var newSource = mutableListOf<Long>()
            for (srcNum in source) {
                var found = false;
                for (mapping in maps[name]!!.second) {
                    if (srcNum >= mapping.sourceRangeStart && srcNum <= mapping.sourceRangeStart + mapping.rangeLength) {
                        var offset = srcNum - mapping.sourceRangeStart
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
        println(maps);

        assertThat(source.min()).isEqualTo(35);
//         assertThat(sum).isEqualTo(107430936);
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
                        (start..(start+length))
                    }

            } else if (match != null) {
                val from =  match.groups["from"]?.value!!;
                val to =  match.groups["to"]?.value!!;
                maps[from] = to to mutableListOf()
                for (i in 1 until block.size) {
                    val vals = extractRegexGroupsL("^([0-9]+) ([0-9]+) ([0-9]+)$", block[i])
                    maps[from]!!.second.add(Mapping2((vals[0]..vals[0]+vals[2]), vals[1]..(vals[1]+vals[2])))
                }
            }
        }

        var name = "seed";
        var source = seeds;
        while(true) {
            var newName = maps[name]!!.first
            var newSource = mutableListOf<LongRange>()
            for (srcNum in source) {
                var unmapped = mutableListOf<LongRange>(srcNum)
                var mapped = mutableListOf<LongRange>()
                for (mapping in maps[name]!!.second) {
                    var toIter = mutableListOf<LongRange>()
                    toIter.addAll(unmapped)
                    for (srcNum2 in toIter) {
                        if (mapping.sourceRange.contains(srcNum2.first)) {
                            val contains = srcNum2.first..min(mapping.sourceRange.last, srcNum2.last)
                            val offset = contains.first - mapping.sourceRange.first
                            val gevonden = (mapping.destinationRange.first + offset)..(mapping.destinationRange.first + (contains.last - contains.first))
                            mapped.add(gevonden)
                            unmapped.remove(srcNum2)
                            if (srcNum2.first < gevonden.first) unmapped.add(srcNum2.first until gevonden.first)
                            if (gevonden.last < srcNum2.last) unmapped.add(gevonden.last+1..srcNum2.last)

                        } else if (mapping.sourceRange.contains(srcNum2.last)) {
                            val contains = mapping.destinationRange.first..min(mapping.sourceRange.last, srcNum2.last)
                            val offset = contains.first - mapping.sourceRange.first
                            val gevonden = (mapping.destinationRange.first + offset)..(mapping.destinationRange.first + (contains.last - contains.first))
                            mapped.add(gevonden)
                            unmapped.remove(srcNum2)
                            if (srcNum2.first < gevonden.first) unmapped.add(srcNum2.first until gevonden.first)
                            if (gevonden.last < srcNum2.last) unmapped.add(gevonden.last+1..srcNum2.last)

                        } else if (mapping.destinationRange.first > mapping.sourceRange.first && mapping.destinationRange.last < mapping.sourceRange.last) {
                            val contains = mapping.destinationRange
                            val offset = contains.first - mapping.sourceRange.first
                            val gevonden = (mapping.destinationRange.first + offset)..(mapping.destinationRange.first + (contains.last - contains.first))
                            mapped.add(gevonden)
                            unmapped.remove(srcNum2)
                            if (srcNum2.first < gevonden.first) unmapped.add(srcNum2.first until gevonden.first)
                            if (gevonden.last < srcNum2.last) unmapped.add(gevonden.last+1..srcNum2.last)
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
        println(maps);

//        assertThat(source.min()).isEqualTo(46);
//         assertThat(sum).isEqualTo(107430936);
    }


    @Test
    fun `part 3`() {
        val inputBlocks = readFileSplitted("2023/day5/input.txt", "\n\n")
        var seeds: List<LongRange> = emptyList()
        val regex = Regex("^(?<from>.+)-to-(?<to>.+) map:$")
        val maps: MutableMap<String, Pair<String, MutableList<Mapping2>>> = mutableMapOf();
        for (block in inputBlocks) {
            val match = regex.find(block[0]);
            if (block[0].startsWith("seeds: ")) {
                /*seeds = block[0].substring("seeds: ".length)
                    .split(" ").map { it.toLong()..it.toLong() }*/
                seeds = block[0].substring("seeds: ".length)
                    .split(" ").map { it.toLong() }.chunked(2).map { (start, length) ->
                        (start until (start+length))
                    }

            } else if (match != null) {
                val from =  match.groups["from"]?.value!!;
                val to =  match.groups["to"]?.value!!;
                maps[from] = to to mutableListOf()
                for (i in 1 until block.size) {
                    val vals = extractRegexGroupsL("^([0-9]+) ([0-9]+) ([0-9]+)$", block[i])
                    maps[from]!!.second.add(Mapping2((vals[0] until vals[0]+vals[2]), vals[1] until (vals[1]+vals[2])))
                }
            }
        }

        var name = "seed";
        var source = seeds;
        while(true) {
            var newName = maps[name]!!.first
            var newSource = mutableListOf<LongRange>()
            for (srcNum in source) {
                var unmapped = mutableListOf<LongRange>(srcNum)
                var mapped = mutableListOf<LongRange>()
                for (mapping in maps[name]!!.second) {
                    var toIter = mutableListOf<LongRange>()
                    toIter.addAll(unmapped)
                    for (srcNum2 in toIter) {
                        if (mapping.sourceRange.contains(srcNum2.first)) {
                            val contains = srcNum2.first..min(mapping.sourceRange.last, srcNum2.last)
                            val offset = contains.first - mapping.sourceRange.first
                            val gevonden = (mapping.destinationRange.first + offset)..(mapping.destinationRange.first + offset + (contains.last - contains.first))
                            mapped.add(gevonden)
                            unmapped.remove(srcNum2)
                            if (srcNum2.first < contains.first) unmapped.add(srcNum2.first until contains.first)
                            if (contains.last < srcNum2.last) unmapped.add(contains.last+1..srcNum2.last)

                        } else if (mapping.sourceRange.contains(srcNum2.last)) {
                            val contains = mapping.sourceRange.first..min(mapping.sourceRange.last, srcNum2.last)
                            val offset = contains.first - mapping.sourceRange.first
                            val gevonden = (mapping.destinationRange.first + offset)..(mapping.destinationRange.first + offset + (contains.last - contains.first))
                            mapped.add(gevonden)
                            unmapped.remove(srcNum2)
                            if (srcNum2.first < contains.first) unmapped.add(srcNum2.first until contains.first)
                            if (contains.last < srcNum2.last) unmapped.add(contains.last+1..srcNum2.last)

                        } else if (srcNum2.contains(mapping.sourceRange.first) && srcNum.contains(mapping.sourceRange.last)) {
                            val contains = mapping.sourceRange
                            val offset = contains.first - mapping.sourceRange.first
                            val gevonden = (mapping.destinationRange.first + offset)..(mapping.destinationRange.first +  offset + (contains.last - contains.first))
                            mapped.add(gevonden)
                            unmapped.remove(srcNum2)
                            if (srcNum2.first < contains.first) unmapped.add(srcNum2.first until contains.first)
                            if (contains.last < srcNum2.last) unmapped.add(contains.last+1..srcNum2.last)
                        } else {
                            ;
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
        println(maps);
        var r = source.map {
            it.first
        }.min()
        //   23738616
        // < 6577901
        assertThat(r).isEqualTo(46);
//         assertThat(sum).isEqualTo(107430936);
    }


    data class Mapping(val destinationRangeStart: Long, val sourceRangeStart: Long, val rangeLength: Long) {

    }

    data class Mapping2(val destinationRange: LongRange, val sourceRange: LongRange) {

    }
}