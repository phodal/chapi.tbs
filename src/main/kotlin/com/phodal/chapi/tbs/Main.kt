@file:JvmName("Main")
package com.phodal.chapi.tbs

import org.archguard.tbs.TbsAnalyser
import java.io.File

fun main(vararg args: String) {
    val results = TbsAnalyser().analysisByPath(".")
    File("tbs.json").writeText(results.toList().toString())
}
