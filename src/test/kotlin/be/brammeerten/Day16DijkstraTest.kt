package be.brammeerten

import be.brammeerten.graphs.Dijkstra
import be.brammeerten.graphs.Graph
import be.brammeerten.graphs.Node
import be.brammeerten.graphs.Vertex
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class Day16DijkstraTest {

    val dijkstraCache = HashMap<Pair<String, String>, List<Valve>?>()

    @Test
    fun `part 1a`() {
        val graph = readGraph(readFile("day16/input.txt"))
        Assertions.assertEquals(1651, solve(graph))
    }

    val possibilities = 1307674368000L
    var indexx = 1

    fun solve(graph: CaveGraph): Int {
        val cur = graph.nodes["AA"]!!
        return solve(graph, cur, 0, 0, HashSet(), graph.nodes.values.toHashSet())
    }

    fun solve(
        graph: CaveGraph, cur: Valve, minute: Int, pressure: Int, visited: Set<Valve>, openTarget: Set<Valve>): Int {
        if (minute == 30) return pressure

        var newPressure = pressure
        var newMinute = minute

        // spend minute opening valve
        if (cur.value != 0) {
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

        return newTargets.maxOf { target ->
            if (indexx % 1000000 == 0) println("${indexx} / $possibilities (${indexx.toFloat() / possibilities})")
            indexx++

            val path = Dijkstra.findShortestPath(graph, cur.key, target.key, dijkstraCache)!!
            val steps = path.windowed(2).sumOf { (from, to) ->
                from.vertices.find { it.to == to.key }!!.weight
            }

            val newMinutes = newMinute + steps - 1
            val newPressures = newPressure + ((steps - 1) * pressureOfRound)
            solve(graph, target, newMinutes, newPressures, newVisited, newTargets)
        }
    }

    fun CaveGraph.getPressureOfRound(visited: Set<Valve>): Int {
        return visited.sumOf { it.value }
    }


    /**
     * ====================== READING AND SIMPLIFYING THE GRAPH ====================
     */
    fun readGraph(lines: List<String>): CaveGraph {
        val graph = CaveGraph()
        lines
            .map { extractRegexGroups("^Valve (.+) has flow rate=(\\d+); tunnels? leads? to valves? (.+)$", it) }
            .forEach { matches -> graph.addNode(matches[0], matches[1].toInt(), matches[2].split(", ").map { Vertex(it, 1) }) }
        return graph.simplify()
    }

    fun CaveGraph.simplify(): CaveGraph {
        while (true) {
            val remove = nodes.values.firstOrNull { it.value == 0 && it.key != "AA" } ?: break
            remove(remove)
        }
        return this
    }

    fun CaveGraph.remove(node: Valve) {
        val neighbours = node.vertices
        neighbours.forEach { neighbour ->
            neighbours
                .filter { it != neighbour }
                .forEach { newNeighbour ->
                    var curNode = nodes[neighbour.to]!!
                    val newLink = Vertex(newNeighbour.to, neighbour.weight + newNeighbour.weight)
                    val existingLink = curNode.vertices.firstOrNull { it.to == newLink.to }
                    if (existingLink != null && newLink.weight < existingLink.weight) {
                        curNode = curNode.removeVertex(existingLink)
                    }
                    nodes[neighbour.to] = curNode.removeVertexTo(node).addVertex(newLink)
                }
        }
        nodes.remove(node.key)
    }

    fun <K, V> Node<K, V>.removeVertex(vertex: Vertex<K>): Node<K, V> {
        return Node(key, value, vertices - vertex)
    }

    fun <K, V> Node<K, V>.removeVertexTo(node: Node<K, V>): Node<K, V> {
        return Node(key, value, vertices.filter { it.to != node.key }.toList())
    }

    fun <K, V> Node<K, V>.addVertex(vertex: Vertex<K>): Node<K, V> {
        return Node(key, value, vertices + vertex)
    }

}

typealias CaveGraph = Graph<String, Int>
typealias Valve = Node<String, Int>
