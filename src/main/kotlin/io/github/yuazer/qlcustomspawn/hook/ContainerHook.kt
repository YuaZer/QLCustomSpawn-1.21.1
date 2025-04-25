package io.github.yuazer.qlcustomspawn.hook

import io.github.yuazer.qlcustomspawn.api.data.ContainerApi
import org.bukkit.entity.Player
import taboolib.platform.compat.PlaceholderExpansion

object ContainerHook : PlaceholderExpansion {
    // 变量前缀
    override val identifier: String = "qlcustomspawn"

    // 变量操作
    override fun onPlaceholderRequest(player: Player?, args: String): String {
        if (player == null) {
            return "player null"
        }
        val containerName = args.split("<>")[0]
        val request = args.split("<>")[1]
        return ContainerApi.getManager().get(containerName)?.let {
            when (request) {
                "name" -> it.name
                "period" -> it.period.toString()
                "timeCount" -> it.timeCount.toString()
                else -> "null"
            }
        } ?: "null"
    }
}