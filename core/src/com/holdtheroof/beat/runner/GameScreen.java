package com.holdtheroof.beat.runner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.holdtheroof.beat.runner.geometry.Chunk;
import com.holdtheroof.beat.runner.geometry.Ground;
import com.holdtheroof.beat.runner.player.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by gary on 05/09/17.
 */

class GameScreen implements Screen {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Texture dropImage;
    private Texture bucketImage;
    private Sound dropSound;
    private Music rainMusic;
    private List<Chunk> chunks;
    private Player player;
    private static final int CHUNK_SIZE = 5;
    private static final int GROUND_WIDTH = 64;
    private static final int GROUND_HEIGHT = 64;


    public GameScreen (final Beat game) {
        batch = new SpriteBatch();

        chunks = new ArrayList<Chunk>();

        rainMusic.setLooping(true);
        rainMusic.play();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        spawnPlayer();
        spawnInitialGeometryChunk();

    }

    private void spawnPlayer() {
        player = new Player(500, 128, new Texture("_water/water1.png"));
    }

    @Override
    public void render (float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawPlayer();
        drawChunks();
        batch.end();

        if(Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
        }

        //If there is a gap to the right that needs some geometry to fill it
        if(getXPositionOfTopofChunk() < (800-128)) {
            spawnGeometryChunk();
        }
        handleGeometry();
        handlePlayer();
    }

    private void handlePlayer() {
//        if(player.getX() < 200) {
//            player
//        }
        if(!Gdx.input.isTouched()) {
            player.setX(player.getX() - (400 * Gdx.graphics.getDeltaTime()));
        }

        if(player.getX() < 0) {
            //game over?
        }

    }

    private void drawPlayer() {
        batch.draw(player.getTexture(), player.getX(), player.getY());
    }

    private float getXPositionOfTopofChunk() {
        return chunks.get(chunks.size()-1).getHead().getX();
    }

    private void drawChunks() {
        for(Chunk chunk: chunks) {
            for (Ground ground : chunk.getSegments()) {
                batch.draw(ground.getTexture(), ground.getX(), ground.getY());
            }
        }
    }

    private void spawnInitialGeometryChunk() {
        Chunk chunk = new Chunk();
        int original = 0;
        Texture texture = new Texture("_ground/ground0" + MathUtils.random(1, 8) + ".png");
        for (int i = 0; i < CHUNK_SIZE; i++) {
            Ground ground = new Ground(original, 0, texture);
            ground.width = GROUND_WIDTH;
            ground.height = GROUND_HEIGHT;
            chunk.add(ground);
            original+=(GROUND_WIDTH*2);
        }
        chunks.add(chunk);
    }


    private void spawnGeometryChunk() {
        Chunk chunk = new Chunk();
        int original = 928;
        Texture texture = new Texture("_ground/ground0" + MathUtils.random(1, 8) + ".png");
        for (int i = 0; i < CHUNK_SIZE; i++) {
            Ground ground = new Ground(original, 0, texture);
            ground.width = GROUND_WIDTH;
            ground.height = GROUND_HEIGHT;
            chunk.add(ground);
            original+=(GROUND_WIDTH*2);
        }
        chunks.add(chunk);
    }

    private void handleGeometry() {
        for (int i = 0; i < chunks.size(); i++) {
            Chunk chunk = chunks.get(i);
            for (int y = 0; y < chunk.getSegments().size(); y++) {
                Ground ground = chunk.getSegments().get(y);
                Ground currentGround = chunks.get(i).getSegments().get(y);
                currentGround.setX(currentGround.getX() - (400 * Gdx.graphics.getDeltaTime() ));
                if(ground.getX() + 128 < 0)  {
                    // remove ground pieces that have been moved out of frame
                    chunks.get(i).getSegments().remove(y);
                }
            }

        }
    }

    @Override
    public void show() {
        rainMusic.play();
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
    public void dispose () {
        batch.dispose();
        dropImage.dispose();
        bucketImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
    }
}
