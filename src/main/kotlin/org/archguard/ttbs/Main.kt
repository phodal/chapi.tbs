@file:JvmName("Main")
package org.archguard.ttbs

import java.io.File

fun main(vararg args: String) {
    val results = TbsAnalyser().analysisByPath(".")
    File("tbs.json").writeText(results.toList().toString())
}
