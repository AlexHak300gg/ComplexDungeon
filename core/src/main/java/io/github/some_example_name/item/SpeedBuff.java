package io.github.some_example_name.item;

import com.badlogic.gdx.graphics.Color;
import io.github.some_example_name.entity.PlayerSlime;

public class SpeedBuff extends PickupItem {
    public SpeedBuff(float x, float y) {
        super(x, y, new Color(0.2f, 0.6f, 1f, 1f), "\u0423\u0441\u043A\u043E\u0440\u0435\u043D\u0438\u0435");
    }

    @Override
    public void apply(PlayerSlime player) {
        player.activateSpeedBuff(5f);
    }
}
