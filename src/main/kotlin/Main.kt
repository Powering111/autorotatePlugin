package com.payrespect.autorotate

import org.bukkit.plugin.java.JavaPlugin

class Plugin : JavaPlugin(){
    override fun onEnable(){
        println("[Teleport request] Plugin enabled!")

        server.pluginManager.registerEvents(EventListener(),this)

        val commandDispatcher = CommandDispatcher(this)
        val tabCompletion = TabCompletion()
        for(k in arrayOf("p1","p2","p3","reg","rot","water","cur","pr")){
            getCommand(k)?.setExecutor(commandDispatcher)
            getCommand(k)?.tabCompleter = tabCompletion
        }

    }
}