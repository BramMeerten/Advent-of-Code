package be.brammeerten.graphs

object Dijkstra {
    fun <K, V> findShortestPath(graph: Graph<K, V>, start: K, end: K,
                                cache: HashMap<Pair<K, K>, List<Node<K, V>>?>? = null): List<Node<K, V>>? {
        // Check cache
        val cached = cache?.get(start to end)
        if (cached != null) return cached

        // Traverse graph
        val prevs = traverse(graph, start)

        // Calculate path
        val result = toPath(end, start, prevs, graph)

        // Store in cache
        if (cache != null) {
            cache[start to end] = result
            cache[end to start] = result?.reversed()
        }

        return result
    }

    private fun <K, V> traverse(graph: Graph<K, V>, start: K): HashMap<K, K?> {
        val unvisited = HashSet<K>(graph.nodes.keys)
        val prevs = HashMap<K, K?>()

        val distances = HashMap<K, Int>()
        graph.nodes.values.forEach { distances[it.key] = Int.MAX_VALUE }
        distances[start] = 0

        while (unvisited.isNotEmpty()) {
            val cur = graph.nodes.values.filter { unvisited.contains(it.key) }.minBy { distances[it.key]!! }
            unvisited.remove(cur.key)

            cur.vertices.forEach { vertex ->
                val d = distances[cur.key]!! + vertex.weight
                if (d < distances[vertex.to]!!) {
                    distances[vertex.to] = d
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