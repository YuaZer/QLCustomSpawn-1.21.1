package io.github.yuazer.qlcustomspawn.api.extension

import net.minecraft.server.level.WorldServer
import org.bukkit.craftbukkit.v1_21_R1.CraftWorld
import org.bukkit.entity.Entity

object NMSExtension {
    fun getNMSEntity(entity: Entity): Any? {
        return try {
            val entityClass = Class.forName("net.minecraft.world.entity.Entity")
            val getHandleMethod = entity.javaClass.getMethod("getHandle")
            getHandleMethod.invoke(entity)
        } catch (e: Exception) {
            null
        }
    }
    fun org.bukkit.World.getNMSWorld(): WorldServer? {
        return (this as CraftWorld).handle
    }
}