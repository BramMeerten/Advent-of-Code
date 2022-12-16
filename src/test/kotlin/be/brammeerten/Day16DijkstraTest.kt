package be.brammeerten

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class Day16DijkstraTest {

    val dijkCache = HashMap<Pair<String, String>, List<Node>?>()

    @Test
    fun `part 1a`() {
        val graph = readGraph(readFile("day16/input.txt"))
        val graphSize = graph.nodes.size
        graph.simplify()
        println("Simplified graph from ${graphSize} to ${graph.nodes.size}")
        Assertions.assertEquals(1651, solve(graph))
    }
    val possibilities = 1307674368000L
    var indexx = 1

    fun solve(graph: Graph): Int {
        val cur = graph.nodes["AA"]!!
        return solve(graph, cur, 0, 0, HashSet(), graph.nodes.values.toHashSet())
    }

    fun solve(graph: Graph, cur: Node, minute: Int, pressure: Int, visited: Set<Node>, openTarget: Set<Node>,
    level: Int = 0): Int {
        if (minute == 30) return pressure

        var newPressure = pressure
        var newMinute = minute

        // spend minute opening valve
        if (cur.rate != 0) {
            newPressure += graph.getPressureOfRound(visited)
            newMinute++
        }

        // move to cur
        if (newMinute == 30) return newPressure
        val newVisited = visited + cur
        val newTargets = openTarget - cur
        val pressureOfRound = graph.getPressureOfRound(newVisited)
        newPressure += pressureOfRound
        newMinute++

        // nowhere to go, wait until time is up
        if (newTargets.isEmpty()) {
            return newPressure + (pressureOfRound * (30 - newMinute))
        }

        return newTargets.maxOf{ target ->
            if (indexx % 1000000 == 0) println("${indexx} / $possibilities (${indexx.toFloat()/possibilities})")
            indexx++

            val path = dijk(graph, cur.name, target.name)!!
            val steps = path.windowed(2).sumOf { (from, to) ->
                from.linksTo.find { it.target==to.name }!!.weight
            }

            val newMinutes = newMinute + steps - 1
            val newPressures = newPressure + ((steps-1) * pressureOfRound)
            solve(graph, target, newMinutes, newPressures, newVisited, newTargets, level+1)
        }
    }

    fun readGraph(lines: List<String>): Graph {
        val graph = Graph()
        lines
            .map { extractRegexGroups("^Valve (.+) has flow rate=(\\d+); tunnels? leads? to valves? (.+)$", it) }
            .forEach { matches ->
                graph.addNode(matches[0], matches[1].toInt(), matches[2].split(", ").map { Vertex(it, 1) })
            }

        return graph
    }

    class Graph {
        val nodes = HashMap<String, Node>()

        fun addNode(name: String, rate: Int, linksTo: List<Vertex>) {
            nodes.putIfAbsent(name, Node(name, rate))
            val n = nodes[name]!!.addLinksTo(linksTo)
            nodes[name] = n.setRate(rate)
        }

        fun getPressureOfRound(visited: Set<Node>): Int {
            return visited.sumOf { it.rate }
        }

        fun simplify() {
            while(true) {
                val remove = nodes.values.filter { it.rate ==0 && it.name != "AA" }.firstOrNull()
                if (remove == null) break
                val neighbours = remove.linksTo
                neighbours.forEach{neighbour ->
                    val others = neighbours.filter { it != neighbour }
                    others.forEach{other ->
                        val newLink = Vertex(other.target, neighbour.weight + other.weight)
                        if (nodes[neighbour.target] == null) {
                            println()
                        }
                        val existingLink = nodes[neighbour.target]!!.linksTo.firstOrNull { it.target == newLink.target }
                        if (existingLink != null) {
                            if (newLink.weight < existingLink.weight) {
                                nodes[neighbour.target] = nodes[neighbour.target]!!.removeLinkTo(existingLink)
                            }
                        }
                        nodes[neighbour.target] = nodes[neighbour.target]!!.removeLinkTo(remove)
                        nodes[neighbour.target] = nodes[neighbour.target]!!.addLinksTo(listOf(newLink))
                    }
                }
                nodes.remove(remove.name)
            }
        }
    }

    data class Node(val name: String, val rate: Int = -1, val linksTo: List<Vertex> = emptyList()) {
        fun addLinksTo(nodes: List<Vertex>): Node {
            return Node(name, rate, linksTo + nodes)
        }

        fun removeLinkTo(node: Vertex): Node {
            return Node(name, rate, linksTo - node)
        }

        fun removeLinkTo(node: Node): Node {
            return Node(name, rate, linksTo.filter { it.target != node.name }.toList())
        }

        fun setRate(flowRate: Int): Node {
            return Node(name, flowRate, linksTo)
        }
    }

    fun dijk(graph: Graph, start: String, end: String): List<Node>? {
        val cache = dijkCache.get(start to end)
        if (cache != null) return cache

        val unvisited = HashSet<String>(graph.nodes.keys)
        val prevs = HashMap<String, String?>()

        val distances = HashMap<String, Int>()
        graph.nodes.values.forEach{distances.put(it.name, Int.MAX_VALUE)}
        distances[start] = 0

        while(unvisited.isNotEmpty()) {
            val cur: Node = graph.nodes.values.filter { unvisited.contains(it.name) }.minBy { distances[it.name]!! }
            unvisited.remove(cur.name)

            cur.linksTo.forEach { neighbour ->
                val d = distances[cur.name]!! + neighbour.weight
                if (d < distances[neighbour.target]!!) {
                    distances[neighbour.target] = d
                    prevs[neighbour.target] = cur.name
                }
            }
        }

        val result = toPath(end, start, prevs, graph)
        dijkCache[start to end] = result
        dijkCache[end to start] = result?.reversed()
        println("Cache size ${dijkCache.size}/256")
        return result

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

    data class Vertex(val target: String, val weight: Int)
}