package com.mygdx.game.util

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.mygdx.game.MyGame
import com.mygdx.game.components.*

object Factory {
    fun createTown(position:Vector2, sprite:Sprite, name:String):Entity{
        val identity = IdentityComponent().apply {
            this.name = name
        }

        val transform = TransformComponent()
        transform.apply {
            dimensions.set(100f, 100f)
            this.position.set(position)
        }

        val graphic = GraphicComponent()
        graphic.apply {
            this.sprite = sprite
        }

        val town = TownComponent()
        town.apply {
            gold = 1000
        }

        val market = MarketComponent()
        market.apply {

        }

        val entity = Entity()
        entity.add(identity)
        entity.add(transform)
        entity.add(graphic)
        entity.add(market)
        entity.add(town)
        MyGame.entityEngine.addEntity(entity)

        return entity
    }

    fun createWorker(position:Vector2, sprite:Sprite, type:String, hiddenAtStart:Boolean = false):Entity{
        val entity = Entity()

        val identity = IdentityComponent().apply {

        }

        val transform = TransformComponent()
        transform.apply {
            dimensions.set(100f, 100f)
            this.position.set(position)
        }

        val graphic = GraphicComponent()
        graphic.apply {
            this.sprite = sprite
            this.hide(hiddenAtStart)
        }

        val worker = WorkerComponent()
        worker.apply {
            this.workerDef = DefinitionManager.workerMap[type]!!
        }

        val behaviour = BehaviourComponent(entity)

        val inventory = InventoryComponent()

        entity.add(identity)
        entity.add(transform)
        entity.add(graphic)
        entity.add(worker)
        entity.add(inventory)
        entity.add(behaviour)
        MyGame.entityEngine.addEntity(entity)

        return entity
    }

    fun createResourceNode(position:Vector2, sprite:Sprite, type:String = ""):Entity{
        val identity = IdentityComponent().apply {

        }

        val transform = TransformComponent()
        transform.apply {
            dimensions.set(100f, 100f)
            this.position.set(position)
        }

        val graphic = GraphicComponent()
        graphic.apply {
            this.sprite = sprite
        }

        val resourceNode = ResourceNodeComponent()
        resourceNode.apply {
            val nodeDef = DefinitionManager.biomeMap[type]
            this.type = type
            this.resources = nodeDef!!.resources.toList()
        }

        val entity = Entity()
        entity.add(identity)
        entity.add(transform)
        entity.add(graphic)
        entity.add(resourceNode)
        MyGame.entityEngine.addEntity(entity)

        return entity
    }
}