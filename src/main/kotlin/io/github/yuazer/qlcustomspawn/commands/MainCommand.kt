package io.github.yuazer.qlcustomspawn.commands

import io.github.yuazer.qlcustomspawn.Qlcustomspawn
import io.github.yuazer.qlcustomspawn.api.data.ContainerApi
import io.github.yuazer.qlcustomspawn.api.data.CreaterApi
import io.github.yuazer.qlcustomspawn.api.extension.EntityExtension.getLookedLocation
import io.github.yuazer.qlcustomspawn.api.extension.LocationExtension.locToString
import io.github.yuazer.qlcustomspawn.api.extension.RunnableExtension.getTask
import io.github.yuazer.qlcustomspawn.api.extension.RunnableExtension.isCancelledNoChecked
import io.github.yuazer.qlcustomspawn.runnable.ClearRunnable
import io.github.yuazer.qlcustomspawn.utils.LocationUtils.isInArea
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper
import taboolib.platform.BukkitPlugin
import taboolib.platform.util.sendLang

@CommandHeader("qlcustomspawn", ["qlcs"])
object MainCommand {
    @CommandBody(permission = "qlcustomspawn.reload")
    val reload = subCommand {
        execute<CommandSender> { sender, context, argument ->
            Qlcustomspawn.config.reload()
            CreaterApi.getManager().reload()
            ContainerApi.getManager().reloadAll()
            ContainerApi.getManager().startAll()
            if (Qlcustomspawn.isRunnableInitialized()) {
                Qlcustomspawn.clearRunnable.cancel()
            }
            Qlcustomspawn.clearRunnable = ClearRunnable("wait")
            Qlcustomspawn.clearRunnable.runTaskTimerAsynchronously(BukkitPlugin.getInstance(), 0, 20)
            sender.sendLang("reload-message")
        }
    }

    @CommandBody
    val help = subCommand {
        createHelper(true)
    }

    @CommandBody(permission = "qlcustomspawn.debug")
    val debug = subCommand {
        execute<CommandSender> { sender, context, argument ->
            if (sender is Player) {
                sender.sendLang("当前Location的信息是:")
                sender.sendMessage(sender.location.locToString())
            }
        }
    }

    @CommandBody(permission = "qlcustomspawn.getloc")
    val getloc = subCommand {
        execute<CommandSender> { sender, context, argument ->
            if (sender is Player) {
                sender.sendLang("你所看向的Location的信息是:")
                sender.getLookedLocation(10)?.let { sender.sendMessage(it.locToString()) }
                    ?: sender.sendLang("没有检测到你看向的方块")
            }
        }
    }

    @CommandBody(permission = "qlcustomspawn.getspawner")
    val getspawner = subCommand {
        execute<CommandSender> { sender, context, argument ->
            if (sender is Player) {
                ContainerApi.getManager().values().filter { sender.isInArea(it.area.pointA, it.area.pointB) }
                    .forEach {
                        sender.sendMessage(it.toString())
                        println(it.toString())
                    }
            }
        }
    }

}