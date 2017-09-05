package com.holdtheroof.beat.runner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.holdtheroof.beat.runner.geometry.Ground;

import java.util.Iterator;

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
    private Array<Ground> geometry;


    public GameScreen (final Beat game) {
        batch = new SpriteBatch();
        dropImage = new Texture("droplet.png");
        bucketImage = new Texture("bucket.png");
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

        geometry = new Array<Ground>();

        rainMusic.setLooping(true);
        rainMusic.play();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        spawnGeometry();

    }

    @Override
    public void render (float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for(Ground ground: geometry) {
            batch.draw(ground.getTexture(), ground.getX(), ground.getY());
        }
        batch.end();

        if(Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
        }

        if(geometry.get(geometry.size - 1).getX() < (800-128)) {
            spawnGeometry();
        }
        handleGeometry();
    }


    private void spawnGeometry() {
        Ground ground = new Ground(800, 100);
        ground.width = 64;
        ground.height = 64;
        geometry.add(ground);
    }

    private void handleGeometry() {
        Iterator<Ground> iter = geometry.iterator();
        while(iter.hasNext()) {
            Ground ground = iter.next();
            ground.setX( ground.getX() - (200 * Gdx.graphics.getDeltaTime()));
            if(ground.getX() + 128 < 0) iter.remove();
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
