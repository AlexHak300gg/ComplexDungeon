package io.github.some_example_name.level;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class LevelRenderer {
    private final TileMap map;
    private final int tileSize;

    private Texture dirtTex;
    private Texture grassTex;
    private Texture platformTex;
    private Texture exitTex;

    public LevelRenderer(TileMap map) {
        this.map = map;
        this.tileSize = TileMap.TILE_SIZE;
        createTextures();
    }

    private void createTextures() {
        dirtTex = createColoredTile(new Color(0.45f, 0.28f, 0.12f, 1f));
        grassTex = createColoredTile(new Color(0.2f, 0.6f, 0.15f, 1f));
        platformTex = createColoredTile(new Color(0.4f, 0.3f, 0.2f, 1f));
        exitTex = createColoredTile(new Color(0.9f, 0.2f, 0.8f, 1f));
    }

    private Texture createColoredTile(Color color) {
        Pixmap pixmap = new Pixmap(tileSize, tileSize, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        pixmap.setColor(color.mul(0.7f));
        pixmap.drawRectangle(0, 0, tileSize, tileSize);
        Texture tex = new Texture(pixmap);
        pixmap.dispose();
        return tex;
    }

    public void render(SpriteBatch batch, OrthographicCamera camera) {
        int left = (int)((camera.position.x - camera.viewportWidth / 2f) / tileSize) - 1;
        int right = (int)((camera.position.x + camera.viewportWidth / 2f) / tileSize) + 2;
        int bottom = (int)((camera.position.y - camera.viewportHeight / 2f) / tileSize) - 1;
        int top = (int)((camera.position.y + camera.viewportHeight / 2f) / tileSize) + 2;

        left = Math.max(0, left);
        right = Math.min(map.getWidth(), right);
        bottom = Math.max(0, bottom);
        top = Math.min(map.getHeight(), top);

        for (int x = left; x < right; x++) {
            for (int y = bottom; y < top; y++) {
                TileType tile = map.getTile(x, y);
                if (tile == TileType.EMPTY) continue;

                Texture tex = getTexture(tile);
                batch.draw(tex, x * tileSize, y * tileSize);
            }
        }
    }

    private Texture getTexture(TileType type) {
        switch (type) {
            case GRASS: return grassTex;
            case DIRT: return dirtTex;
            case PLATFORM: return platformTex;
            case EXIT: return exitTex;
            default: return dirtTex;
        }
    }

    public void dispose() {
        dirtTex.dispose();
        grassTex.dispose();
        platformTex.dispose();
        exitTex.dispose();
    }
}
