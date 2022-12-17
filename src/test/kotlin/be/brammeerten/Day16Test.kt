package be.brammeerten

import be.brammeerten.graphs.Dijkstra
import be.brammeerten.graphs.Graph
import be.brammeerten.graphs.Node
import be.brammeerten.graphs.Vertex
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class Day16Test {
    private val START = "AA"

    val dijkstraCache = HashMap<Pair<String, String>, List<Valve>?>()

    @Test
    fun `part 1a`() {
        val graph = readGraph(readFile("day16/exampleInput.txt"))
        Assertions.assertEquals(1651, solve(graph))
    }

    @Test
    fun `part 1aReal`() {
        val graph = readGraph(readFile("day16/input.txt"))
        val solved = solve(graph)
        Assertions.assertEquals(1647, solved)
    }

    fun solve(graph: CaveGraph): Int {
        val state = State(graph.nodes[START]!!, 0, 0, 0)
        val notVisited = HashMap(graph.nodes)
        notVisited.remove(START)

        return notVisited.values.maxOf { target ->
            trySolution(graph, state, target, notVisited, 1)
        }
    }

    fun trySolution(graph: CaveGraph, state: State, target: Valve, notVisited: Map<String, Valve>, count: Int): Int {
        // new state
        val newState = openValve(graph, target, state)
        if (newState.time == 30) return newState.pressure

        // new targets
        val newNotVisited = HashMap(notVisited)
        newNotVisited.remove(target.key)

        // no targets left, wait
        if (newNotVisited.isEmpty())
            return newState.pressure + ((30-newState.time) * newState.pressurePerRound)

        // try all targets
        return newNotVisited.values.maxOf { trySolution(graph, newState, it, newNotVisited, count+1) }
    }

    fun openValve(graph: CaveGraph, to: Valve, state: State): State {
        val path = Dijkstra.findShortestPath(graph, state.position.key, to.key, dijkstraCache)!!
        val steps = path.windowed(2).sumOf { (from, to) -> from.vertices.find { it.to == to.key }!!.weight }

        // Not enough time to reach valve and open it, just stay still
        if (state.time + steps >= 30) {
            return State(state.position, 30, state.pressure + ((30-state.time)*state.pressurePerRound), state.pressurePerRound)
        }

        // Run to valve and open it
        val timePassed = steps + 1
        val pressure = state.pressure + (timePassed * state.pressurePerRound)
        return State(to, state.time + timePassed, pressure, state.pressurePerRound + to.value)
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
            val remove = nodes.values.firstOrNull { it.value == 0 && it.key != START } ?: break
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

data class State(val position: Valve, val time: Int, val pressure: Int, val pressurePerRound: Int)