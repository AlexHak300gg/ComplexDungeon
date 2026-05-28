package io.github.some_example_name.weapon;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.entity.PlayerSlime;
import io.github.some_example_name.level.TileMap;

public class PlasmaLaser extends Weapon {
    private static final float BEAM_LENGTH = 300f;
    private static final float BEAM_WIDTH = 6f;

    private Texture beamTex;
    private TileMap tileMap;

    public PlasmaLaser(PlayerSlime owner, EntityManager em, TileMap tileMap) {
        super("\u041F\u043B\u0430\u0437\u043C\u0435\u043D\u043D\u044B\u0439 \u043B\u0430\u0437\u0435\u0440", 0.1f, 5f, owner, em);
        this.tileMap = tileMap;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(1f, 0.2f, 0.5f, 1f));
        pixmap.fill();
        beamTex = new Texture(pixmap);
        pixmap.dispose();
    }

    @Override
    protected void fire(Array<Entity> enemies) {
        float px = owner.getPosition().x + owner.getSize().x / 2f;
        float py = owner.getPosition().y + owner.getSize().y / 2f;
        int dir = owner.getFacingDirection();

        float beamEnd = px + dir * BEAM_LENGTH;
        for (float rx = px; Math.abs(rx - px) < BEAM_LENGTH; rx += dir * TileMap.TILE_SIZE) {
            int tx = (int)(rx / TileMap.TILE_SIZE);
            int ty = (int)(py / TileMap.TILE_SIZE);
            if (tileMap.isSolid(tx, ty)) {
                beamEnd = rx - dir * TileMap.TILE_SIZE;
                break;
            }
        }

        float beamStart = Math.min(px, beamEnd);
        float beamStop = Math.max(px, beamEnd);

        if (enemies != null) {
            float totalDamage = this.damage + owner.getElementalDamage();
            for (Entity e : enemies) {
                float ex = e.getPosition().x + e.getSize().x / 2f;
                float ey = e.getPosition().y + e.getSize().y / 2f;
                if (ey >= py - BEAM_WIDTH * 4 && ey <= py + BEAM_WIDTH * 4
                    && ex >= beamStart && ex <= beamStop) {
                    e.kill();
                }
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        float px = owner.getPosition().x + owner.getSize().x / 2f;
        float py = owner.getPosition().y + owner.getSize().y / 2f - BEAM_WIDTH / 2f;
        int dir = owner.getFacingDirection();

        float endX = px + dir * BEAM_LENGTH;
        for (float rx = px; Math.abs(rx - px) < BEAM_LENGTH; rx += dir * TileMap.TILE_SIZE) {
            int tx = (int)(rx / TileMap.TILE_SIZE);
            int ty = (int)(py / TileMap.TILE_SIZE);
            if (tileMap.isSolid(tx, ty)) {
                endX = rx - dir * TileMap.TILE_SIZE;
                break;
            }
        }

        float drawX = Math.min(px, endX);
        float drawW = Math.abs(endX - px);
        batch.draw(beamTex, drawX, py, drawW, BEAM_WIDTH);
    }

    @Override
    public void dispose() {
        beamTex.dispose();
    }
}
