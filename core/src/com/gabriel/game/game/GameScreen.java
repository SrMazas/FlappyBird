package com.gabriel.game.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.gabriel.game.Assets;
import com.gabriel.game.MainFlappy;
import com.gabriel.game.screens.Screens;

public class GameScreen extends Screens {
    static final int STATE_READY = 0;
    static final int STATE_RUNNING = 1;
    static final int STATE_GAME_OVER = 2;
    int state;

    public static int maxScore;

    public static Preferences prefs = Gdx.app.getPreferences("My Preferences");


    WorldGame world;
    WorldGameRenderer worldRenderer;

    Image getReady;
    Image tap;
    Image gameOver;

    public GameScreen(MainFlappy game) {
        super(game);
        state = STATE_READY;
        maxScore = prefs.getInteger("maxScore", 0);

        Assets.backMusic.play();

        world = new WorldGame();
        worldRenderer = new WorldGameRenderer(spriteBatch, world);

        getReady = new Image(Assets.getReady);
        getReady.setPosition(SCREEN_WIDTH / 2f - getReady.getWidth() / 2f, 600);

        tap = new Image(Assets.tap);
        tap.setPosition(SCREEN_WIDTH / 2f - tap.getWidth() / 2f, 310);

        gameOver = new Image(Assets.gameOver);
        gameOver.setPosition(SCREEN_WIDTH / 2f - getReady.getWidth() / 2f, 350);

        stage.addActor(getReady);
        stage.addActor(tap);
    }

    @Override
    public void draw(float delta) {
        worldRenderer.render(delta);

        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);

        spriteBatch.begin();

        if (state == STATE_RUNNING) {
            String auxScore = world.score + "";
            float widthScore = Assets.getTextWidthFont(auxScore);
            Assets.font.draw(spriteBatch, auxScore, SCREEN_WIDTH / 1.8f - widthScore , 680);
        }

        if (state == STATE_READY) {
            String auxMaxScore = "Record: " + maxScore;
            float widthMaxScore = Assets.getTextWidthSmallFont(auxMaxScore);
            Assets.fontSmall.draw(spriteBatch, auxMaxScore, SCREEN_WIDTH / 1.3f - widthMaxScore, 170);
        }
        // Assets.font.draw(spriteBatch, ""+world.bodies.size, 10, 200);
        spriteBatch.end();
    }

    @Override
    public void update(float delta) {

        switch (state) {
            case STATE_READY:
                updateReady(delta);
                break;
            case STATE_RUNNING:
                updateRunning(delta);
                break;
            case STATE_GAME_OVER:
                updateGameOver(delta);
                break;

        }
    }

    private void updateReady(float delta) {
        if (Gdx.input.justTouched()) {
            getReady.addAction(Actions.fadeOut(.3f));
            tap.addAction(Actions.sequence(
                    Actions.fadeOut(.3f),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            getReady.remove();
                            tap.remove();
                            state = STATE_RUNNING;
                        }
                    })
            ));
        }

    }

    private void updateRunning(float delta) {
        boolean jump = Gdx.input.justTouched();

        world.update(delta, jump);

        if (world.state == WorldGame.STATE_GAME_OVER) {
            state = STATE_GAME_OVER;
            stage.addActor(gameOver);
        }
    }

    private void updateGameOver(float delta) {
        if (Gdx.input.justTouched()) {
            gameOver.addAction(Actions.sequence(
                    Actions.fadeOut(.3f),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            gameOver.remove();
                            game.setScreen(new GameScreen(game));
                        }
                    })
            ));
        }
    }


}
