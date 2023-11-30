package be.brammeerten.y2022

import be.brammeerten.extractRegexGroups
import be.brammeerten.graphs.Dijkstra
import be.brammeerten.graphs.Graph
import be.brammeerten.graphs.Node
import be.brammeerten.graphs.Vertex
import be.brammeerten.readFile
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class Day16Test {
    private val START = "AA"
    var TIME = 30
    var HEURISTIC_MIN_VISITED = 0
    var HEURISTIC_MIN_PRESSURE = 0

    val dijkstraCache = HashMap<Pair<String, String>, List<Valve>?>()

    @Test
    fun `part 1a`() {
        val graph = readGraph(readFile("2022/day16/exampleInput.txt"))
        Assertions.assertEquals(1651, solve(graph))
    }

    @Test
    fun `part 1b`() {
        val graph = readGraph(readFile("2022/day16/input.txt"))
        val solved = solve(graph)
        Assertions.assertEquals(1647, solved)
    }

    @Test
    fun `part 2a`() {
        TIME = 26
        val graph = readGraph(readFile("2022/day16/exampleInput.txt"))
        Assertions.assertEquals(1707, solveWithElephant(graph))
    }

    @Test
    fun `part 2b`() {
        TIME = 26
        HEURISTIC_MIN_VISITED = 6
        HEURISTIC_MIN_PRESSURE = 600
        val graph = readGraph(readFile("2022/day16/input.txt"))
        Assertions.assertEquals(2169, solveWithElephant(graph))
    }

    fun solve(graph: CaveGraph): Int {
        val state = State(graph.nodes[START]!!, 0, 0, 0)
        val notVisited = HashMap(graph.nodes)
        notVisited.remove(START)

        val solutions = notVisited.values.flatMap { target -> trySolution(graph, state, target, notVisited, 1) }
        return solutions.maxOf { it.pressure }
    }

    fun solveWithElephant(graph: CaveGraph): Int {
        var state = State(graph.nodes[START]!!, 0, 0, 0)
        var notVisited = HashMap(graph.nodes)
        notVisited.remove(START)
        val solutions1 = notVisited.values.flatMap { target -> trySolution(graph, state, target, notVisited, 1) }

        println("Found possible solutions: ${solutions1.size}")

        return solutions1
            .flatMap { s1 -> solutions1.map { s2 -> s1 to s2 }
                .filter { (s1, s2) -> s1.getVisitedSize(graph.nodes.size) + s2.getVisitedSize(graph.nodes.size) -1 <= graph.nodes.keys.size }
                .filter { (s1, s2) -> s1.getVisited(graph.nodes.keys).none { v -> s2.getVisited(graph.nodes.keys).contains(v) }}}
            .maxOf { (s1, s2) -> s1.pressure + s2.pressure }
    }

    fun trySolution(graph: CaveGraph, state: State, target: Valve, notVisited: Map<String, Valve>, count: Int): List<Result> {
        // new state
        val newState = openValve(graph, target, state)
        if (newState.time == TIME) return listOf(Result(newState.pressure, HashMap(notVisited)))

        // new targets
        val newNotVisited = HashMap(notVisited)
        newNotVisited.remove(target.key)

        // no targets left, wait
        if (newNotVisited.isEmpty())
            return listOf(Result(newState.pressure + ((TIME-newState.time) * newState.pressurePerRound), HashMap(newNotVisited)))

        // try all targets
        val out = newNotVisited.values
            .flatMap { trySolution(graph, newState, it, newNotVisited, count+1).filter { it.pressure > HEURISTIC_MIN_PRESSURE && it.getVisitedSize(graph.nodes.size) > HEURISTIC_MIN_VISITED } }
        val wait = tryWaiting(newState, newNotVisited)
        return if (wait.pressure > HEURISTIC_MIN_PRESSURE) out + tryWaiting(newState, newNotVisited) else out
    }

    fun tryWaiting(state: State, notVisited: Map<String, Valve>): Result {
        return Result(state.pressure + ((TIME-state.time) * state.pressurePerRound), HashMap(notVisited))
    }

    data class Result(val pressure: Int, val notVisited: HashMap<String, Valve>) {
        fun getVisited(allNodes: Set<String>): Set<String> {
            return allNodes - notVisited.keys - "AA"
        }

        fun getVisitedSize(allNodesSize: Int): Int {
            return allNodesSize - notVisited.size
        }
    }

    fun openValve(graph: CaveGraph, to: Valve, state: State): State {
        val path = Dijkstra.findShortestPath(graph, state.position.key, to.key, dijkstraCache)!!
        val steps = path.windowed(2).sumOf { (from, to) -> from.vertices.find { it.to == to.key }!!.weight }

        // Not enough time to reach valve and open it, just stay still
        if (state.time + steps >= TIME) {
            return State(state.position, TIME, state.pressure + ((TIME-state.time)*state.pressurePerRound), state.pressurePerRound)
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