package io.github.yuazer.qlcustomspawn

import io.github.yuazer.qlcustomspawn.api.data.ContainerApi
import io.github.yuazer.qlcustomspawn.api.data.CreaterApi
import io.github.yuazer.qlcustomspawn.data.DataLoader
import io.github.yuazer.qlcustomspawn.manager.ContainerManager
import io.github.yuazer.qlcustomspawn.manager.CreaterManager
import io.github.yuazer.qlcustomspawn.runnable.ClearRunnable
import kotlinx.coroutines.delay
import org.bukkit.Bukkit
import taboolib.common.LifeCycle
import taboolib.common.io.newFolder
import taboolib.common.platform.Awake
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.releaseResourceFolder
import taboolib.common.platform.function.submit
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
    lateinit var clearRunnable: ClearRunnable
    fun isRunnableInitialized(): Boolean {
        return ::clearRunnable.isInitialized
    }
    @Awake(LifeCycle.ENABLE)
    fun loadPlugin() {
        loadDir()
        logLoaded()
        Bukkit.getScheduler().runTaskLater(BukkitPlugin.getInstance(), Runnable {
            loadFinal()
        },1L)
    }
    fun loadFinal(){
        containerManager = ContainerManager(BukkitPlugin.getInstance())
        createrManager = CreaterManager()
        DataLoader.loadData()
        createrManager.reload()
        containerManager.reloadAll()
        if (config.getBoolean("auto_start")){
            submit(delay = 20L) {
                containerManager.startAll()
            }
        }
        if (config.getBoolean("clear.auto_start")|| config.getString("clear.auto_start")?.equals("true",true) == true){
            clearRunnable = ClearRunnable(config.getString("clear.mode") ?: "")
            clearRunnable.runTaskTimerAsynchronously(BukkitPlugin.getInstance(), 0L, 20L)
        }else{
            println("auto start:${config.getBoolean("clear.auto_start")}")
            println("auto start_string:${config.getString("clear.auto_start")?.equals("true",true) == true}")
        }
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
