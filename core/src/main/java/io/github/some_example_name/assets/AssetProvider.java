package io.github.some_example_name.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;

public class AssetProvider {
    private final AssetManager manager;

    public AssetProvider() {
        manager = new AssetManager();
        FileHandleResolver resolver = new InternalFileHandleResolver();
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
    }

    public void queueTexture(String path) {
        manager.load(path, Texture.class);
    }

    public void queueSound(String path) {
        manager.load(path, Sound.class);
    }

    public void queueTTFFont(String path, FreetypeFontLoader.FreeTypeFontLoaderParameter param) {
        manager.load(path, BitmapFont.class, param);
    }

    public boolean update() {
        return manager.update();
    }

    public float getProgress() {
        return manager.getProgress();
    }

    public void finishLoading() {
        manager.finishLoading();
    }

    public Texture getTexture(String path) {
        return manager.get(path, Texture.class);
    }

    public Sound getSound(String path) {
        return manager.get(path, Sound.class);
    }

    public BitmapFont getFont(String path) {
        return manager.get(path, BitmapFont.class);
    }

    public boolean isLoaded(String path) {
        return manager.isLoaded(path);
    }

    public void dispose() {
        manager.dispose();
    }
}
