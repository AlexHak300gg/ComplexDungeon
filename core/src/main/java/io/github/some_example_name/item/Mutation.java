package io.github.some_example_name.item;

import com.badlogic.gdx.graphics.Color;
import io.github.some_example_name.entity.PlayerSlime;

public class Mutation extends PickupItem {
    public Mutation(float x, float y) {
        super(x, y, new Color(0.8f, 0.2f, 0.9f, 1f), "\u041C\u0443\u0442\u0430\u0446\u0438\u044F");
    }

    @Override
    public void apply(PlayerSlime player) {
        player.cycleElement();
    }
}
