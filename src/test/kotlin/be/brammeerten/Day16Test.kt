package be.brammeerten

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.system.exitProcess

class Day16Test {

    @Test
    fun `part 1a`() {
        val graph = readGraph(readFile("day16/input.txt"))
        Assertions.assertEquals(1651, solve(graph))
    }

    fun solve(graph: Graph): Int {
        val cur = graph.nodes["AA"]!!
        return solve(graph, cur, 0, 0, HashSet<Node>(), graph.nodes.values.toHashSet())
    }

    fun solve(graph: Graph, cur: Node, minute: Int, pressure: Int, visited: Set<Node>, openTarget: Set<Node>,
//              queue: List<String> = ArrayList()): Int {
              ): Int {
        if (minute == 30) return pressure

//        val newQueue = ArrayList(queue)
        val vis = visited + cur
        val tar = openTarget - cur
        var pressureOfRound = graph.getPressureOfRound(visited)
        var newPressure = pressure + pressureOfRound
        pressureOfRound = graph.getPressureOfRound(vis)
        if (cur.rate != 0) {
//            newQueue.add("minute $minute:\tOpen valve ${cur.name}\t\t$newPressure")
        } else {
//            newQueue.add("minute $minute:\tMove on")
            newPressure -= pressureOfRound
        }

        if (tar.isEmpty()) {
//            if (1688 == pressure + (pressureOfRound * (30 - minute))) {
//                queue.forEach { println(it) }
//                exitProcess(0)
//            }
            return pressure + (pressureOfRound * (30 - minute))
        }

        return tar.maxOf{ target ->
//            val newNewQueue = ArrayList(newQueue)
            val path = findShortestPath(graph, cur.name, target.name)!!
            val distance = path.size-1
            var newMinute = minute
            var newNewPress = newPressure
            for (i in 1 until path.size) {
                if (newMinute >= 30) {
//                    if (1688 == newNewPress) {
//                        queue.forEach { println(it) }
//                        exitProcess(0)
//                    }
                    return newNewPress - pressureOfRound
                }
                newMinute++
                newNewPress += pressureOfRound
//                newNewQueue.add("minute $newMinute:\tMove to valve ${path[i].name}\t\t$newNewPress")
            }

            solve(graph, target, if(target.rate==0)newMinute else newMinute+1, newNewPress, vis, tar) // , newNewQueue)
        }
    }

    fun readGraph(lines: List<String>): Graph {
        val graph = Graph()
        lines
            .map { extractRegexGroups("^Valve (.+) has flow rate=(\\d+); tunnels? leads? to valves? (.+)$", it) }
            .forEach { matches ->
                graph.addNode(matches[0], matches[1].toInt(), matches[2].split(", "))
            }

        return graph
    }

    class Graph {
        val nodes = HashMap<String, Node>()
//        val visited = HashSet<Node>()
//        var openTargets = HashSet<Node>()

        fun addNode(name: String, rate: Int, linksTo: List<String>) {
            nodes.putIfAbsent(name, Node(name, rate))
            val n = nodes[name]!!.addLinksTo(linksTo)
            nodes[name] = n.setRate(rate)
//            openTargets = nodes.values.filter { it.rate != 0 }.toHashSet()
        }

        fun getPressureOfRound(visited: Set<Node>): Int {
            return visited.sumOf { it.rate }
        }

        private fun getOrAddIfMissing(nodeName: String): Node {
            nodes.putIfAbsent(nodeName, Node(nodeName))
            return nodes[nodeName]!!
        }
    }

    data class Node(val name: String, val rate: Int = -1, val linksTo: List<String> = emptyList()) {
        fun addLinksTo(nodes: List<String>): Node {
            return Node(name, rate, linksTo + nodes)
        }

        fun setRate(flowRate: Int): Node {
            return Node(name, flowRate, linksTo)
        }
    }



    fun findShortestPath(graph: Graph, startNodeIndex: String, endNodeIndex: String): List<Node>? {
        val prevs = traverseGraphToEnd(graph, startNodeIndex)
        return toPath(endNodeIndex, startNodeIndex, prevs, graph)
    }

    private fun traverseGraphToEnd(graph: Graph, startNodeIndex: String): HashMap<String, String?> {
        val queue = LinkedList<String>()
        val visited = HashSet<String>()
        val prevs = HashMap<String, String?>()

        queue.add(startNodeIndex)
        visited.add(startNodeIndex)

        while (!queue.isEmpty()) {
            val nodeI = queue.remove()
            for (neighbour in graph.nodes[nodeI]!!.linksTo) {
                if (!visited.contains(neighbour)) {
                    queue.add(neighbour)
                    visited.add(neighbour)
                    prevs[neighbour] = nodeI
                }
            }
        }
        return prevs
    }

    private fun toPath(endNodeIndex: String, startNodeIndex: String, prevs: HashMap<String, String?>, graph: Graph): List<Node>? {
        val path = ArrayList<String>()
        var curr: String? = endNodeIndex
        while (curr != null && curr != startNodeIndex) {
            path.add(curr)
            curr = prevs[curr]
        }

        if (curr == null) return null
        path.add(curr)
        return path.map { graph.nodes[it]!! }.reversed()
    }
}