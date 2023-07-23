package com.payrespect.autorotate

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class EventListener : Listener{

    @EventHandler
    fun onClick(event: PlayerInteractEvent){
        if(event.player.inventory.itemInMainHand.type== Material.TRIDENT){
            if(event.clickedBlock != null) {
                if(event.action== Action.LEFT_CLICK_BLOCK){
                    p1(event.player, event.clickedBlock!!.location)
                    event.isCancelled=true
                }
                else if(event.action==Action.RIGHT_CLICK_BLOCK){
                    p2(event.player, event.clickedBlock!!.location)
                    event.isCancelled=true
                }
                else{
                    return
                }


            }
        }
    }

}