package io.github.yuazer.qlcustomspawn.manager

import io.github.yuazer.qlcustomspawn.data.DataLoader
import io.github.yuazer.qlcustomspawn.obj.runnable.SpawnContainer
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import taboolib.common.io.newFolder
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.invokeMethod
import taboolib.platform.BukkitPlugin

class ContainerManager(val plugin: JavaPlugin) {
    private val containers: MutableMap<String, SpawnContainer> = mutableMapOf()

    // 添加（已存在则先 cancel 再替换）
    fun add(container: SpawnContainer) {
        containers[container.name]?.cancel() // 先取消旧任务
        containers[container.name] = container
    }

    // 根据名称获取
    fun get(name: String): SpawnContainer? {
        return containers[name]
    }

    fun start(name: String, delay: Long = 0L, period: Long = 20L) {
        containers[name]?.runTaskTimerAsynchronously(plugin, delay, period)
    }

    // 删除（并取消任务）
    fun remove(name: String): Boolean {
        val cancelled = containers[name]?.isCancelled
        if (cancelled == true) {
            containers[name]?.cancel()
        }
        return containers.remove(name) != null
    }

    // 判断是否存在
    fun contains(name: String): Boolean {
        return containers.containsKey(name)
    }

    // 获取全部容器名称
    fun keys(): Set<String> {
        return containers.keys
    }

    // 获取全部容器
    fun values(): Collection<SpawnContainer> {
        return containers.values
    }

    // 清空（并全部 cancel）
    fun clear() {
        containers.values.forEach {
            val property = it.getProperty<BukkitTask>("task")
            val isCancel = property?.invokeMethod<Boolean>("isCancelled")
            if (isCancel == false) {
                it.cancel()
            }
        }
        containers.clear()
    }

    fun startAll() {
        containers.keys.forEach {
            start(it)
        }
    }

    fun reloadAll() {
        clear()
        DataLoader.loadContainerFormFolder(newFolder(BukkitPlugin.getInstance().dataFolder, "container"))
        startAll()
    }
}
