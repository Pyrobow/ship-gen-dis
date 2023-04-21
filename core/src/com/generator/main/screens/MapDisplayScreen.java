package com.generator.main.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.generator.main.GeneratorMain;
import com.generator.main.generators.HullGenerator;
import com.generator.main.generators.RequirementsGenerator;
import com.generator.main.generators.RoomGrowthManager;
import com.generator.main.generators.RoomPlacer;
import com.generator.main.objects.MapTile;
import com.generator.main.objects.ShipSpecification;
import com.generator.main.utils.PolygonSubdivider;

public class MapDisplayScreen implements Screen {

    Polygon hullShape;

    final GeneratorMain main;
    final HullGenerator hullGen;
    final RequirementsGenerator reqGen;
    OrthographicCamera camera;
    final PolygonSubdivider subdivider;
    RoomPlacer placer;
    RoomGrowthManager growthManager;
    MapTile[][] baseLayer;
    ShipSpecification specification;

    public MapDisplayScreen(GeneratorMain main) {
        this.main = main;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        this.reqGen = new RequirementsGenerator();
        specification = reqGen.createSpecification();
        this.hullGen = new HullGenerator(specification.getTotalHull(), 0.1F, 3);
        subdivider = new PolygonSubdivider(16);
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
                baseLayer = subdivider.polygonToArray(hullShape);
                placer = new RoomPlacer(baseLayer, specification);
                placer.placeAllRooms();
                growthManager = new RoomGrowthManager(specification, baseLayer);
                growthManager.growAllRooms();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (hullShape != null){
            main.shapeRen.setProjectionMatrix(camera.combined);
            main.shapeRen.begin(ShapeRenderer.ShapeType.Filled);
            for (int i = 0; i < baseLayer.length; i++){
                for (int j = 0; j < baseLayer[0].length; j++){
                    Rectangle rect = baseLayer[i][j].getRect();
                    main.shapeRen.setColor(baseLayer[i][j].getColour());
                    main.shapeRen.rect(rect.getX(),rect.getY(), rect.getWidth(), rect.getHeight());
                }
            }
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
