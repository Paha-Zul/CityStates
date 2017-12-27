package com.mygdx.game.util

import com.badlogic.ashley.core.ComponentMapper
import com.mygdx.game.components.*

object Mappers {

    val identity:ComponentMapper<IdentityComponent> = ComponentMapper.getFor(IdentityComponent::class.java)
    val graphic:ComponentMapper<GraphicComponent> = ComponentMapper.getFor(GraphicComponent::class.java)
    val transform:ComponentMapper<TransformComponent> = ComponentMapper.getFor(TransformComponent::class.java)
    val town:ComponentMapper<TownComponent> = ComponentMapper.getFor(TownComponent::class.java)
    val behaviour:ComponentMapper<BehaviourComponent> = ComponentMapper.getFor(BehaviourComponent::class.java)
    val resource:ComponentMapper<ResourceNodeComponent> = ComponentMapper.getFor(ResourceNodeComponent::class.java)
    val worker:ComponentMapper<WorkerComponent> = ComponentMapper.getFor(WorkerComponent::class.java)
    val inventory:ComponentMapper<InventoryComponent> = ComponentMapper.getFor(InventoryComponent::class.java)
    val market:ComponentMapper<MarketComponent> = ComponentMapper.getFor(MarketComponent::class.java)
}