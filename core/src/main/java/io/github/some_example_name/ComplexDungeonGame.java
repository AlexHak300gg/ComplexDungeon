package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import io.github.some_example_name.assets.AssetProvider;
import io.github.some_example_name.screens.MainMenuScreen;
import io.github.some_example_name.screens.ScreenManager;

public class ComplexDungeonGame extends Game {
    private SpriteBatch batch;
    private AssetProvider assetProvider;
    private ScreenManager screenManager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        assetProvider = new AssetProvider();
        screenManager = new ScreenManager(this);

        assetProvider.queueTTFFont("fonts/Roboto-Medium.ttf", createFontParam());
        assetProvider.finishLoading();

        screenManager.switchTo(new MainMenuScreen(this));
    }

    private FreetypeFontLoader.FreeTypeFontLoaderParameter createFontParam() {
        FreetypeFontLoader.FreeTypeFontLoaderParameter param =
            new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        param.fontFileName = "fonts/Roboto-Medium.ttf";
        param.fontParameters.size = 32;
        param.fontParameters.characters =
            "абвгдеёжзийклмнопрстуфхцчшщъыьэюя" +
            "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ" +
            "abcdefghijklmnopqrstuvwxyz" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "0123456789.,!?-+*/=()[]{}<>:;\"'%$#@& ";
        return param;
    }

    @Override
    public void dispose() {
        batch.dispose();
        assetProvider.dispose();
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public AssetProvider getAssetProvider() {
        return assetProvider;
    }

    public ScreenManager getScreenManager() {
        return screenManager;
    }
}
