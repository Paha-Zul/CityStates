package com.mygdx.game.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.util.Mappers
import com.mygdx.game.components.GraphicComponent
import com.mygdx.game.components.TransformComponent
import java.util.*

/**
 * Created by Paha on 1/16/2017.
 * An EntitySystem that handles drawing graphics
 */
class RenderSystem(val batch:SpriteBatch) : EntitySystem(){
    lateinit var entities: ImmutableArray<Entity>
    lateinit var sortedEntities:List<Entity>

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)

        val family = Family.all(GraphicComponent::class.java, TransformComponent::class.java).get()
        entities = engine.getEntitiesFor(family)
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        //TODO This could possibly get super laggy. Might need a better way to deal with this...
        sortedEntities = entities.sortedWith(Comparator<Entity> { o1, o2 ->
            val tc1 = Mappers.transform[o1]
            val tc2 = Mappers.transform[o2]
            when {
                tc2.position.y - tc1.position.y < 0 -> -1
                tc2.position.y - tc1.position.y > 0 -> 1
                else -> 0
            }
        })

        sortedEntities.forEach { ent ->
            val gc = Mappers.graphic.get(ent)
            val tc = Mappers.transform.get(ent)

            gc.sprite.setPosition(tc.position.x - gc.anchor.x*gc.sprite.width, tc.position.y - gc.anchor.y*gc.sprite.height)

            if(!gc.hidden)
                gc.sprite.draw(batch)
        }
    }
}