package be.brammeerten.graphs

class Graph<K, V> {
    val nodes = HashMap<K, Node<K, V>>()

    fun addNode(key: K, value: V, vertices: List<Vertex<K>>) {
        nodes[key] = Node(key, value, vertices)
    }
}