package io.github.yuazer.qlcustomspawn.api.data

import io.github.yuazer.qlcustomspawn.Qlcustomspawn
import io.github.yuazer.qlcustomspawn.manager.CreaterManager

object CreaterApi {
    fun getManager(): CreaterManager {
        return Qlcustomspawn.createrManager
    }
}