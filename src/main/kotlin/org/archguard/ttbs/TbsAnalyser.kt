package org.archguard.ttbs

import chapi.app.analyser.ChapiAnalyser

class TbsAnalyser {
    fun analysis(path: String = "") {
        val nodes = ChapiAnalyser().analysisByPath(path)
    }
}
