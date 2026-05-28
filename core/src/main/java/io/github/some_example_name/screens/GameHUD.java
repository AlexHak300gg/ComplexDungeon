package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.entity.PlayerSlime;

public class GameHUD {
    private Stage stage;
    private Skin skin;
    private BitmapFont font;

    private Label healthLabel;
    private Label sizeLabel;
    private Label killsLabel;
    private Label buffsLabel;

    private Texture healthBarBg;
    private Texture healthBarFill;
    private Texture buffIconSpeed;
    private Texture buffIconMagnet;

    public GameHUD(BitmapFont font) {
        this.font = font;
        createSkin();
        createHUD();
        createTextures();
    }

    private void createSkin() {
        skin = new Skin();
        skin.add("default", font);

        Label.LabelStyle ls = new Label.LabelStyle(font, Color.WHITE);
        skin.add("default", ls, Label.LabelStyle.class);
    }

    private void createHUD() {
        stage = new Stage(new ScreenViewport());

        Table table = new Table();
        table.setFillParent(true);
        table.top().left();
        stage.addActor(table);

        healthLabel = new Label("\u0417\u0434\u043E\u0440\u043E\u0432\u044C\u0435: 100/100", skin);
        sizeLabel = new Label("\u0420\u0430\u0437\u043C\u0435\u0440: x1.0", skin);
        killsLabel = new Label("\u041F\u043E\u0431\u0435\u0436\u0434\u0435\u043D\u043E: 0", skin);
        buffsLabel = new Label("", skin);

        table.add(healthLabel).padTop(10f).padLeft(10f).row();
        table.add(sizeLabel).padTop(5f).padLeft(10f).row();
        table.add(killsLabel).padTop(5f).padLeft(10f).row();
        table.add(buffsLabel).padTop(5f).padLeft(10f);
    }

    private void createTextures() {
        Pixmap bg = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bg.setColor(Color.DARK_GRAY);
        bg.fill();
        healthBarBg = new Texture(bg);
        bg.dispose();

        Pixmap fill = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        fill.setColor(Color.RED);
        fill.fill();
        healthBarFill = new Texture(fill);
        fill.dispose();

        Pixmap speed = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        speed.setColor(Color.BLUE);
        speed.fillCircle(8, 8, 7);
        buffIconSpeed = new Texture(speed);
        speed.dispose();

        Pixmap magnet = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        magnet.setColor(Color.RED);
        magnet.fillCircle(8, 8, 7);
        buffIconMagnet = new Texture(magnet);
        magnet.dispose();
    }

    public void update(PlayerSlime player, GameState state) {
        healthLabel.setText("\u0417\u0434\u043E\u0440\u043E\u0432\u044C\u0435: " + (int)player.getHealth() + "/" + (int)player.getMaxHealth());
        sizeLabel.setText("\u0420\u0430\u0437\u043C\u0435\u0440: x" + String.format("%.1f", player.getCurrentScale()));
        killsLabel.setText("\u041F\u043E\u0431\u0435\u0436\u0434\u0435\u043D\u043E: " + state.kills);

        String buffs = "";
        if (player.isSpeedBuffActive()) buffs += "\u0423\u0441\u043A\u043E\u0440\u0435\u043D\u0438\u0435 ";
        if (player.isMagnetActive()) buffs += "\u041C\u0430\u0433\u043D\u0438\u0442 ";
        buffsLabel.setText(buffs);
    }

    public Stage getStage() { return stage; }

    public void render(SpriteBatch batch, OrthographicCamera uiCamera) {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
        healthBarBg.dispose();
        healthBarFill.dispose();
        buffIconSpeed.dispose();
        buffIconMagnet.dispose();
    }
}
