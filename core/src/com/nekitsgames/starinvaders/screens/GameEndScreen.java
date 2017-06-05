/*
* Copyright (c) 20016 - 2017, NG Tech and/or its affiliates. All rights reserved.
* GNI GPL v3 licence . Use is subject to license terms
*/

package com.nekitsgames.starinvaders.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;
import com.nekitsgames.starinvaders.API.logAPI.LogSystem;
import com.nekitsgames.starinvaders.StarInvaders;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Game end screen
 *
 * @author Nikita Serba
 * @version 1.0
 * @since 1.1
 */
public class GameEndScreen implements Screen {

    private static String label;
    private static String[] menuLables;
    private static int menuLabelsX;
    private static double menuLabelXAdd;
    private StarInvaders game;
    private OrthographicCamera camera;
    private GlyphLayout glyphLayout;
    private Properties prop;
    private Texture selectedImage;
    private Rectangle labelPos;

    private int distance;

    private String selectedTexture;
    private String imagePath;
    private int labelMarginTop;
    private int menuElementStep;
    private int menuMarginBottom;
    private int menuMarginRight;
    private int menuHeight;
    private int menuWidth;
    private int menuChangeLimit;

    private int pos = 0;
    private long lastMenuChange;

    private String distan;
    private int disX, disY;

    /**
     * Init game end screen
     *
     * @since 1.1
     * @param game - game class
     * @param dis - distance
     * @throws IOException if can't access properties files
     */
    public GameEndScreen(StarInvaders game, int dis) throws IOException {
        game.log.Log("Initializing game end screen", LogSystem.INFO);

        this.distance = dis;

        prop = new Properties();
        prop.load(new FileInputStream("properties/strings." + game.settingsMain.get("lang", "us") + ".properties"));

        label = prop.getProperty("die.label");
        menuLables = prop.getProperty("die.elements").split(";");
        distan = prop.getProperty("die.result") + " " + distance + " m";

        prop.load(new FileInputStream("properties/die.properties"));
        menuLabelXAdd = Double.parseDouble(prop.getProperty("menu.elements.position.x"));
        selectedTexture = prop.getProperty("menu.selected.texture");
        labelMarginTop = (int) (game.HEIGHT * Double.parseDouble(prop.getProperty("label.margin.top")));
        menuElementStep = (int) (game.HEIGHT * Double.parseDouble(prop.getProperty("menu.elements.step")));
        menuMarginBottom = (int) (game.HEIGHT * Double.parseDouble(prop.getProperty("menu.selected.margin.bottom")));
        menuMarginRight = (int) (game.WIDTH * Double.parseDouble(prop.getProperty("menu.selected.margin.right")));
        menuHeight = (int) (game.HEIGHT * Double.parseDouble(prop.getProperty("menu.selected.height")));
        menuWidth = (int) (game.WIDTH * Double.parseDouble(prop.getProperty("menu.selected.width")));
        menuChangeLimit = Integer.parseInt(prop.getProperty("menu.change.limit"));

        prop.load(new FileInputStream("properties/main.properties"));
        imagePath = prop.getProperty("dir.images");

        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, game.WIDTH, game.HEIGHT);
        glyphLayout = new GlyphLayout(game.fontMain, label);
        labelPos = new Rectangle();

        selectedImage = new Texture(imagePath + selectedTexture);

        labelPos.x = (int) ((game.WIDTH) / 2 - glyphLayout.width / 2);
        labelPos.y = game.HEIGHT - labelMarginTop;

        menuLabelsX = (int) (game.WIDTH / 2 - glyphLayout.width / 2 + glyphLayout.width * menuLabelXAdd);

        glyphLayout = new GlyphLayout(game.fontLabel, distan);

        disX = (int) ((game.WIDTH - glyphLayout.width) / 2);
        disY = (int) (labelPos.y - 100 - glyphLayout.height);
    }

    /**
     * Render game end screen
     *
     * @since 1.1
     * @param delta - delta time
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.fontMain.draw(game.batch, label, labelPos.x, labelPos.y);

        game.fontLabel.draw(game.batch, distan, disX, disY);

        for (int i = 0; i < menuLables.length; i++)
            game.fontLabel.draw(game.batch, menuLables[i], menuLabelsX, labelPos.y - 100 - (i + 1) * menuElementStep);

        game.batch.draw(selectedImage, menuLabelsX - menuMarginRight, labelPos.y - 100 - (pos + 1) * menuElementStep - menuMarginBottom, menuWidth, menuHeight);
        game.batch.end();

        if (TimeUtils.nanoTime() - lastMenuChange > menuChangeLimit) {
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                pos++;
                lastMenuChange = TimeUtils.nanoTime();
            }
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                pos--;
                lastMenuChange = TimeUtils.nanoTime();
            }
        }

        if (pos < 0)
            pos = 0;
        if (pos > menuLables.length - 1)
            pos = menuLables.length - 1;

        if (Gdx.input.isKeyPressed(Input.Keys.ENTER))
            switch (pos) {
                case 0:
                    try {
                        game.setScreen(new MainGameScreen(game));
                    } catch (IOException e) {
                        e.printStackTrace();
                        game.log.Log("Error: " + e.getMessage(), LogSystem.ERROR);
                        Gdx.app.exit();
                    }
                    break;
                case 1:
                    try {
                        game.setScreen(new MainMenuScreen(game));
                    } catch (IOException e) {
                        e.printStackTrace();
                        game.log.Log("Error: " + e.getMessage(), LogSystem.ERROR);
                        Gdx.app.exit();
                    }
                    break;
                case 2:
                    Gdx.app.exit();
                    break;
            }
    }

    /**
     * Show screen
     *
     * @since 1.1
     */
    @Override
    public void show() {

    }

    /**
     * Resize screen
     *
     * @since 1.1
     * @param width - new screen width
     * @param height - new screen height
     */
    @Override
    public void resize(int width, int height) {

    }

    /**
     * Pause screen
     *
     * @since 1.1
     */
    @Override
    public void pause() {

    }

    /**
     * Resume screen
     *
     * @since 1.1
     */
    @Override
    public void resume() {

    }

    /**
     * Hide screen
     *
     * @since 1.1
     */
    @Override
    public void hide() {

    }

    /**
     * Clean
     *
     * @since 1.1
     */
    @Override
    public void dispose() {
        game.log.Log("Disposing game end screen", LogSystem.INFO);
        game = null;
        camera = null;
        glyphLayout = null;
        prop = null;
        selectedImage.dispose();
        selectedImage = null;
        labelPos = null;
    }

}
