fun readFile(file: String) =
        {}::class.java.classLoader.getResourceAsStream(file)
                ?.reader()
                ?.readLines()!!