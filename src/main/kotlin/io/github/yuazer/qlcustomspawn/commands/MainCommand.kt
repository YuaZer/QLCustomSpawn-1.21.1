package io.github.yuazer.qlcustomspawn.commands

import io.github.yuazer.qlcustomspawn.Qlcustomspawn
import io.github.yuazer.qlcustomspawn.api.data.ContainerApi
import io.github.yuazer.qlcustomspawn.api.data.CreaterApi
import io.github.yuazer.qlcustomspawn.api.extension.LocationExtension.locToString
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper
import taboolib.platform.util.sendLang

@CommandHeader("qlcustomspawn", ["qlcs"])
object MainCommand {
    @CommandBody(permission = "qlcustomspawn.reload")
    val reload = subCommand {
        execute<CommandSender> { sender, context, argument ->
            Qlcustomspawn.config.reload()
            CreaterApi.getManager().reload()
            ContainerApi.getManager().reloadAll()
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
            if (sender is Player){
                sender.sendLang("当前Location的信息是:")
                sender.sendMessage(sender.location.locToString())
            }
        }
    }
}