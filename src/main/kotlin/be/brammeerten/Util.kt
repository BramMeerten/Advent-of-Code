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

fun readFileSplitted(file: String, delimitter: String) =
        readAllText(file).split(delimitter)
                .map { it.split("\n") }

fun readAllText(file: String) =
        {}::class.java.classLoader.getResourceAsStream(file)
                ?.reader()
                ?.readText()!!

fun extractRegexGroups(regex: String, text: String): List<String> {
        val matches = Regex(regex).find(text)
        return matches?.groupValues?.drop(1) ?: throw IllegalStateException("Line does not match regex: $text")
}

fun extractRegexGroupsI(regex: String, text: String) =
        extractRegexGroups(regex, text).map { it.toInt() }

fun String.toCharList() = this.toCharArray().toList()

data class Co(val row: Int, val col: Int) {

        operator fun plus(co: Co): Co {
                return Co(row + co.row, col + co.col)
        }

        fun min(vararg cos: Co): Co {
                return cos.fold(this) {acc, co -> Co(Math.min(acc.row, co.row), Math.min(acc.col, co.col))}
        }

        fun max(vararg cos: Co): Co {
                return cos.fold(this) {acc, co -> Co(Math.max(acc.row, co.row), Math.max(acc.col, co.col))}
        }
}

fun Char.toAlphabetIndex(): Int {
        if (this in 'a'..'z')
                return this.toByte().toInt() - 'a'.toByte().toInt()
        else if (this in 'A' .. 'Z')
                return this.toByte().toInt() - 'A'.toByte().toInt()
        else
                throw IllegalStateException("Not an alphabetic character: '$this'")
}