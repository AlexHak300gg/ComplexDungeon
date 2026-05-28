package io.github.some_example_name.level;

public class TileMap {
    public static final int TILE_SIZE = 32;

    private final int width;
    private final int height;
    private final TileType[][] tiles;
    private int spawnX;
    private int spawnY;
    private int exitX;
    private int exitY;

    public TileMap(int width, int height) {
        this.width = width;
        this.height = height;
        tiles = new TileType[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = TileType.EMPTY;
            }
        }
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public TileType getTile(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return TileType.DIRT;
        return tiles[x][y];
    }

    public void setTile(int x, int y, TileType type) {
        if (x < 0 || x >= width || y < 0 || y >= height) return;
        tiles[x][y] = type;
    }

    public boolean isSolid(int x, int y) {
        return getTile(x, y).isSolid();
    }

    public int getSpawnX() { return spawnX; }
    public int getSpawnY() { return spawnY; }
    public void setSpawn(int x, int y) { spawnX = x; spawnY = y; }

    public int getExitX() { return exitX; }
    public int getExitY() { return exitY; }
    public void setExit(int x, int y) { exitX = x; exitY = y; }

    public int getWorldWidth() { return width * TILE_SIZE; }
    public int getWorldHeight() { return height * TILE_SIZE; }
}
