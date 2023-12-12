package be.brammeerten.graphs

import java.util.function.Function

object AStar {
    fun <K, V> findShortestPath(graph: Graph<K, V>, start: K, end: K,
                                h: Function<Pair<K, K>, Double>,
                                cache: HashMap<Pair<K, K>, List<Node<K, V>>?>? = null): List<Node<K, V>>? {
        // Check cache
        val cached = cache?.get(start to end)
        if (cached != null) return cached

        // Traverse graph
        val prevs = traverse(graph, h, start, end)

        // Calculate path
        val result = toPath(end, start, prevs, graph)

        // Store in cache
        if (cache != null) {
            cache[start to end] = result
            cache[end to start] = result?.reversed()
        }

        return result
    }

    private fun <K, V> traverse(graph: Graph<K, V>, h: Function<Pair<K, K>, Double>, start: K, end: K): HashMap<K, K?> {
        val unvisited = HashSet<K>(graph.nodes.keys)
        val prevs = HashMap<K, K?>()

        val gScore = HashMap<K, Int>()
        graph.nodes.values.forEach { gScore[it.key] = Int.MAX_VALUE }
        gScore[start] = 0

        val fScore = HashMap<K, Double>()
        graph.nodes.values.forEach { fScore[it.key] = Double.MAX_VALUE }
        fScore[start] = h.apply(start to end)

        while (unvisited.isNotEmpty()) {
            val cur = graph.nodes.values
                .filter { unvisited.contains(it.key) }
                .minBy { fScore[it.key]!!}
            if (cur.key == end)
                return prevs
            unvisited.remove(cur.key)

            cur.vertices.forEach { vertex ->
                val d = gScore[cur.key]!! + vertex.weight
                if (d < gScore[vertex.to]!!) {
                    gScore[vertex.to] = d
                    val hResult = h.apply(vertex.to to end)
                    fScore[vertex.to] = d + hResult
                    prevs[vertex.to] = cur.key
                }
            }
        }
        return prevs
    }

    private fun <K, V> toPath(end: K, start: K, prevs: HashMap<K, K?>, graph: Graph<K, V>): List<Node<K, V>>? {
        val path = ArrayList<K>()
        var curr: K? = end
        while (curr != null && curr != start) {
            path.add(curr)
            curr = prevs[curr]
        }

        if (curr == null) return null
        path.add(curr)
        return path.map { graph.nodes[it]!! }.reversed()
    }
}