package com.payrespect.autorotate

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.lang.Integer.parseInt
import kotlin.math.max
import kotlin.math.min

class CommandDispatcher(val plugin: Plugin) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean{
        when(command.name) {
            "p1" -> {
                p1(sender as Player, sender.location)
                return true
            }
            "p2" -> {
                p2(sender as Player, sender.location)
                return true
            }
            "p3" ->{
                if(args.size==2) {
                    if (args[0] == "xmargin") {
                        val data:Data= PlayerData.dataOf(sender as Player)
                        data.xmargin=parseInt(args[1])
                        PlayerData.setData(sender,data)
                        sender.sendMessage(Component.text("xmargin is set to ${parseInt(args[1])}", Style.style(TextColor.color(255,255,0))))
                    }
                    else if (args[0] == "zmargin") {
                        val data:Data= PlayerData.dataOf(sender as Player)
                        data.zmargin=parseInt(args[1])
                        PlayerData.setData(sender,data)
                        sender.sendMessage(Component.text("zmargin is set to ${parseInt(args[1])}", Style.style(TextColor.color(255,255,0))))
                    }
                    else if (args[0] == "zcnt") {
                        val data:Data= PlayerData.dataOf(sender as Player)
                        data.zcnt=parseInt(args[1])
                        PlayerData.setData(sender,data)

                        sender.sendMessage(Component.text("zcnt is set to ${parseInt(args[1])}", Style.style(TextColor.color(255,255,0))))
                    }
                    else{
                        return false
                    }
                    return true
                }
                else if(args.size==0){
                    val data:Data = PlayerData.dataOf(sender as Player)
                    sender.sendMessage(Component.text("xmargin: ${data.xmargin}, zmargin: ${data.zmargin}, zcnt: ${data.zcnt}",Style.style(TextColor.color(0,200,0))))
                    return true
                }
            }
            "rot"->{
                sender.server.broadcast(Component.text("[NOTICE] Automatically applying rotation...",Style.style(TextColor.color(255,255,0))))

                sender.server.scheduler.runTaskLater(plugin,fun(){
                    val data = PlayerData.dataOf(sender as Player)
                    val pos1:Vec3i = Vec3i(min(data.pos1.x,data.pos2.x), min(data.pos1.y,data.pos2.y),min(data.pos1.z,data.pos2.z))
                    val pos2:Vec3i = Vec3i(max(data.pos1.x,data.pos2.x), max(data.pos1.y,data.pos2.y),max(data.pos1.z,data.pos2.z))
                    for(j in 1..data.zcnt){
                        rotX(sender.world, pos1, pos2, Vec3i(pos1.x-1*data.xmargin,pos1.y,pos1.z))
                        rotZ(sender.world, Vec3i(pos1.x-1*data.xmargin,pos1.y,pos1.z),
                            Vec3i(pos2.x-1*data.xmargin,pos2.y,pos2.z),
                            Vec3i(pos1.x-2*data.xmargin,pos1.y,pos1.z))
                        rotZ(sender.world, Vec3i(pos1.x-2*data.xmargin,pos1.y,pos1.z),
                            Vec3i(pos2.x-2*data.xmargin,pos2.y,pos2.z),
                            Vec3i(pos1.x-3*data.xmargin,pos1.y,pos1.z))
                        rotZ(sender.world, Vec3i(pos1.x-3*data.xmargin,pos1.y,pos1.z),
                            Vec3i(pos2.x-3*data.xmargin,pos2.y,pos2.z),
                            Vec3i(pos1.x-4*data.xmargin,pos1.y,pos1.z))
                        rotX(sender.world, Vec3i(pos1.x-3*data.xmargin,pos1.y,pos1.z),
                            Vec3i(pos2.x-3*data.xmargin,pos2.y,pos2.z),
                            Vec3i(pos1.x-5*data.xmargin,pos1.y,pos1.z))

                        pos1.z-=data.zmargin
                        pos2.z-=data.zmargin
                    }
                    sender.server.broadcast(Component.text("[NOTICE] DONE!",Style.style(TextColor.color(255,255,0))))
                },0)

                return true
            }
            "reg"->{
                val data = PlayerData.dataOf(sender as Player)
                val pos1:Vec3i = Vec3i(min(data.pos1.x,data.pos2.x), min(data.pos1.y,data.pos2.y),min(data.pos1.z,data.pos2.z))
                val pos2:Vec3i = Vec3i(max(data.pos1.x,data.pos2.x), max(data.pos1.y,data.pos2.y),max(data.pos1.z,data.pos2.z))

                sender.sendMessage(Component.text("Selected region:\n${pos1.x} ${pos1.y} ${pos1.z} \n ${pos2.x} ${pos2.y} ${pos2.z}",Style.style(TextColor.color(0,200,0))))

                displayOutline((sender as Player).world, pos1, pos2)
                return true
            }
            "water"->{
                (sender as Player).world.getBlockAt(sender.location).setType(Material.WATER,false)
                return true
            }
            "cur"->{
                val loc=(sender as Player).location
                val data:Data = PlayerData.dataOf(sender as Player)
                val p1= data.pos1
                val p2= data.pos2
                val pos0 = Vec3i(max(p1.x,p2.x),max(p1.y,p2.y),max(p1.z,p2.z))
                val xmargin= data.xmargin
                val zmargin= data.zmargin
                val zcnt = data.zcnt

                val currentX = (pos0.x-loc.x).toInt()/xmargin +1
                val currentZ = (pos0.z-loc.z).toInt()/zmargin +1

                if(pos0.x-loc.x<0 || pos0.z-loc.z<0){
                    sender.sendMessage(Component.text("You are not on a valid area."))
                    return true
                }

                if(currentX>=1 && currentX <=6 && currentZ>=1 && currentZ<=zcnt){
                    // in the valid area
                    sender.sendMessage(Component.text("Performing on X${currentX} Z${currentZ}",Style.style(TextColor.color(255,255,0))))
                    applyRot((sender as Player).world, data, currentX, currentZ)
                    plugin.server.broadcast(Component.text("${(sender.name)} Invoked /cur at (${currentX},${currentZ}).",Style.style(TextColor.color(255,255,0))))
                }
                else{
                    sender.sendMessage(Component.text("You are not on a valid area."))
                }
                return true
            }
            "pr"->{
                PlayerData.resetData(sender as Player)
                sender.sendMessage(Component.text("Position and property data reset.",Style.style(TextColor.color(255,255,0))))
                return true
            }
        }
        return false
    }

    fun applyRot(world:World, data:Data, gridX:Int, gridZ:Int){
        println("Performing : ${gridX} ${gridZ}")

        when(gridX){
            1->{
                runRot(this::rotX, world, data, gridZ, 1, 2)
                runRot(this::rotZ, world, data, gridZ, 2, 3)
                runRot(this::rotZ, world, data, gridZ, 3, 4)
                runRot(this::rotZ, world, data, gridZ, 4, 5)
                runRot(this::rotX, world, data, gridZ, 4, 6)
            }
            2->{
                runRot(this::irotX, world, data, gridZ, 2, 1)

                runRot(this::rotZ, world, data, gridZ, 2, 3)
                runRot(this::rotZ, world, data, gridZ, 3, 4)
                runRot(this::rotZ, world, data, gridZ, 4, 5)
                runRot(this::rotX, world, data, gridZ, 4, 6)
            }
            3->{
                runRot(this::irotZ, world, data, gridZ, 3, 2)
                runRot(this::irotX, world, data, gridZ, 2, 1)

                runRot(this::rotZ, world, data, gridZ, 3, 4)
                runRot(this::rotZ, world, data, gridZ, 4, 5)
                runRot(this::rotX, world, data, gridZ, 4, 6)
            }
            4->{
                runRot(this::irotZ, world, data, gridZ, 4, 3)
                runRot(this::irotZ, world, data, gridZ, 3, 2)
                runRot(this::irotX, world, data, gridZ, 2, 1)

                runRot(this::rotZ, world, data, gridZ, 4, 5)
                runRot(this::rotX, world, data, gridZ, 4, 6)
            }
            5->{
                runRot(this::irotZ, world, data, gridZ, 5, 4)
                runRot(this::irotZ, world, data, gridZ, 4, 3)
                runRot(this::irotZ, world, data, gridZ, 3, 2)
                runRot(this::irotX, world, data, gridZ, 2, 1)

                runRot(this::rotX, world, data, gridZ, 4, 6)
            }
            6->{
                runRot(this::irotX, world, data, gridZ, 6, 4)
                runRot(this::rotZ, world, data, gridZ, 4, 5)
                runRot(this::irotZ, world, data, gridZ, 4, 3)
                runRot(this::irotZ, world, data, gridZ, 3, 2)
                runRot(this::irotX, world, data, gridZ, 2, 1)
            }
        }
    }

    // grid based rotation
    fun runRot(toRun:(World,Vec3i,Vec3i,Vec3i) -> Unit, world:World, data:Data, gridZ:Int, fromX:Int, toX:Int){
        val pos1:Vec3i = Vec3i(min(data.pos1.x,data.pos2.x), min(data.pos1.y,data.pos2.y),min(data.pos1.z,data.pos2.z))
        val pos2:Vec3i = Vec3i(max(data.pos1.x,data.pos2.x), max(data.pos1.y,data.pos2.y),max(data.pos1.z,data.pos2.z))
        toRun(world,
            Vec3i(pos1.x-(fromX-1)*data.xmargin,pos1.y,pos1.z-(gridZ-1)*data.zmargin),
            Vec3i(pos2.x-(fromX-1)*data.xmargin,pos2.y,pos2.z-(gridZ-1)*data.zmargin),
            Vec3i(pos1.x-(toX-1)*data.xmargin,pos1.y,pos1.z-(gridZ-1)*data.zmargin))
    }

    fun copyBlock(world:World, from:Vec3i, to:Vec3i){
        if(world.getBlockAt(from.x,from.y,from.z).type in ignoredMaterial || world.getBlockAt(to.x,to.y,to.z).type in ignoredMaterial);
        else world.getBlockAt(to.x,to.y,to.z).setBlockData(world.getBlockAt(from.x,from.y,from.z).blockData,false)
    }

    // copy <pos1>-<pos2> volume to <where> and rotate clockwise X axis
    fun rotX(world:World, pos1:Vec3i, pos2:Vec3i, where:Vec3i) {
        val dx = pos2.x - pos1.x
        val dy = pos2.y - pos1.y
        val dz = pos2.z - pos1.z

        var jy = where.y
        for (iz in 0..dz) {
            var jz = where.z+dz
            for (iy in 0..dy) {
                var jx = where.x
                for (ix in 0..dx) {
                    copyBlock(world, Vec3i(pos1.x+ix,pos1.y+iy,pos1.z+iz), Vec3i(jx,jy,jz))
                    jx++
                }
                jz--
            }
            jy++
        }
    }

    // counterclockwise
    fun irotX(world:World, pos1:Vec3i, pos2:Vec3i, where:Vec3i){
        val dx = pos2.x - pos1.x
        val dy = pos2.y - pos1.y
        val dz = pos2.z - pos1.z

        var jy = where.y+dy
        for (iz in 0..dz) {
            var jz = where.z
            for (iy in 0..dy) {
                var jx = where.x
                for (ix in 0..dx) {
                    copyBlock(world, Vec3i(pos1.x+ix,pos1.y+iy,pos1.z+iz), Vec3i(jx,jy,jz))
                    jx++
                }
                jz++
            }
            jy--
        }
    }

    fun rotZ(world:World, pos1:Vec3i, pos2:Vec3i, where:Vec3i) {
        val dx = pos2.x - pos1.x
        val dy = pos2.y - pos1.y
        val dz = pos2.z - pos1.z

        var jz = where.z
        for (iz in 0..dz) {
            var jx = where.x
            for (iy in 0..dy) {
                var jy = where.y+dy
                for (ix in 0..dx) {
                    copyBlock(world, Vec3i(pos1.x+ix,pos1.y+iy,pos1.z+iz), Vec3i(jx,jy,jz))
                    jy--
                }
                jx++
            }
            jz++
        }
    }

    fun irotZ(world:World, pos1:Vec3i, pos2:Vec3i, where:Vec3i) {
        val dx = pos2.x - pos1.x
        val dy = pos2.y - pos1.y
        val dz = pos2.z - pos1.z

        var jz = where.z
        for (iz in 0..dz) {
            var jx = where.x+dx
            for (iy in 0..dy) {
                var jy = where.y
                for (ix in 0..dx) {
                    copyBlock(world, Vec3i(pos1.x+ix,pos1.y+iy,pos1.z+iz), Vec3i(jx,jy,jz))
                    jy++
                }
                jx--
            }
            jz++
        }
    }
}

