package com.mygdx.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.screens.GameScreen;
import com.mygdx.game.util.DefinitionManager;

public class MyGame extends Game {
	public static SpriteBatch batch;
	public static SpriteBatch UIBatch;

	public static ShapeRenderer shapeRenderer;
	public static OrthographicCamera camera;
	public static OrthographicCamera UICamera;

	public static Engine entityEngine;

	public static Stage stage;

	public static BitmapFont defaultFont12;

	@Override
	public void create () {
		batch = new SpriteBatch();
		UIBatch = new SpriteBatch();

		shapeRenderer = new ShapeRenderer();

		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		camera = new OrthographicCamera(width, height);
		UICamera = new OrthographicCamera(width, height);

		new StretchViewport(width, height, camera);
		Viewport UIViewport = new StretchViewport(width, height, UICamera);

		stage = new Stage(UIViewport, UIBatch);

		entityEngine = new Engine();

		DefinitionManager.INSTANCE.readDefs();

		makeFonts();

		setScreen(new GameScreen());
	}

	private void makeFonts(){
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/titillium/Titillium-Regular.otf"));
		parameter.size = 12;
		parameter.genMipMaps = true;
		parameter.minFilter = Texture.TextureFilter.MipMapLinearNearest;
		parameter.magFilter = Texture.TextureFilter.Linear;
		defaultFont12 = generator.generateFont(parameter); // font size 8 pixels
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		MyGame.camera.update();
		MyGame.UICamera.update();

		super.render();

		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
