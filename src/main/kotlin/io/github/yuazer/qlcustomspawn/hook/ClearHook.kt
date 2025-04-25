package io.github.yuazer.qlcustomspawn.hook

import io.github.yuazer.qlcustomspawn.Qlcustomspawn
import org.bukkit.entity.Player
import taboolib.platform.compat.PlaceholderExpansion

object ClearHook:PlaceholderExpansion {
    override val identifier: String = "qlcustomspawnclear"

    override fun onPlaceholderRequest(player: Player?, args: String): String {
        if (player == null) {
            return "player null"
        }
        if (args.equals("time",true)){
            return Qlcustomspawn.clearRunnable.counter.toString()
        }else{
            return "args error"
        }
    }
}