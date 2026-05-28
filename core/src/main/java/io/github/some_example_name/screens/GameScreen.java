package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import io.github.some_example_name.ComplexDungeonGame;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.entity.PlayerSlime;
import io.github.some_example_name.entity.enemy.BaseEnemy;
import io.github.some_example_name.entity.enemy.EliteBoss;
import io.github.some_example_name.entity.enemy.EnemySpawner;
import io.github.some_example_name.entity.enemy.RangedSpitter;
import io.github.some_example_name.item.PickupItem;
import io.github.some_example_name.item.PickupSpawner;
import io.github.some_example_name.level.LevelGenerator;
import io.github.some_example_name.level.LevelRenderer;
import io.github.some_example_name.level.TileMap;
import io.github.some_example_name.weapon.Bullet;

public class GameScreen extends BaseScreen {
    private static final float WORLD_WIDTH = 640f;
    private static final float WORLD_HEIGHT = 360f;
    private static final float CRUSH_SCALE_THRESHOLD = 2.5f;

    private EntityManager entityManager;
    private PlayerSlime player;

    private OrthographicCamera camera;
    private OrthographicCamera uiCamera;
    private ExtendViewport viewport;

    private TileMap tileMap;
    private LevelRenderer levelRenderer;

    private Array<PickupItem> pickups;
    private Array<BaseEnemy> enemies;
    private EnemySpawner spawner;

    private GameState gameState;
    private GameHUD hud;
    private UpgradeOverlay upgradeOverlay;

    private Texture bossBarBg;
    private Texture bossBarFill;

    private boolean transitioning;

    public GameScreen(ComplexDungeonGame game) {
        super(game);
    }

    @Override
    public void show() {
        transitioning = false;

        LevelGenerator generator = new LevelGenerator();
        tileMap = generator.generate(200, 50);

        levelRenderer = new LevelRenderer(tileMap);
        entityManager = new EntityManager();

        player = new PlayerSlime(tileMap);
        entityManager.add(player);
        player.initWeapons(entityManager);

        PickupSpawner ps = new PickupSpawner();
        pickups = ps.generate(tileMap, 15);

        enemies = new Array<>();
        spawner = new EnemySpawner();

        gameState = new GameState();
        BitmapFont font = game.getAssetProvider().getFont("fonts/Roboto-Medium.ttf");
        hud = new GameHUD(font);
        upgradeOverlay = new UpgradeOverlay(font);

        Pixmap bg = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bg.setColor(Color.DARK_GRAY);
        bg.fill();
        bossBarBg = new Texture(bg);
        bg.dispose();

        Pixmap fill = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        fill.setColor(Color.RED);
        fill.fill();
        bossBarFill = new Texture(fill);
        fill.dispose();

        camera = new OrthographicCamera();
        uiCamera = new OrthographicCamera();
        viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(player.getControlStage());
        multiplexer.addProcessor(hud.getStage());
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        if (transitioning) return;

        gameState.elapsedTime += delta;

        spawner.update(delta, camera, tileMap, enemies, player);

        for (int i = enemies.size - 1; i >= 0; i--) {
            BaseEnemy e = enemies.get(i);
            if (!e.isAlive()) {
                if (e instanceof EliteBoss) {
                    gameState.victory = true;
                }
                gameState.addKill();
                enemies.removeIndex(i);
                continue;
            }
            if (e instanceof RangedSpitter && ((RangedSpitter) e).getEntityManager() == null) {
                ((RangedSpitter) e).setEntityManager(entityManager);
            }
            e.update(delta);
        }

        entityManager.updateAll(delta);
        Array<Entity> enemyEntities = new Array<>();
        for (BaseEnemy e : enemies) { if (e.isAlive()) enemyEntities.add(e); }
        player.updateWeapons(delta, enemyEntities);

        updateEnemyCollisions();
        updateMagnet();
        updatePickupCollisions();
        checkGameConditions();

        hud.update(player, gameState);

        Vector3 target = new Vector3(
            player.getPosition().x + player.getSize().x / 2f,
            player.getPosition().y + player.getSize().y / 2f,
            0
        );
        camera.position.lerp(target, 0.1f);
        float hw = camera.viewportWidth / 2f;
        float hh = camera.viewportHeight / 2f;
        camera.position.x = Math.max(hw, Math.min(camera.position.x, tileMap.getWorldWidth() - hw));
        camera.position.y = Math.max(hh, Math.min(camera.position.y, tileMap.getWorldHeight() - hh));
        camera.update();

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        levelRenderer.render(batch, camera);

        for (PickupItem p : pickups) {
            if (p.isAlive()) p.draw(batch);
        }

        entityManager.drawAll(batch);

        for (BaseEnemy e : enemies) {
            if (e.isAlive()) e.draw(batch);
        }

        player.drawWeapons(batch);
        batch.end();

        drawBossHealthBar();

        hud.render(batch, uiCamera);

        if (upgradeOverlay.isVisible()) {
            upgradeOverlay.render(batch, uiCamera);
        }

        player.drawControls();
    }

