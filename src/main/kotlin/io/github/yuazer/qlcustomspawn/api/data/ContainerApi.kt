package io.github.yuazer.qlcustomspawn.api.data

import io.github.yuazer.qlcustomspawn.Qlcustomspawn
import io.github.yuazer.qlcustomspawn.manager.ContainerManager

object ContainerApi {
    fun getManager(): ContainerManager {
        return Qlcustomspawn.containerManager
    }
}