fun p1(player:Player, location: Location){
    val b = player.world.getBlockAt(location)
    val data:Data= PlayerData.dataOf(player)
    data.pos1=Vec3i(b.x,b.y,b.z)
    PlayerData.setData(player, data)

    player.sendMessage(Component.text("First position is set to ${b.x} / ${b.y} / ${b.z}", Style.style(TextColor.color(255,255,0))))
}
fun p2(player:Player, location:Location){
    val b = player.world.getBlockAt(location)
    val data:Data= PlayerData.dataOf(player)
    data.pos2=Vec3i(b.x,b.y,b.z)
    PlayerData.setData(player, data)

    player.sendMessage(Component.text("Second position is set to ${b.x} / ${b.y} / ${b.z}", Style.style(TextColor.color(255,255,0))))
    displayOutline(player.world, data.pos1, data.pos2)
}

fun displayOutline(world:World,start:Vec3i,end:Vec3i){
    val pos1:Vec3i = Vec3i(min(start.x,end.x), min(start.y,end.y),min(start.z,end.z))
    val pos2:Vec3i = Vec3i(max(start.x,end.x)+1,max(start.y,end.y)+1,max(start.z,end.z)+1)
    for(x in pos1.x..pos2.x) {
        world.spawnParticle(Particle.FLAME, x.toDouble(),pos1.y.toDouble(),pos1.z.toDouble(), 1,
            0.0,0.0,0.0, 0.0, null)
        world.spawnParticle(Particle.FLAME, x.toDouble(),pos2.y.toDouble(),pos2.z.toDouble(), 1,
            0.0,0.0,0.0, 0.0, null)
        world.spawnParticle(Particle.FLAME, x.toDouble(),pos2.y.toDouble(),pos1.z.toDouble(), 1,
            0.0,0.0,0.0, 0.0, null)
        world.spawnParticle(Particle.FLAME, x.toDouble(),pos1.y.toDouble(),pos2.z.toDouble(), 1,
            0.0,0.0,0.0, 0.0, null)
    }
    for(y in pos1.y..pos2.y) {
        world.spawnParticle(Particle.FLAME, pos1.x.toDouble(),y.toDouble(),pos1.z.toDouble(), 1,
            0.0,0.0,0.0, 0.0, null)
        world.spawnParticle(Particle.FLAME, pos2.x.toDouble(),y.toDouble(),pos2.z.toDouble(), 1,
            0.0,0.0,0.0, 0.0, null)
        world.spawnParticle(Particle.FLAME, pos2.x.toDouble(),y.toDouble(),pos1.z.toDouble(), 1,
            0.0,0.0,0.0, 0.0, null)
        world.spawnParticle(Particle.FLAME, pos1.x.toDouble(),y.toDouble(),pos2.z.toDouble(), 1,
            0.0,0.0,0.0, 0.0, null)
    }
    for(z in pos1.z..pos2.z) {
        world.spawnParticle(Particle.FLAME, pos1.x.toDouble(),pos1.y.toDouble(),z.toDouble(), 1,
            0.0,0.0,0.0, 0.0, null)
        world.spawnParticle(Particle.FLAME, pos2.x.toDouble(),pos2.y.toDouble(),z.toDouble(), 1,
            0.0,0.0,0.0, 0.0, null)
        world.spawnParticle(Particle.FLAME, pos2.x.toDouble(),pos1.y.toDouble(),z.toDouble(), 1,
            0.0,0.0,0.0, 0.0, null)
        world.spawnParticle(Particle.FLAME, pos1.x.toDouble(),pos2.y.toDouble(),z.toDouble(), 1,
            0.0,0.0,0.0, 0.0, null)
    }
}