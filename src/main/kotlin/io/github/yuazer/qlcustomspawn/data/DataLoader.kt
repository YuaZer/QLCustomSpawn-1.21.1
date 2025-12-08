package io.github.yuazer.qlcustomspawn.data

import io.github.yuazer.qlcustomspawn.api.data.ContainerApi
import io.github.yuazer.qlcustomspawn.api.data.CreaterApi
import io.github.yuazer.qlcustomspawn.obj.runnable.SpawnContainer
import io.github.yuazer.qlcustomspawn.obj.SpawnCreater
import taboolib.common.io.newFolder
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import taboolib.platform.BukkitPlugin
import java.io.File

object DataLoader {
    fun loadData() {
        //加载creater
        val createrFolder = newFolder(BukkitPlugin.getInstance().dataFolder, "creater")
        loadCreaterFormFolder(createrFolder)
        //加载container
        val containerFolder = newFolder(BukkitPlugin.getInstance().dataFolder, "container")
        loadContainerFormFolder(containerFolder)
    }

    private fun loadContainer(file: File) {
        val yamlConfiguration = Configuration.loadFromFile(file, Type.YAML)
        val name = file.name.replace(".yml", "")
        val spawnContainer = SpawnContainer.fromConfiguration(name, yamlConfiguration)
        if (spawnContainer != null) {
            ContainerApi.getManager().add(spawnContainer)
        }
    }

    //递归读取子文件夹配置文件
    fun loadContainerFormFolder(folder: File) {
        folder.walk()
            .filter { it.isFile }
            .filter { it.extension == "yaml" || it.extension == "yml" }
            .forEach {
                loadContainer(it)
            }
    }

    private fun loadCreater(file: File) {
        val yamlConfiguration = Configuration.loadFromFile(file, Type.YAML)
        val name = file.name.replace(".yml", "")
        val spawnCreater = SpawnCreater(name, yamlConfiguration)
        CreaterApi.getManager().add(spawnCreater)
    }

    fun loadCreaterFormFolder(folder: File) {
        folder.walk()
            .filter { it.isFile }
            .filter { it.extension == "yaml" || it.extension == "yml" }
            .forEach {
                loadCreater(it)
            }
    }


}