package com.generator.main.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.generator.main.GeneratorMain;

public class MainMenuScreen implements Screen {
    final GeneratorMain main;
    OrthographicCamera camera;

    public MainMenuScreen(final GeneratorMain main){
        this.main = main;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 400);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0,0,0.2f,1);

        camera.update();
        main.batch.setProjectionMatrix(camera.combined);

        main.batch.begin();
        main.font.draw(main.batch, "Welcome to ShipGen", 100, 150);
        main.font.draw(main.batch, "Press space to begin", 100, 100);
        main.batch.end();

        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            main.setScreen(new MapDisplayScreen(main));
            //TODO: Implement an intermediary screen for setting input
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
