package be.brammeerten.graphs

data class Node<K, V>(val key: K, val value: V, val vertices: List<Vertex<K>>) {

}