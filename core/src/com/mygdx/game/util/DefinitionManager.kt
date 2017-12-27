package com.mygdx.game.util

import com.badlogic.gdx.Gdx
import com.moandjiezana.toml.Toml
import com.mygdx.game.objects.NameAmountLink

object DefinitionManager {
    private val resourceFile = Gdx.files.internal("files/resources.toml").file()
    private val biomesFile = Gdx.files.internal("files/biomes.toml").file()
    private val workerFile = Gdx.files.internal("files/workers.toml").file()

    private lateinit var resourceList: Resources
    var resourceMap:HashMap<String, Resource> = hashMapOf()
    var resourceTypeMap:HashMap<String, MutableList<Resource>> = hashMapOf()
    /** A map of biome definitions by name*/
    var biomeMap:HashMap<String, BiomeDef> = hashMapOf()
    /** Links the name of a worker to the worker definition */
    var workerMap:HashMap<String, WorkerDef> = hashMapOf()
    /** A HashMap to link a worker to the type it processes for easy lookup*/
    var workerProcessTypeMap:HashMap<String, WorkerDef> = hashMapOf()

    fun readDefs(){
        val resData = Toml().read(resourceFile)
        resourceList = resData.to(Resources::class.java)
        resourceList.resources.forEach {
            resourceTypeMap.getOrPut(it.type, {mutableListOf()}).add(it)
            resourceMap.put(it.name, it)
        }

        //Apparently anonymous object doesn't work here...
        val biomes = Toml().read(biomesFile).to(BiomesList::class.java).biomes
        biomes.forEach { biomeMap.put(it.type, it) }

        //Make an anonymous object to take in the TOML data...
        val workers = Toml().read(workerFile).to(WorkerList::class.java).workers
        workers.forEach {
            workerMap.put(it.name, it)
            workerProcessTypeMap.put(it.producesType, it)
        }
    }

    class BiomesList{
        var biomes:Array<BiomeDef> = arrayOf()
    }

    class WorkerList{
        var workers:Array<WorkerDef> = arrayOf()
    }

    class Resources{
        lateinit var resources:kotlin.Array<Resource>
    }

    class Resource{
        var name = ""
        var type = ""
        var reqs:Array<NameAmountLink> = arrayOf()
        var marketPrice = 0
    }

    class BiomeDef{
        var type = ""
        var resources:Array<NameAmountLink> = arrayOf()
    }

    class WorkerDef{
        var name = ""
        var initialCost = 0
        var dailyCost = 0
        var gathers = false
        var buysType = ""
        var producesType = ""
        var buyable = true
    }
}