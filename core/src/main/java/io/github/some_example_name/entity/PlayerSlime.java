package io.github.some_example_name.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.item.PickupItem;
import io.github.some_example_name.level.TileMap;
import io.github.some_example_name.level.TileType;
import io.github.some_example_name.weapon.AutoTurret;
import io.github.some_example_name.weapon.PlasmaLaser;
import io.github.some_example_name.weapon.Weapon;

public class PlayerSlime extends Entity {
    private static final float GRAVITY = 700f;
    private static final float MOVE_SPEED_BASE = 300f;
    private static final float JUMP_VELOCITY_BASE = 370f;
    private static final float BASE_SIZE = 32f;
    private static final float MAGNET_RANGE = 200f;
    private static final float HITBOX_SHRINK = 6f;

    public enum Element { \u041D\u041E\u0420\u041C\u0410, \u041E\u0413\u041E\u041D\u042C, \u041B\u0415\u0414, \u042D\u041B\u0415\u041A\u0422\u0420\u0418\u0427\u0415\u0421\u0422\u0412\u041E }

    private float currentScale;
    private float moveSpeed;
    private float jumpVelocity;
    private float gravity;
    private boolean grounded;
    private int moveDirection;
    private int facingDirection;

    private Sprite sprite;
    private Texture texture;
    private Color baseColor;

    private Stage controlStage;
    private Skin skin;
    private boolean jumpPressed;

    private TileMap tileMap;
    private boolean wasJumpKeyDown;
    private int jumpsLeft = 2;

    private Array<Weapon> weapons;
    private Element element;
    private float elementalDamage;

    private float speedBuffTimer;
    private float speedBuffMultiplier;
    private float originalMoveSpeed;

    private float magnetTimer;

    private float health;
    private float maxHealth;
    private float baseMagnetRange;
    private float speedUpgradeMultiplier;

    public PlayerSlime(TileMap tileMap) {
        super(tileMap.getSpawnX(), tileMap.getSpawnY(), BASE_SIZE, BASE_SIZE);
        this.tileMap = tileMap;
        currentScale = 1f;
        moveSpeed = MOVE_SPEED_BASE;
        jumpVelocity = JUMP_VELOCITY_BASE;
        gravity = GRAVITY;
        grounded = false;
        moveDirection = 0;
        facingDirection = 1;

        baseColor = new Color(0.2f, 0.8f, 0.4f, 1f);
        element = Element.\u041D\u041E\u0420\u041C\u0410;
        elementalDamage = 0;

        speedBuffTimer = 0;
        speedBuffMultiplier = 1f;
        originalMoveSpeed = MOVE_SPEED_BASE;

        magnetTimer = 0;
        maxHealth = 100f;
        health = maxHealth;
        baseMagnetRange = MAGNET_RANGE;
        speedUpgradeMultiplier = 1f;

        createTexture();
        createControls();
        weapons = new Array<>();
    }

    public void initWeapons(EntityManager em) {
        weapons.add(new AutoTurret(this, em, tileMap));
        weapons.add(new PlasmaLaser(this, em, tileMap));
    }

    private void createTexture() {
        Pixmap pixmap = new Pixmap((int)BASE_SIZE, (int)BASE_SIZE, Pixmap.Format.RGBA8888);
        pixmap.setColor(baseColor);
        pixmap.fillCircle((int)BASE_SIZE / 2, (int)BASE_SIZE / 2, (int)BASE_SIZE / 2 - 2);
        texture = new Texture(pixmap);
        pixmap.dispose();
        sprite = new Sprite(texture);
    }

    public void updateSlimeColor() {
        Color tint;
        switch (element) {
            case \u041E\u0413\u041E\u041D\u042C: tint = new Color(1f, 0.3f, 0.1f, 1f); break;
            case \u041B\u0415\u0414: tint = new Color(0.3f, 0.6f, 1f, 1f); break;
            case \u042D\u041B\u0415\u041A\u0422\u0420\u0418\u0427\u0415\u0421\u0422\u0412\u041E: tint = new Color(1f, 0.8f, 0.1f, 1f); break;
            default: tint = new Color(0.2f, 0.8f, 0.4f, 1f); break;
        }
        baseColor.set(tint);
        Pixmap pixmap = new Pixmap((int)BASE_SIZE, (int)BASE_SIZE, Pixmap.Format.RGBA8888);
        pixmap.setColor(tint);
        pixmap.fillCircle((int)BASE_SIZE / 2, (int)BASE_SIZE / 2, (int)BASE_SIZE / 2 - 2);
        texture.dispose();
        texture = new Texture(pixmap);
        pixmap.dispose();
        sprite.setTexture(texture);
    }

    private void createControls() {
        controlStage = new Stage(new ScreenViewport());

        float baseSize = Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) * 0.13f;
        float jumpSize = baseSize * 1.5f;
        float pad = baseSize * 0.08f;

        skin = new Skin();

