package io.github.some_example_name.item;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.level.TileMap;
import io.github.some_example_name.level.TileType;

public class PickupSpawner {
    public Array<PickupItem> generate(TileMap tileMap, int count) {
        Array<PickupItem> items = new Array<>();
        for (int i = 0; i < count; i++) {
            PickupItem item = createRandom(tileMap);
            if (item != null) items.add(item);
        }
        return items;
    }

    private PickupItem createRandom(TileMap tileMap) {
        for (int attempt = 0; attempt < 50; attempt++) {
            int tx = MathUtils.random(5, tileMap.getWidth() - 5);
            int ty = MathUtils.random(6, tileMap.getHeight() - 3);

            if (tileMap.getTile(tx, ty) != TileType.EMPTY) continue;
            if (!tileMap.isSolid(tx, ty + 1) && !tileMap.isSolid(tx, ty - 1)) continue;

            float wx = tx * TileMap.TILE_SIZE;
            float wy = ty * TileMap.TILE_SIZE;

            float r = MathUtils.random();
            if (r < 0.5f) return new BioMass(wx, wy);
            else if (r < 0.75f) return new Magnet(wx, wy);
            else if (r < 0.9f) return new SpeedBuff(wx, wy);
            else return new Mutation(wx, wy);
        }
        return null;
    }
}
