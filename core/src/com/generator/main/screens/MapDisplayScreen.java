package com.generator.main.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.ScreenUtils;
import com.generator.main.GeneratorMain;
import com.generator.main.generators.HullGenerator;
import com.generator.main.generators.RequirementsGenerator;
import com.generator.main.objects.ShipSpecification;

public class MapDisplayScreen implements Screen {

    Polygon hullShape;

    final GeneratorMain main;
    final HullGenerator hullGen;
    final RequirementsGenerator reqGen;
    OrthographicCamera camera;

    public MapDisplayScreen(GeneratorMain main) {
        this.main = main;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        this.reqGen = new RequirementsGenerator();
        ShipSpecification specification = reqGen.createSpecification();
        this.hullGen = new HullGenerator(specification.getTotalHull(), 0.1F, 3);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        handleInput();
        ScreenUtils.clear(0, 0, 0.2f, 1);
        if (Gdx.input.isKeyJustPressed(Input.Keys.G)){
            try {
                hullShape = this.hullGen.generateSymmetricHull();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (hullShape != null){
            main.shapeRen.setProjectionMatrix(camera.combined);
            main.shapeRen.begin(ShapeRenderer.ShapeType.Line);
            main.shapeRen.setColor(Color.PINK);
            main.shapeRen.polygon(hullShape.getVertices());
            main.shapeRen.end();
        }

        // tell the camera to update its matrices.
        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        main.batch.setProjectionMatrix(camera.combined);
    }

    private void handleInput(){
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.zoom += 0.02;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            camera.zoom -= 0.02;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.translate(-3, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.translate(3, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.translate(0, -3, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.translate(0, 3, 0);
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
