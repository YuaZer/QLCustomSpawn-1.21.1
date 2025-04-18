package io.github.yuazer.qlcustomspawn.api.extension

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.command.argument.PokemonPropertiesArgumentType
import com.cobblemon.mod.common.util.getPlayer
import com.mojang.brigadier.StringReader
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptCommandSender
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptOptions
import taboolib.module.kether.runKether
import taboolib.platform.compat.replacePlaceholder

object PlayerExtension {

    fun List<String>.runKether(player: Player) {
        val script = this
        runKether(script, detailError = true) {
            KetherShell.eval(
                script.replacePlaceholder(player),
                options = ScriptOptions(sender = adaptCommandSender(player))
            )
        }
    }


    fun replaceInList(strings: List<String>, oldChar: String, newChar: String): List<String> {
        return strings.map { it.replace(oldChar, newChar) }
    }
    fun parseArgs(context: String): PokemonProperties {
        val parse = PokemonPropertiesArgumentType.properties().parse(StringReader(context))
        return parse
    }
    fun createPokemon(context: String,player: Player){
        parseArgs(context).create(player.uniqueId.getPlayer())
    }
}