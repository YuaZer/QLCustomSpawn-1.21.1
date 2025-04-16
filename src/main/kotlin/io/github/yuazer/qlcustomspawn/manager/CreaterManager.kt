package io.github.yuazer.qlcustomspawn.manager

import io.github.yuazer.qlcustomspawn.data.DataLoader
import io.github.yuazer.qlcustomspawn.obj.SpawnCreater
import taboolib.common.io.newFolder
import taboolib.platform.BukkitPlugin

class CreaterManager() {
    private val creaters: MutableMap<String, SpawnCreater> = mutableMapOf()

    // 添加（已存在则先 cancel 再替换）
    fun add(creater: SpawnCreater) {
        creaters[creater.name] = creater
    }

    // 根据名称获取
    fun get(name: String): SpawnCreater? {
        return creaters[name]
    }

    // 删除（并取消任务）
    fun remove(name: String): Boolean {
        return creaters.remove(name) != null
    }

    // 判断是否存在
    fun contains(name: String): Boolean {
        return creaters.containsKey(name)
    }

    // 获取全部容器名称
    fun keys(): Set<String> {
        return creaters.keys
    }

    // 获取全部容器
    fun values(): Collection<SpawnCreater> {
        return creaters.values
    }

    // 清空（并全部 cancel）
    fun clear() {
        creaters.clear()
    }
    fun reload() {
        clear()
        DataLoader.loadCreaterFormFolder(newFolder(BukkitPlugin.getInstance().dataFolder,"creater"))
    }
}
