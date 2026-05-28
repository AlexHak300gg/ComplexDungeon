package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.ComplexDungeonGame;
import io.github.some_example_name.entity.PlayerSlime;

public class UpgradeOverlay {
    public interface UpgradeAction {
        void apply(PlayerSlime player);
        String getDescription();
    }

    private Stage stage;
    private Skin skin;
    private boolean visible;
    private Array<UpgradeAction> currentOptions;

    public UpgradeOverlay(BitmapFont font) {
        currentOptions = new Array<>();
        visible = false;

        skin = new Skin();
        skin.add("default", font);

        Label.LabelStyle ls = new Label.LabelStyle(font, Color.WHITE);
        skin.add("default", ls, Label.LabelStyle.class);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = font;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.overFontColor = Color.YELLOW;
        btnStyle.downFontColor = Color.GRAY;
        skin.add("default", btnStyle, TextButton.TextButtonStyle.class);
    }

    public void show(PlayerSlime player) {
        currentOptions.clear();
        currentOptions.addAll(generateOptions(player));
        visible = true;
        rebuildUI(player);
    }

    public void hide() {
        visible = false;
    }

    public boolean isVisible() { return visible; }

    private Array<UpgradeAction> generateOptions(PlayerSlime player) {
        Array<UpgradeAction> options = new Array<>();
        options.add(new UpgradeAction() {
            public String getDescription() { return "+20% \u0421\u043A\u043E\u0440\u043E\u0441\u0442\u044C"; }
            public void apply(PlayerSlime p) { p.upgradeSpeed(0.2f); }
        });
        options.add(new UpgradeAction() {
            public String getDescription() { return "+10% \u041C\u0430\u0433\u043D\u0435\u0442\u0438\u0437\u043C"; }
            public void apply(PlayerSlime p) { p.upgradeMagnetRange(0.1f); }
        });
        options.add(new UpgradeAction() {
            public String getDescription() { return "+15% \u0423\u0440\u043E\u043D \u0441\u0442\u0438\u0445\u0438\u0439"; }
            public void apply(PlayerSlime p) { p.upgradeElementalDamage(1.5f); }
        });
        options.add(new UpgradeAction() {
            public String getDescription() { return "+25 \u041C\u0430\u043A\u0441. \u0437\u0434\u043E\u0440\u043E\u0432\u044C\u0435"; }
            public void apply(PlayerSlime p) { p.upgradeMaxHealth(25f); }
        });
        options.add(new UpgradeAction() {
            public String getDescription() { return "\u0423\u0441\u0438\u043B\u0435\u043D\u0438\u0435 \u043F\u0440\u044B\u0436\u043A\u0430"; }
            public void apply(PlayerSlime p) { p.upgradeJump(50f); }
        });
        options.add(new UpgradeAction() {
            public String getDescription() { return "+10% \u0420\u0430\u0434\u0438\u0443\u0441 \u043C\u0430\u0433\u043D\u0438\u0442\u0430"; }
            public void apply(PlayerSlime p) { p.upgradeMagnetRange(0.1f); }
        });
        options.shuffle();
        while (options.size > 3) options.pop();
        return options;
    }

    private void rebuildUI(PlayerSlime player) {
        if (stage != null) stage.dispose();
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Table bg = new Table();
        bg.setFillParent(true);
        bg.setColor(0, 0, 0, 0.7f);
        stage.addActor(bg);

        Table panel = new Table();
        panel.setFillParent(true);
        stage.addActor(panel);

        Label title = new Label("\u0412\u044B\u0431\u0435\u0440\u0438\u0442\u0435 \u0443\u043B\u0443\u0447\u0448\u0435\u043D\u0438\u0435", skin);
        title.setFontScale(1.5f);
        panel.add(title).padBottom(30f).row();

        for (final UpgradeAction option : currentOptions) {
            TextButton btn = new TextButton(option.getDescription(), skin);
            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent e, float x, float y) {
                    option.apply(player);
                    hide();
                }
            });
            panel.add(btn).width(300f).height(50f).padBottom(15f).row();
        }
    }

    public void render(SpriteBatch batch, OrthographicCamera uiCamera) {
        if (!visible) return;
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void resize(int width, int height) {
        if (stage != null) stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        if (stage != null) stage.dispose();
        skin.dispose();
    }
}
