package io.github.yuazer.qlcustomspawn.api.extension

import net.minecraft.server.level.WorldServer
import net.minecraft.world.item.ItemStack
import org.bukkit.craftbukkit.v1_21_R1.CraftWorld
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftEntity
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftItemStack
import org.bukkit.entity.Entity

object NMSExtension {
    fun Entity.getNMSEntity(): net.minecraft.world.entity.Entity? {
        return (this as CraftEntity).handle
    }
    fun org.bukkit.World.getNMSWorld(): WorldServer? {
        return (this as CraftWorld).handle
    }
    fun ItemStack.getBukkitItem(): org.bukkit.inventory.ItemStack? {
        return CraftItemStack.asBukkitCopy(this)
    }
}