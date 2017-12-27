package com.mygdx.game.screens

import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.mygdx.game.*
import com.mygdx.game.gui.GUIManager
import com.mygdx.game.systems.BehaviourSystem
import com.mygdx.game.systems.RenderSystem
import com.mygdx.game.systems.TownSystem
import com.mygdx.game.systems.WorkerSystem
import com.mygdx.game.util.DefinitionManager
import com.mygdx.game.util.Factory
import com.mygdx.game.util.Util

class GameScreen : Screen {
    override fun hide() {

    }

    override fun show() {
        MyGame.entityEngine.addSystem(TownSystem())
        MyGame.entityEngine.addSystem(BehaviourSystem())
        MyGame.entityEngine.addSystem(WorkerSystem())
        MyGame.entityEngine.addSystem(RenderSystem(MyGame.batch))

        createTowns()
        createResources()

    }

    private fun createTowns(){
        val town = Factory.createTown(Vector2(200f, 200f), Sprite(Util.createPixel(Color.BLACK, 100, 100)), "town 1")
        val town2 = Factory.createTown(Vector2(800f, 800f), Sprite(Util.createPixel(Color.BLACK, 100, 100)), "town 2")
        val town3 = Factory.createTown(Vector2(1400f, 400f), Sprite(Util.createPixel(Color.BLACK, 100, 100)), "town 3")

        GUIManager.openEntityWindow(town, 200f, 300f)
        GUIManager.openEntityWindow(town2, 700f, 300f)
        GUIManager.openEntityWindow(town3, 1200f, 300f)
    }

    private fun createResources(){
        DefinitionManager.biomeMap.values.forEach {
            //TODO This should be more advanced. Use a world width/height instead?
            val randPosition = Vector2(MathUtils.random(200f, MyGame.camera.viewportWidth-200f), MathUtils.random(200f, MyGame.camera.viewportHeight-200f))
            Factory.createResourceNode(randPosition, Sprite(Util.createPixel(Color.GREEN, 50, 50)), it.type)
        }
    }

    override fun render(delta: Float) {
        MyGame.batch.begin()
        MyGame.entityEngine.update(delta)
        MyGame.batch.end()

        GUIManager.update(delta)
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun resize(width: Int, height: Int) {
    }

    override fun dispose() {

    }
}