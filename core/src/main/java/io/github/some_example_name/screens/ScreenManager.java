package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import io.github.some_example_name.ComplexDungeonGame;

public class ScreenManager {
    private final ComplexDungeonGame game;

    public ScreenManager(ComplexDungeonGame game) {
        this.game = game;
    }

    public void switchTo(BaseScreen screen) {
        game.setScreen(screen);
    }

    public void switchTo(BaseScreen screen, BaseScreen previous) {
        previous.dispose();
        game.setScreen(screen);
    }

    public void exit() {
        Gdx.app.exit();
    }
}
