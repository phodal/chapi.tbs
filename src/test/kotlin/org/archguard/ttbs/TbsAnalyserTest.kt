package org.archguard.ttbs

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.nio.file.Paths

internal class TbsAnalyserTest {
    @Test
    internal fun shouldIdentifyJavaEmptyTest() {
        val path = getAbsolutePath("tbs/usecases/EmptyTest.java")
        val results = TbsAnalyser().analysisByPath(path)

        assertEquals(results[0].FileName, "EmptyTest.java")
        assertEquals(results[0].Line, 8)
        assertEquals(results[0].Type, "EmptyTest")
    }

    @Test
    internal fun shouldIdentifyJavaIgnoreTest() {
        val path = getAbsolutePath("tbs/usecases/IgnoreTest.java")
        val results = TbsAnalyser().analysisByPath(path)

        assertEquals(results[0].Line, 0)
        assertEquals(results[0].Type, "IgnoreTest")
    }

    private fun getAbsolutePath(path: String): String {
        val resource = this.javaClass.classLoader.getResource(path)
        return Paths.get(resource!!.toURI()).toFile().absolutePath
    }
}