    private void checkGameConditions() {
        if (gameState.victory) {
            transitioning = true;
            game.getScreenManager().switchTo(new VictoryScreen(game, gameState.score,
                gameState.elapsedTime, gameState.biomassAbsorbed, gameState.kills));
            return;
        }

        if (player.getHealth() <= 0) {
            gameState.gameOver = true;
            transitioning = true;
            game.getScreenManager().switchTo(new GameOverScreen(game, gameState.score,
                gameState.elapsedTime, gameState.biomassAbsorbed));
            return;
        }
    }

    private void updateEnemyCollisions() {
        Rectangle playerBounds = player.getBounds();
        float playerScale = player.getCurrentScale();

        Array<BaseEnemy> deadEnemies = new Array<>();
        Array<Bullet> deadBullets = new Array<>();

        for (Entity e : entityManager.getEntities()) {
            if (!e.isAlive()) continue;
            if (e instanceof Bullet) {
                Bullet b = (Bullet) e;
                Rectangle bBounds = b.getBounds();
                for (BaseEnemy enemy : enemies) {
                    if (!enemy.isAlive()) continue;
                    if (bBounds.overlaps(enemy.getBounds())) {
                        if (b.isFromPlayer()) {
                            enemy.applyDamage(b.getDamage());
                            float kx = b.getVelocity().x > 0 ? 100f : -100f;
                            enemy.applyKnockback(kx, 50f);
                        }
                        deadBullets.add(b);
                    }
                }
            }
        }

        for (BaseEnemy enemy : enemies) {
            if (!enemy.isAlive()) continue;
            if (playerBounds.overlaps(enemy.getBounds())) {
                float enemySize = Math.max(enemy.getSize().x, enemy.getSize().y);
                float playerSize = Math.max(player.getSize().x, player.getSize().y);

                if (playerSize > enemySize * CRUSH_SCALE_THRESHOLD) {
                    enemy.kill();
                    player.grow(0.05f);
                    gameState.addBiomass(0.05f);
                } else {
                    player.takeDamage(enemy.getDamage());
                    player.getVelocity().x += (player.getPosition().x < enemy.getPosition().x ? -200f : 200f);
                    player.getVelocity().y += 100f;
                }
            }
        }

        for (BaseEnemy e : enemies) {
            if (!e.isAlive()) {
                if (e instanceof EliteBoss) {
                    gameState.victory = true;
                }
                deadEnemies.add(e);
                gameState.addKill();
            }
        }
        enemies.removeAll(deadEnemies, true);

        for (Bullet b : deadBullets) {
            b.kill();
        }
    }

    private void drawBossHealthBar() {
        EliteBoss boss = null;
        for (BaseEnemy e : enemies) {
            if (e instanceof EliteBoss && e.isAlive()) {
                boss = (EliteBoss) e;
                break;
            }
        }
        if (boss == null) return;

        float barW = 200f;
        float barH = 16f;
        float screenX = (Gdx.graphics.getWidth() - barW) / 2f;
        float screenY = Gdx.graphics.getHeight() - 40f;

        uiCamera.setToOrtho(false);
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();

        float ratio = boss.getHealth() / boss.getMaxHealth();
        batch.draw(bossBarBg, screenX, screenY, barW, barH);
        batch.draw(bossBarFill, screenX, screenY, barW * ratio, barH);
        BitmapFont font = game.getAssetProvider().getFont("fonts/Roboto-Medium.ttf");
        font.draw(batch, "\u0417\u0434\u043E\u0440\u043E\u0432\u044C\u0435 \u0411\u043E\u0441\u0441\u0430", screenX, screenY + 30f);
        batch.end();
    }

    private void updateMagnet() {
        if (!player.isMagnetActive()) return;

        float px = player.getPosition().x + player.getSize().x / 2f;
        float py = player.getPosition().y + player.getSize().y / 2f;
        float range = player.getMagnetRange();

        for (PickupItem p : pickups) {
            if (!p.isAlive()) continue;
            float dx = px - (p.getPosition().x + 8);
            float dy = py - (p.getPosition().y + 8);
            if (dx * dx + dy * dy < range * range) {
                p.setMagnetized(px, py);
            }
        }
    }

    private void updatePickupCollisions() {
        Rectangle playerBounds = player.getBounds();
        for (PickupItem p : pickups) {
            if (!p.isAlive()) continue;
            p.update(Gdx.graphics.getDeltaTime());
            if (playerBounds.overlaps(p.getBounds())) {
                p.apply(player);
                p.kill();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        player.getControlStage().getViewport().update(width, height, true);
        hud.resize(width, height);
        upgradeOverlay.resize(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        if (player != null) player.dispose();
        if (levelRenderer != null) levelRenderer.dispose();
        if (hud != null) hud.dispose();
        if (upgradeOverlay != null) upgradeOverlay.dispose();
        entityManager.clear();
        bossBarBg.dispose();
        bossBarFill.dispose();
    }
}
