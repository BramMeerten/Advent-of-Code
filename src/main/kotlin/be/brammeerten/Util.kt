package be.brammeerten

fun readFile(file: String) =
        {}::class.java.classLoader.getResourceAsStream(file)
                ?.reader()
                ?.readLines()!!

fun readSingleLine(file: String): String {
        val lines = readFile(file)
        if (lines.size != 1)
                throw IllegalStateException("Expected a single line in file $file")
        return lines[0]
}