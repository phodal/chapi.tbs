package org.archguard.ttbs

import chapi.app.analyser.config.ChapiConfig
import chapi.app.analyser.support.AbstractFile
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
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
//
//    @Test
//    internal fun shouldIdentifyGolangEmptyTest() {
//        val path = getAbsolutePath("tbs/go/empty_test.go")
//        val results = TbsAnalyser(ChapiConfig(language = "go")).analysisByPath(path)
//
//        assertEquals(results[0].FileName, "empty_test.go")
//        assertEquals(results[0].Line, 8)
//        assertEquals(results[0].Type, "EmptyTest")
//    }

    private fun getAbsolutePath(path: String): String {
        val resource = this.javaClass.classLoader.getResource(path)
        return Paths.get(resource!!.toURI()).toFile().absolutePath
    }
}
