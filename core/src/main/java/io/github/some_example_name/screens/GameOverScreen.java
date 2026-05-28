package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.ComplexDungeonGame;

public class GameOverScreen extends BaseScreen {
    private Stage stage;
    private Skin skin;
    private int score;
    private float time;
    private float biomass;

    public GameOverScreen(ComplexDungeonGame game, int score, float time, float biomass) {
        super(game);
        this.score = score;
        this.time = time;
        this.biomass = biomass;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport(), batch);
        Gdx.input.setInputProcessor(stage);

        BitmapFont font = game.getAssetProvider().getFont("fonts/Roboto-Medium.ttf");
        skin = new Skin();
        skin.add("default", font);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        skin.add("default", labelStyle, Label.LabelStyle.class);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = font;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.overFontColor = Color.YELLOW;
        btnStyle.downFontColor = Color.GRAY;
        skin.add("default", btnStyle, TextButton.TextButtonStyle.class);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label title = new Label("\u0418\u0433\u0440\u0430 \u041E\u043A\u043E\u043D\u0447\u0435\u043D\u0430", skin);
        title.setFontScale(2f);

        Label scoreLabel = new Label("\u0421\u0447\u0451\u0442: " + score, skin);
        Label timeLabel = new Label("\u0412\u0440\u0435\u043C\u044F: " + String.format("%.1f", time) + "\u0441", skin);
        Label bioLabel = new Label("\u041F\u043E\u0433\u043B\u043E\u0449\u0435\u043D\u043E \u0431\u0438\u043E\u043C\u0430\u0441\u0441\u044B: " + String.format("%.1f", biomass), skin);

        TextButton retryBtn = new TextButton("\u041F\u043E\u0432\u0442\u043E\u0440\u0438\u0442\u044C", skin);
        TextButton menuBtn = new TextButton("\u041C\u0435\u043D\u044E", skin);

        table.add(title).padBottom(40f).row();
        table.add(scoreLabel).padBottom(10f).row();
        table.add(timeLabel).padBottom(10f).row();
        table.add(bioLabel).padBottom(40f).row();
        table.add(retryBtn).width(250f).height(60f).padBottom(15f).row();
        table.add(menuBtn).width(250f).height(60f);

        retryBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                game.getScreenManager().switchTo(new GameScreen(game));
            }
        });

        menuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                game.getScreenManager().switchTo(new MainMenuScreen(game));
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
