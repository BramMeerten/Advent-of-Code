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

fun signI(value: Int): Int {
        return if (value == 0) 0 else if (value < 0) -1 else 1
}

fun extractRegexGroups(regex: String, text: String): List<String> {
        val matches = Regex(regex).find(text)
        return matches!!.groupValues.drop(1)
}

fun String.toCharList() = this.toCharArray().toList()

data class Co(val row: Int, val col: Int) {
        fun add(co: Co): Co {
                return Co(row + co.row, col + co.col)
        }

        fun min(vararg cos: Co): Co {
                return cos.fold(this) {acc, co -> Co(Math.min(acc.row, co.row), Math.min(acc.col, co.col))}
        }

        fun max(vararg cos: Co): Co {
                return cos.fold(this) {acc, co -> Co(Math.max(acc.row, co.row), Math.max(acc.col, co.col))}
        }
}