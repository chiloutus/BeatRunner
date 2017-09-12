package com.holdtheroof.beat.runner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.holdtheroof.beat.runner.game.GameState;
import com.holdtheroof.beat.runner.geometry.Chunk;
import com.holdtheroof.beat.runner.geometry.Ground;
import com.holdtheroof.beat.runner.player.Player;
import com.holdtheroof.beat.runner.player.PlayerState;
import com.holdtheroof.beat.runner.utils.CollisionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class GameScreen implements Screen {
    private static final int PLAYER_JUMP_SPEED = 200;
    private static final float PLAYER_HEIGHT = 128;
    private static final float PLAYER_WIDTH = 128;
    private static final int MAX_PLAYER_SPEED = 300;
    private int currentSpeed = 200;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private List<Chunk> chunks;
    private Player player;
    private Texture background;
    private Sprite backgroundSprite;
    private static final int CHUNK_SIZE = 5;
    private static final int GROUND_WIDTH = 128;
    private static final int GROUND_HEIGHT = 128;


    public GameScreen (final Beat game) {
        batch = new SpriteBatch();

        background = new Texture("background.png");
        backgroundSprite = new Sprite(background);

        chunks = new ArrayList<Chunk>();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        spawnPlayer();
        spawnInitialGeometryChunk();

    }

    private void spawnPlayer() {
        player = new Player(150, 127, new Texture("water1.png"));
        player.setWidth(PLAYER_WIDTH);
        player.setHeight(PLAYER_HEIGHT);
        player.setState(PlayerState.Standing);
    }

    @Override
    public void render (float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        backgroundSprite.draw(batch);
        drawPlayer();
        drawChunks();
        batch.end();

        if(Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
        }

        //If there is a gap to the right that needs some geometry to fill it
        if(getXPositionOfTopofChunk() < (800 - GROUND_WIDTH)) {
            spawnGeometryChunk();
        }
        handleGeometry();
        handlePlayer();
    }

    private void handlePlayer() {
        boolean blocked =false;
        handleJump();
        if(player.getState() != PlayerState.Jumping && player.getState() != PlayerState.PreparingJump) {
            boolean ontopOfNone = true;
            for (Chunk chunk: chunks) {
                for (Ground ground: chunk.getSegments()) {
                    Rectangle intersection = new Rectangle();
                    Intersector.intersectRectangles(ground, player, intersection);
                    if(intersection.y != 0 && intersection.y + intersection.height > player.y && player.y >= 120) {
                        ontopOfNone = false;
                        player.setY(127);
                    }
                    if(intersection.x != 0 && intersection.x + intersection.width > ground.x && player.y < 120) {
                        blocked = true;
                    }
                }
            }
            if(ontopOfNone) {
                player.setState(PlayerState.Falling);
            } else {
                player.setState(PlayerState.Standing);
            }
        }

//        if(player.getX() <= 650 && Gdx.input.isTouched() && !blocked) {
//            player.setX(player.getX() + (150 * Gdx.graphics.getDeltaTime()));
//        }
        if(blocked) {
            player.setX(player.getX() - (400 * Gdx.graphics.getDeltaTime()));
        }


        if(player.getX() < 0 || player.getY() < 0) {
            resetWorld();
        }

    }

    private void resetWorld() {
        chunks = new ArrayList<Chunk>();
        spawnPlayer();
        spawnInitialGeometryChunk();
    }

    private void handleJump() {
        if(Gdx.input.isTouched() && player.getState() == PlayerState.Standing) {
            player.setState(PlayerState.PreparingJump);
            currentSpeed = PLAYER_JUMP_SPEED;
        }
        if(Gdx.input.isTouched() && player.getState() == PlayerState.PreparingJump) {
            if(currentSpeed < MAX_PLAYER_SPEED) {
                currentSpeed += 50;
            } else {
                player.setState(PlayerState.Jumping);
            }
        } else {
            if(player.getState() == PlayerState.PreparingJump) {
                player.setState(PlayerState.Jumping);
            }
        }

        if (player.getState() == PlayerState.Jumping || player.getState() == PlayerState.PreparingJump) {
            player.setY(player.getY() + (currentSpeed * Gdx.graphics.getDeltaTime()));
            if(currentSpeed <= 0) {
                player.setState(PlayerState.Falling);
            }
            currentSpeed -= 10;
        } else if (player.getState() == PlayerState.Falling) {
            player.setY(player.getY() - (currentSpeed * Gdx.graphics.getDeltaTime()));
            currentSpeed += 10;
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
        Texture texture = new Texture("ground0" + MathUtils.random(1, 8) + ".png");
        while (original < 800) {
            Ground ground = new Ground(original, 0, texture);
            ground.width = GROUND_WIDTH;
            ground.height = GROUND_HEIGHT;
            chunk.add(ground);
            original+=(GROUND_WIDTH);
        }
        chunks.add(chunk);
    }


    private void spawnGeometryChunk() {
        Chunk chunk = new Chunk();
        int original = MathUtils.random(900,1300);
        Texture texture = new Texture("ground0" + MathUtils.random(1, 8) + ".png");
        for (int i = 0; i < MathUtils.random(3,CHUNK_SIZE); i++) {
            Ground ground = new Ground(original, 0, texture);
            ground.width = GROUND_WIDTH;
            ground.height = GROUND_HEIGHT;
            chunk.add(ground);
            original+=(GROUND_WIDTH);
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
                if(ground.getX() + GROUND_WIDTH < 0)  {
                    // remove ground pieces that have been moved out of frame
                    chunks.get(i).getSegments().remove(y);
                }
            }

        }
    }

    @Override
    public void show() {

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
    }
}
