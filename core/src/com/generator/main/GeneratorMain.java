package com.generator.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.generator.main.screens.MainMenuScreen;

public class GeneratorMain extends Game {
	public SpriteBatch batch;
	public BitmapFont font;
	public ShapeRenderer shapeRen;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		font = new BitmapFont();
		shapeRen = new ShapeRenderer();
		this.setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		font.dispose();
	}
}
