package org.archguard.ttbs

import chapi.app.analyser.AbstractFile
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.nio.file.Paths

internal class TbsAnalyserTest {
    @Test
    internal fun shouldIdentifyJavaEmptyTest() {
        val files = getTestResourceFileByPath("tbs/usecases/EmptyTest.java")
        val results = TbsAnalyser().analysisByFiles(files)

        assertEquals(results[0].FileName, "EmptyTest.java")
        assertEquals(results[0].Line, 8)
        assertEquals(results[0].Type, "EmptyTest")
    }

//    @Test
//    internal fun shouldIdentifyGolangEmptyTest() {
//        val files = getTestResourceFileByPath("tbs/go/empty_test.go")
//        val results = TbsAnalyser().analysisByFiles(files)
//
//        assertEquals(results[0].FileName, "empty_test.go")
//        assertEquals(results[0].Line, 8)
//        assertEquals(results[0].Type, "EmptyTest")
//    }
//
    private fun getTestResourceFileByPath(path: String): Array<AbstractFile> {
        val resource = this.javaClass.classLoader.getResource(path)
        val file = Paths.get(resource!!.toURI()).toFile()

        var files = arrayOf<AbstractFile>()
        val toAbstractFile = AbstractFile.toAbstractFile(file)
        files += toAbstractFile

        return files
    }
}
