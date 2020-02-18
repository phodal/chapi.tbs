package org.archguard.ttbs

import chapi.app.analyser.ChapiAnalyser
import chapi.app.analyser.config.ChapiConfig
import chapi.domain.core.CodeAnnotation
import chapi.domain.core.CodeDataStruct
import chapi.domain.core.CodeFunction

data class TestBadSmell(
    var FileName: String = "",
    var Type: String = "",
    var Description: String = "",
    var Line: Int = 0
) {}

data class TbsResults(var results: Array<TestBadSmell>) {}

class TbsAnalyser(
    private val config: ChapiConfig = ChapiConfig()
) {
    fun analysisByPath(path: String): Array<TestBadSmell> {
        val nodes = ChapiAnalyser(config).analysis(path)
        var results: TbsResults = TbsResults(arrayOf())
        val callMethodMap = buildCallMethodMap(nodes)

        for (node in nodes) {
            for (method in node.Functions) {
                if (!method.isJUnitTest()) {
                    continue
                }

                for (annotation in method.Annotations) {
                    checkEmptyTest(node.FilePath, annotation, results, method)
                }
            }
        }

        return results.results
    }

    private fun checkEmptyTest(
        filePath: String,
        annotation: CodeAnnotation,
        results: TbsResults,
        method: CodeFunction
    ) {
        val isJavaTest = filePath.endsWith(".java") && annotation.isTest()
        val isGoTest = filePath.endsWith("_test.go")
        if (isJavaTest || isGoTest) {
            if (method.FunctionCalls.size <= 1) {
                val badSmell = TestBadSmell(
                    FileName = filePath,
                    Type = "EmptyTest",
                    Description = "",
                    Line = method.Position.StartLine
                )

                results.results += badSmell
            }
        }
    }

    private fun buildCallMethodMap(nodes: Array<CodeDataStruct>): MutableMap<String, CodeFunction> {
        var callMethodMap: MutableMap<String, CodeFunction> = mutableMapOf()
        for (node in nodes) {
            for (method in node.Functions) {
                callMethodMap[method.buildFullMethodName(node)] = method
            }
        }

        return callMethodMap
    }
}
