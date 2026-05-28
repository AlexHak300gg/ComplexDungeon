package io.github.some_example_name.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.some_example_name.ComplexDungeonGame;

public abstract class BaseScreen implements Screen {
    protected final ComplexDungeonGame game;
    protected final SpriteBatch batch;

    public BaseScreen(ComplexDungeonGame game) {
        this.game = game;
        this.batch = game.getBatch();
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {}

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {}
}
