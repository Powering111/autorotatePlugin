package com.payrespect.autorotate

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class TabCompletion : TabCompleter {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): MutableList<String>? {
        if(command.name=="p3") {
            if (args?.size == 1) {
                return mutableListOf<String>("xmargin", "zmargin", "zcnt")
            }
        }
        return null
    }
}