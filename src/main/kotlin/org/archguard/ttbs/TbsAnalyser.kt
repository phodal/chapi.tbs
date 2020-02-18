package org.archguard.ttbs

import chapi.app.analyser.ChapiAnalyser
import chapi.app.analyser.config.ChapiConfig
import chapi.domain.core.CodeAnnotation
import chapi.domain.core.CodeCall
import chapi.domain.core.CodeDataStruct
import chapi.domain.core.CodeFunction

data class TestBadSmell(
    var FileName: String = "",
    var Type: String = "",
    var Description: String = "",
    var Line: Int = 0
) {}

data class TbsResult(var results: Array<TestBadSmell>) {}

class TbsAnalyser(
    private val config: ChapiConfig = ChapiConfig()
) {
    fun analysisByPath(path: String): Array<TestBadSmell> {
        val nodes = ChapiAnalyser(config).analysis(path)
        var tbsResult: TbsResult = TbsResult(arrayOf())
        val callMethodMap = buildCallMethodMap(nodes)

        for (node in nodes) {
            for (method in node.Functions) {
                if (!method.isJUnitTest()) {
                    continue
                }

                for (annotation in method.Annotations) {
                    checkIgnoreTest(node.FilePath, annotation, tbsResult, method)
                    checkEmptyTest(node.FilePath, annotation, tbsResult, method)
                }

                val methodCallMap = mutableMapOf<String, Array<CodeCall>>()
                val hasAssert = false
                for (funcCall in method.FunctionCalls) {
                    checkRedundantPrintTest(node.FilePath, funcCall, tbsResult)
                    checkSleepyTest(node.FilePath, method, funcCall, tbsResult)
                    checkRedundantAssertionTest(node.FilePath, method, funcCall, tbsResult)
                }
            }
        }

        return tbsResult.results
    }

    private fun checkRedundantAssertionTest(
        filePath: String,
        method: CodeFunction,
        funcCall: CodeCall,
        tbsResult: TbsResult
    ) {
        val assertParametersSize = 2
        if (funcCall.Parameters.size == assertParametersSize) {
            if (funcCall.Parameters[0].TypeValue == funcCall.Parameters[1].TypeValue) {
                val testBadSmell = TestBadSmell(
                    FileName = filePath,
                    Type = "RedundantAssertionTest",
                    Description = "",
                    Line = funcCall.Position.StartLine
                )

                tbsResult.results += testBadSmell
            }
        }
    }

    private fun checkSleepyTest(filePath: String, method: CodeFunction, funcCall: CodeCall, tbsResult: TbsResult) {
        if (funcCall.isThreadSleep()) {
            val testBadSmell = TestBadSmell(
                FileName = filePath,
                Type = "SleepyTest",
                Description = "",
                Line = funcCall.Position.StartLine
            )

            tbsResult.results += testBadSmell
        }
    }

    private fun checkRedundantPrintTest(filePath: String, funcCall: CodeCall, tbsResult: TbsResult) {
        if (funcCall.isSystemOutput()) {
            val testBadSmell = TestBadSmell(
                FileName = filePath,
                Type = "RedundantPrintTest",
                Description = "",
                Line = funcCall.Position.StartLine
            )

            tbsResult.results += testBadSmell
        }
    }

    private fun checkIgnoreTest(
        filePath: String,
        annotation: CodeAnnotation,
        tbsResult: TbsResult,
        method: CodeFunction
    ) {
        if (annotation.isIgnore()) {
            val testBadSmell = TestBadSmell(
                FileName = filePath,
                Type = "IgnoreTest",
                Description = "",
                Line = method.Position.StartLine
            )

            tbsResult.results += testBadSmell
        }
    }

    private fun checkEmptyTest(
        filePath: String,
        annotation: CodeAnnotation,
        tbsResult: TbsResult,
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

                tbsResult.results += badSmell
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
