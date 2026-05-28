package io.github.some_example_name.weapon;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.entity.PlayerSlime;

public abstract class Weapon {
    protected String name;
    protected float fireRate;
    protected float damage;
    protected float cooldown;
    protected PlayerSlime owner;
    protected EntityManager entityManager;

    public Weapon(String name, float fireRate, float damage, PlayerSlime owner, EntityManager em) {
        this.name = name;
        this.fireRate = fireRate;
        this.damage = damage;
        this.owner = owner;
        this.entityManager = em;
        this.cooldown = 0;
    }

    public String getName() { return name; }

    public void update(float delta, Array<Entity> enemies) {
        cooldown -= delta;
        if (cooldown <= 0) {
            fire(enemies);
            cooldown = fireRate;
        }
    }

    protected abstract void fire(Array<Entity> enemies);

    public abstract void draw(SpriteBatch batch);

    public void dispose() {}
}
