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

public class MainMenuScreen extends BaseScreen {
    private Stage stage;
    private Skin skin;

    public MainMenuScreen(ComplexDungeonGame game) {
        super(game);
    }

    private Skin createSkin() {
        Skin s = new Skin();
        BitmapFont font = game.getAssetProvider().getFont("fonts/Roboto-Medium.ttf");
        s.add("default", font);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        s.add("default", labelStyle, Label.LabelStyle.class);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = font;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.overFontColor = Color.YELLOW;
        btnStyle.downFontColor = Color.GRAY;
        s.add("default", btnStyle, TextButton.TextButtonStyle.class);

        return s;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport(), batch);
        Gdx.input.setInputProcessor(stage);

        skin = createSkin();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label titleLabel = new Label("\u0421\u043b\u043e\u0436\u043d\u044b\u0439 \u041f\u043e\u0434\u0437\u0435\u043c\u0435\u043b\u044c\u0435", skin);
        titleLabel.setFontScale(2f);

        TextButton playButton = new TextButton("\u0418\u0433\u0440\u0430\u0442\u044c", skin);
        TextButton exitButton = new TextButton("\u0412\u044b\u0445\u043e\u0434", skin);

        table.add(titleLabel).padBottom(80f).row();
        table.add(playButton).padBottom(30f).width(250f).height(60f).row();
        table.add(exitButton).width(250f).height(60f);

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getScreenManager().switchTo(new GameScreen(game));
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getScreenManager().exit();
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f);
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
