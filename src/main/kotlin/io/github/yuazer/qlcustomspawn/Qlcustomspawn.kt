package io.github.yuazer.qlcustomspawn

import io.github.yuazer.qlcustomspawn.data.DataLoader
import io.github.yuazer.qlcustomspawn.manager.ContainerManager
import io.github.yuazer.qlcustomspawn.manager.CreaterManager
import taboolib.common.LifeCycle
import taboolib.common.io.newFolder
import taboolib.common.platform.Awake
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.releaseResourceFolder
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigFile
import taboolib.platform.BukkitPlugin

object Qlcustomspawn : Plugin() {
    @Config("config.yml")
    lateinit var config: ConfigFile

    lateinit var containerManager: ContainerManager
    lateinit var createrManager: CreaterManager
    override fun onEnable() {

    }

    @Awake(LifeCycle.ENABLE)
    fun loadPlugin() {
        loadDir()
        containerManager = ContainerManager(BukkitPlugin.getInstance())
        createrManager = CreaterManager()
        DataLoader.loadData()
        if (config.getBoolean("auto-start")){
            containerManager.reloadAll()
        }
        logLoaded()
    }

    @Awake(LifeCycle.DISABLE)
    fun disablePlugin() {

    }

    fun loadDir() {
        //newFolder(getDataFolder(), "marks", create = false)
        val createrDir = newFolder(BukkitPlugin.getInstance().dataFolder, "creater", false)
        if (!createrDir.exists()) {
            createrDir.mkdirs()
            releaseResourceFolder("creater/normal.yml", false)
        }
        val containerDir = newFolder(BukkitPlugin.getInstance().dataFolder, "container", false)
        if (!containerDir.exists()) {
            containerDir.mkdirs()
            releaseResourceFolder("container/test.yml", false)
        }
    }

    override fun onDisable() {

    }

    private fun logLoaded() {
        val description = BukkitPlugin.getInstance().description
        val version = description.version
        val pluginName = description.name
        val authors = description.authors.joinToString(", ")

        BukkitPlugin.getInstance().logger.info(
            """
            
            §8+--------------------------------------------------+
            §8|   §aQL Plugin §7has been §aloaded §7successfully!             §8|
            §8+--------------------------------------------------+
            §7 Name    : §b$pluginName
            §7 Version : §e$version
            §7 Author  : §d$authors
            §8+--------------------------------------------------+
            """.trimIndent()
        )
    }

    private fun logUnload() {
        BukkitPlugin.getInstance().logger.info(
            """
            
            §8+--------------------------------------------------+
            §8|   §cQL Plugin §7has been §cunloaded §7gracefully!             §8|
            §8+--------------------------------------------------+
            """.trimIndent()
        )
    }
}
