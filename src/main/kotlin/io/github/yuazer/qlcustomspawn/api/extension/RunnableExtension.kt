package io.github.yuazer.qlcustomspawn.api.extension

import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.invokeMethod

object RunnableExtension {
    fun BukkitRunnable.isCancelledNoChecked(): Boolean {
        val property = this.getProperty<BukkitTask>("task")
        val isCancel = property?.invokeMethod<Boolean>("isCancelled")
        return isCancel ?: false
    }
    fun BukkitRunnable.getTask(): BukkitTask? {
        return this.getProperty<BukkitTask>("task")
    }
}