package com.payrespect.autorotate

import io.papermc.paper.math.BlockPosition
import io.papermc.paper.math.Position
import org.bukkit.Material
import org.bukkit.entity.Player

object PlayerData{
    val data:MutableMap<Player,Data> = mutableMapOf<Player,Data>()
    fun dataOf(player:Player):Data{
        return data.getOrDefault(player,Data())
    }
    fun setData(player:Player, dat:Data){
        data[player]=dat
    }
    fun resetData(player:Player){
        data[player]=Data()
    }
}
data class Data(
    var pos1: Vec3i=Vec3i(0,11,1),
    var pos2: Vec3i=Vec3i(-11, 0, -10),
    var xmargin: Int =20,
    var zmargin: Int =20,
    var zcnt: Int =8
    )

val ignoredMaterial :Array<Material> = arrayOf(Material.LADDER, Material.VINE)

data class Vec3i(var x:Int =0, var y:Int =0, var z:Int =0){
    operator fun plus(a:Vec3i):Vec3i
    = Vec3i(a.x+this.x,a.y+this.y,a.z+this.z)

    operator fun minus(a:Vec3i):Vec3i
            = Vec3i(this.x-a.x,this.y-a.y,this.z-a.z)

    operator fun times(a:Int):Vec3i
            = Vec3i(this.x*a,this.y*a,this.z*a)
}
