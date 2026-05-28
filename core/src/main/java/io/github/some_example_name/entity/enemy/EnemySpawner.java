package io.github.some_example_name.entity.enemy;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.level.TileMap;
import io.github.some_example_name.level.TileType;

public class EnemySpawner {
    private static final float SPAWN_INTERVAL = 4f;
    private static final int MAX_ENEMIES = 30;
    private static final float SPAWN_MARGIN = 200f;

    private float timer;
    private int wave;
    private boolean bossSpawned;

    public EnemySpawner() {
        timer = 2f;
        wave = 0;
        bossSpawned = false;
    }

    public void update(float delta, OrthographicCamera camera, TileMap tileMap,
                       Array<BaseEnemy> enemies, Entity player) {
        timer -= delta;
        if (timer > 0) return;
        if (enemies.size >= MAX_ENEMIES) return;

        wave++;
        int count = Math.min(3 + wave / 2, 8);
        timer = Math.max(SPAWN_INTERVAL - wave * 0.1f, 1.5f);

        for (int i = 0; i < count; i++) {
            BaseEnemy enemy = createEnemy(camera, tileMap, wave);
            if (enemy == null) continue;

            enemy.setPlayer(player);
            enemies.add(enemy);
        }

        if (!bossSpawned && wave >= 5) {
            BaseEnemy boss = createBoss(camera, tileMap);
            if (boss != null) {
                boss.setPlayer(player);
                enemies.add(boss);
                bossSpawned = true;
            }
        }
    }

    private BaseEnemy createEnemy(OrthographicCamera camera, TileMap tileMap, int wave) {
        float cx = camera.position.x;
        float cy = camera.position.y;
        float hw = camera.viewportWidth / 2f;
        float hh = camera.viewportHeight / 2f;

        for (int attempt = 0; attempt < 30; attempt++) {
            float x, y;
            int side = MathUtils.random(3);
            switch (side) {
                case 0: x = cx - hw - SPAWN_MARGIN; y = cy + MathUtils.random(-hh, hh); break;
                case 1: x = cx + hw + SPAWN_MARGIN; y = cy + MathUtils.random(-hh, hh); break;
                case 2: x = cx + MathUtils.random(-hw, hw); y = cy + hh + SPAWN_MARGIN; break;
                default: x = cx + MathUtils.random(-hw, hw); y = cy - hh - SPAWN_MARGIN; break;
            }

            if (x < 0 || x >= tileMap.getWorldWidth() || y < 0 || y >= tileMap.getWorldHeight()) continue;

            int tx = (int)(x / TileMap.TILE_SIZE);
            int ty = (int)(y / TileMap.TILE_SIZE);
            if (tileMap.isSolid(tx, ty)) continue;

            float r = MathUtils.random();
            if (r < 0.6f) return new SwarmBug(x, y, tileMap);
            else return new RangedSpitter(x, y, tileMap);
        }
        return null;
    }

    private BaseEnemy createBoss(OrthographicCamera camera, TileMap tileMap) {
        float x = camera.position.x + camera.viewportWidth / 2f + 100f;
        float y = 0;

        int tx = (int)(x / TileMap.TILE_SIZE);
        for (int ty = 0; ty < tileMap.getHeight() - 1; ty++) {
            if (!tileMap.isSolid(tx, ty) && tileMap.isSolid(tx, ty + 1)) {
                y = (ty + 1) * TileMap.TILE_SIZE;
                break;
            }
        }
        return new EliteBoss(x, y, tileMap);
    }

    public void reset() {
        timer = 2f;
        wave = 0;
        bossSpawned = false;
    }
}
