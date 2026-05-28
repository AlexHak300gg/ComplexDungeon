package io.github.some_example_name.item;

import com.badlogic.gdx.graphics.Color;
import io.github.some_example_name.entity.PlayerSlime;

public class BioMass extends PickupItem {
    public BioMass(float x, float y) {
        super(x, y, new Color(0.2f, 0.9f, 0.3f, 1f), "\u0411\u0438\u043E\u043C\u0430\u0441\u0441\u0430");
    }

    @Override
    public void apply(PlayerSlime player) {
        player.grow(0.1f);
    }
}