        Pixmap bg = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bg.setColor(new Color(0.2f, 0.2f, 0.2f, 0.6f));
        bg.fill();
        Texture bgTex = new Texture(bg);
        bg.dispose();

        Pixmap bgDown = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bgDown.setColor(new Color(0.4f, 0.4f, 0.4f, 0.8f));
        bgDown.fill();
        Texture bgDownTex = new Texture(bgDown);
        bgDown.dispose();

        com.badlogic.gdx.graphics.g2d.BitmapFont font = new com.badlogic.gdx.graphics.g2d.BitmapFont();
        font.getData().setScale(Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) / 500f);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = font;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.downFontColor = Color.YELLOW;
        btnStyle.up = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(bgTex));
        btnStyle.down = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(bgDownTex));
        skin.add("default", btnStyle, TextButton.TextButtonStyle.class);

        Table root = new Table();
        root.setFillParent(true);
        controlStage.addActor(root);

        Table leftControls = new Table();
        TextButton leftBtn = new TextButton("<", skin);
        TextButton rightBtn = new TextButton(">", skin);
        leftControls.add(leftBtn).size(baseSize, baseSize).pad(pad);
        leftControls.add(rightBtn).size(baseSize, baseSize).pad(pad);

        leftBtn.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent e, float x, float y, int pointer, int button) {
                moveDirection = -1;
                facingDirection = -1;
                return true;
            }
            @Override
            public void touchUp(InputEvent e, float x, float y, int pointer, int button) {
                if (moveDirection == -1) moveDirection = 0;
            }
        });

        rightBtn.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent e, float x, float y, int pointer, int button) {
                moveDirection = 1;
                facingDirection = 1;
                return true;
            }
            @Override
            public void touchUp(InputEvent e, float x, float y, int pointer, int button) {
                if (moveDirection == 1) moveDirection = 0;
            }
        });

        root.add(leftControls).expand().left().bottom().padBottom(baseSize * 0.4f).padLeft(pad);

        Table rightControls = new Table();
        TextButton jumpBtn = new TextButton("JUMP", skin);
        rightControls.add(jumpBtn).size(jumpSize, jumpSize).pad(pad);

        jumpBtn.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent e, float x, float y, int pointer, int button) {
                jumpPressed = true;
                return true;
            }
            @Override
            public void touchUp(InputEvent e, float x, float y, int pointer, int button) {
                jumpPressed = false;
            }
        });

        root.add(rightControls).expand().right().bottom().padBottom(baseSize * 0.4f).padRight(pad);
    }

    @Override
    public void update(float delta) {
        controlStage.act(delta);

        int kbDir = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) kbDir = -1;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) kbDir = 1;
        if (kbDir != 0) {
            moveDirection = kbDir;
            facingDirection = kbDir;
        }

        boolean jumpKeyDown = Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isKeyPressed(Input.Keys.W);
        if (grounded) jumpsLeft = 2;

        if (jumpKeyDown && !wasJumpKeyDown && jumpsLeft > 0) {
            velocity.y = jumpVelocity;
            grounded = false;
            jumpsLeft--;
        }
        wasJumpKeyDown = jumpKeyDown;

        if (moveDirection != 0) {
            velocity.x = moveDirection * moveSpeed * speedBuffMultiplier * speedUpgradeMultiplier;
        } else {
            velocity.x *= 0.85f;
        }

        if (jumpPressed && jumpsLeft > 0) {
            velocity.y = jumpVelocity;
            grounded = false;
            jumpPressed = false;
            jumpsLeft--;
        }
        if (!jumpPressed && !jumpKeyDown && velocity.y > 0) {
            velocity.y *= 0.5f;
        }

        velocity.y -= gravity * delta;

        position.x += velocity.x * delta;
        resolveCollisionX();

        position.y += velocity.y * delta;
        resolveCollisionY();

        if (position.x < 0) { position.x = 0; }
        if (position.y < -200) { alive = false; health = 0; }

        size.x = BASE_SIZE * currentScale;
        size.y = BASE_SIZE * currentScale;

        if (speedBuffTimer > 0) {
            speedBuffTimer -= delta;
            if (speedBuffTimer <= 0) {
                speedBuffMultiplier = 1f;
                moveSpeed = originalMoveSpeed;
            }
        }

        if (magnetTimer > 0) {
            magnetTimer -= delta;
        }
    }

    public void updateWeapons(float delta, Array<Entity> enemies) {
        for (Weapon w : weapons) {
            w.update(delta, enemies);
        }
    }

    public void drawWeapons(SpriteBatch batch) {
        for (Weapon w : weapons) {
            w.draw(batch);
        }
    }

    private void resolveCollisionX() {
        float shrink = HITBOX_SHRINK * currentScale;
        float hx = position.x + shrink;
        float hw = size.x - shrink * 2;

        int left = (int)(hx / TileMap.TILE_SIZE);
        int right = (int)((hx + hw - 1) / TileMap.TILE_SIZE);
        int bottom = (int)(position.y / TileMap.TILE_SIZE);
        int top = (int)((position.y + size.y - 1) / TileMap.TILE_SIZE);

        for (int tx = left; tx <= right; tx++) {
            for (int ty = bottom; ty <= top; ty++) {
                if (tileMap.isSolid(tx, ty)) {
                    if (tileMap.getTile(tx, ty) == TileType.PLATFORM) continue;
                    if (velocity.x > 0) {
                        position.x = tx * TileMap.TILE_SIZE - size.x + shrink;
                        velocity.x = 0;
                        hx = position.x + shrink;
                    } else if (velocity.x < 0) {
                        position.x = (tx + 1) * TileMap.TILE_SIZE - shrink;
                        velocity.x = 0;
                        hx = position.x + shrink;
                    }
                }
            }
        }
    }

    private void resolveCollisionY() {
        int left = (int)(position.x / TileMap.TILE_SIZE);
        int right = (int)((position.x + size.x - 1) / TileMap.TILE_SIZE);
        int bottom = (int)(position.y / TileMap.TILE_SIZE);
        int top = (int)((position.y + size.y - 1) / TileMap.TILE_SIZE);

        grounded = false;

        for (int tx = left; tx <= right; tx++) {
            for (int ty = bottom; ty <= top; ty++) {
                if (tileMap.isSolid(tx, ty)) {
                    boolean isPlatform = tileMap.getTile(tx, ty) == TileType.PLATFORM;
                    if (isPlatform && velocity.y > 0) continue;
                    if (velocity.y > 0) {
                        position.y = ty * TileMap.TILE_SIZE - size.y;
                        velocity.y = 0;
                    } else if (velocity.y < 0) {
                        position.y = (ty + 1) * TileMap.TILE_SIZE;
                        velocity.y = 0;
                        grounded = true;
                    }
                }
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        sprite.setPosition(position.x, position.y);
        sprite.setSize(size.x, size.y);
        sprite.draw(batch);
    }

    public void grow(float amount) {
        currentScale += amount;
        moveSpeed = MOVE_SPEED_BASE / currentScale;
        jumpVelocity = JUMP_VELOCITY_BASE / (1f + (currentScale - 1f) * 0.3f);
        gravity = GRAVITY * (1f + (currentScale - 1f) * 0.2f);
    }

    public void activateSpeedBuff(float duration) {
        speedBuffTimer = duration;
        speedBuffMultiplier = 2f;
        originalMoveSpeed = MOVE_SPEED_BASE / currentScale;
        moveSpeed = originalMoveSpeed * speedBuffMultiplier;
    }

    public void activateMagnet(float duration) {
        magnetTimer = duration;
    }

    public void cycleElement() {
        Element[] values = Element.values();
        int next = (element.ordinal() + 1) % values.length;
        element = values[next];
        switch (element) {
            case \u041E\u0413\u041E\u041D\u042C: elementalDamage = 5f; break;
            case \u041B\u0415\u0414: elementalDamage = 4f; break;
            case \u042D\u041B\u0415\u041A\u0422\u0420\u0418\u0427\u0415\u0421\u0422\u0412\u041E: elementalDamage = 6f; break;
            default: elementalDamage = 0; break;
        }
        updateSlimeColor();
    }

    public void takeDamage(float amount) {
        health -= amount;
        if (health <= 0) { health = 0; }
    }

    public float getHealth() { return health; }
    public float getMaxHealth() { return maxHealth; }

    public boolean isSpeedBuffActive() { return speedBuffTimer > 0; }

    public void upgradeSpeed(float percent) {
        speedUpgradeMultiplier *= (1f + percent);
        moveSpeed = MOVE_SPEED_BASE / currentScale * speedUpgradeMultiplier;
    }

    public void upgradeMagnetRange(float percent) {
        baseMagnetRange += MAGNET_RANGE * percent;
    }

    public void upgradeElementalDamage(float amount) {
        elementalDamage += amount;
    }

    public void upgradeMaxHealth(float amount) {
        maxHealth += amount;
        health += amount;
    }

    public void upgradeJump(float amount) {
        jumpVelocity += amount / currentScale;
    }

    public int getFacingDirection() { return facingDirection; }
    public float getElementalDamage() { return elementalDamage; }
    public Element getElement() { return element; }
    public boolean isMagnetActive() { return magnetTimer > 0; }
    public float getMagnetRange() { return baseMagnetRange; }
    public Array<Weapon> getWeapons() { return weapons; }

    public void drawControls() {
        controlStage.draw();
    }

    public Stage getControlStage() {
        return controlStage;
    }

    public float getCurrentScale() {
        return currentScale;
    }

    @Override
    public Rectangle getBounds() {
        float s = HITBOX_SHRINK * currentScale;
        return new Rectangle(position.x + s, position.y + s, size.x - s * 2, size.y - s * 2);
    }

    @Override
    public void dispose() {
        texture.dispose();
        controlStage.dispose();
        skin.dispose();
        for (Weapon w : weapons) w.dispose();
    }
}
