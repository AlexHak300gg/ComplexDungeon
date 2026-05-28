package io.github.some_example_name.item;

import com.badlogic.gdx.graphics.Color;
import io.github.some_example_name.entity.PlayerSlime;

public class Magnet extends PickupItem {
    public Magnet(float x, float y) {
        super(x, y, new Color(0.9f, 0.2f, 0.2f, 1f), "\u041C\u0430\u0433\u043D\u0438\u0442");
    }

    @Override
    public void apply(PlayerSlime player) {
        player.activateMagnet(8f);
    }
}
