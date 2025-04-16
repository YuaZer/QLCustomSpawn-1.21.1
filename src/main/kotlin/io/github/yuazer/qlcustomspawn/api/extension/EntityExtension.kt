package io.github.yuazer.qlcustomspawn.api.extension

import org.bukkit.entity.Entity

object EntityExtension {
    fun Entity.isCobblemon():Boolean{
        return this.type.key.namespace.equals("cobblemon",true)
    }
}