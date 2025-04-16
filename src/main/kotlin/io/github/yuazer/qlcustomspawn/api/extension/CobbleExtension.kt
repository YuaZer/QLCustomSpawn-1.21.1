package io.github.yuazer.qlcustomspawn.api.extension

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.command.argument.PokemonPropertiesArgumentType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.getPlayer
import com.mojang.brigadier.StringReader
import io.github.yuazer.qlcustomspawn.api.extension.NMSExtension.getNMSWorld
import net.minecraft.world.level.Level
import org.bukkit.Location
import org.bukkit.entity.Player

object CobbleExtension {
    private fun parseArgs(context: String): PokemonProperties {
        val parse = PokemonPropertiesArgumentType.properties().parse(StringReader(context))
        return parse
    }

    fun String.createPokemon(player: Player): Pokemon {
        return parseArgs(this).create(player.uniqueId.getPlayer())
    }

    fun String.createPokemon(location: Location): PokemonEntity? {
        val worldServer = location.world?.getNMSWorld()
        val createEntity: PokemonEntity = parseArgs(this).createEntity(worldServer as Level)
        createEntity.moveTo(location.x, location.y, location.z, createEntity.yRot, createEntity.xRot)
        if (worldServer.addFreshEntity(createEntity)) {
            return createEntity
        }
        return null
    }
}