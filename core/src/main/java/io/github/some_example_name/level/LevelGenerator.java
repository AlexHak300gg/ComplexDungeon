package io.github.some_example_name.level;

import com.badlogic.gdx.math.MathUtils;

public class LevelGenerator {
    private static final int SURFACE_Y = 8;
    private static final int CEILING_Y = 2;
    private static final int ROOM_MIN_W = 5;
    private static final int ROOM_MAX_W = 10;
    private static final int ROOM_MIN_H = 4;
    private static final int ROOM_MAX_H = 6;
    private static final int PIT_COUNT = 3;
    private static final int TUNNEL_H = 3;
    private static final int MAX_JUMP_HEIGHT = 4;
    private static final int MAX_GEN_ATTEMPTS = 20;

    public TileMap generate(int mapWidth, int mapHeight) {
        for (int attempt = 0; attempt < MAX_GEN_ATTEMPTS; attempt++) {
            TileMap map = generateInner(mapWidth, mapHeight);
            if (isReachable(map, mapWidth, mapHeight)) {
                return map;
            }
        }
        return generateInner(mapWidth, mapHeight);
    }

    private TileMap generateInner(int mapWidth, int mapHeight) {
        TileMap map = new TileMap(mapWidth, mapHeight);

        fillCeilingAndUnderground(map, mapWidth, mapHeight);
        carveSurfacePath(map, mapWidth);
        carvePits(map, mapWidth, mapHeight);
        generateUndergroundRooms(map, mapWidth, mapHeight);
        addFloatingPlatforms(map, mapWidth);

        int spawnTileX = 3;
        for (int dx = -1; dx <= 1; dx++) {
            int tx = spawnTileX + dx;
            if (tx >= 1 && tx < mapWidth - 1) {
                for (int dy = 1; dy <= 4; dy++) {
                    map.setTile(tx, SURFACE_Y + dy, TileType.EMPTY);
                }
            }
        }
        map.setSpawn(spawnTileX * TileMap.TILE_SIZE, (SURFACE_Y + 1) * TileMap.TILE_SIZE);

        int exitX = MathUtils.random(mapWidth - 12, mapWidth - 5);
        for (int dx = -1; dx <= 1; dx++) {
            int ex = exitX + dx;
            if (ex >= 0 && ex < mapWidth) {
                map.setTile(ex, SURFACE_Y, dx == 0 ? TileType.EXIT : TileType.GRASS);
            }
        }
        map.setExit(exitX * TileMap.TILE_SIZE, (SURFACE_Y + 1) * TileMap.TILE_SIZE);

        return map;
    }

    private boolean isReachable(TileMap map, int mapW, int mapH) {
        int exitTileX = map.getExitX() / TileMap.TILE_SIZE;
        int spawnGX = 3;
        int spawnGY = SURFACE_Y;

        if (!map.isSolid(spawnGX, spawnGY)) return false;

        boolean[][] visited = new boolean[mapW][mapH];
        int[] queueX = new int[mapW * mapH];
        int[] queueY = new int[mapW * mapH];
        int head = 0, tail = 0;

        visited[spawnGX][spawnGY] = true;
        queueX[tail] = spawnGX;
        queueY[tail] = spawnGY;
        tail++;

        while (head < tail) {
            int gx = queueX[head];
            int gy = queueY[head];
            head++;

            for (int dx = -1; dx <= 1; dx++) {
                if (gx + dx == exitTileX && gy == SURFACE_Y) return true;
            }

            for (int ndx : new int[]{-1, 1}) {
                int nx = gx + ndx;
                if (nx < 0 || nx >= mapW) continue;

                if (map.isSolid(nx, gy) && !visited[nx][gy]) {
                    visited[nx][gy] = true;
                    queueX[tail] = nx; queueY[tail] = gy; tail++;
                }

                if (gy + 1 < mapH && map.isSolid(nx, gy + 1) && !map.isSolid(nx, gy) && !map.isSolid(gx, gy + 1) && !visited[nx][gy + 1]) {
                    visited[nx][gy + 1] = true;
                    queueX[tail] = nx; queueY[tail] = gy + 1; tail++;
                }

                if (gy - 1 >= 0 && map.isSolid(nx, gy - 1) && !map.isSolid(nx, gy) && !visited[nx][gy - 1]) {
                    visited[nx][gy - 1] = true;
                    queueX[tail] = nx; queueY[tail] = gy - 1; tail++;
                }
            }

            for (int ny = gy - 1; ny >= 0; ny--) {
                if (map.isSolid(gx, ny)) {
                    if (!visited[gx][ny] && ny + 1 < mapH && !map.isSolid(gx, ny + 1)) {
                        visited[gx][ny] = true;
                        queueX[tail] = gx; queueY[tail] = ny; tail++;
                    }
                    break;
                }
                if (map.getTile(gx, ny) != TileType.EMPTY) break;
            }

            for (int jy = gy + 1; jy <= gy + MAX_JUMP_HEIGHT && jy < mapH - 1; jy++) {
                if (map.isSolid(gx, jy)) break;

                if (map.isSolid(gx, jy + 1) && jy + 2 < mapH && !map.isSolid(gx, jy + 2) && !visited[gx][jy + 1]) {
                    visited[gx][jy + 1] = true;
                    queueX[tail] = gx; queueY[tail] = jy + 1; tail++;
                }

                for (int ndx : new int[]{-1, 1}) {
                    int nx = gx + ndx;
                    if (nx < 0 || nx >= mapW) continue;
                    if (map.isSolid(nx, jy + 1) && !map.isSolid(nx, jy) && !map.isSolid(gx, jy) && !visited[nx][jy + 1]) {
                        visited[nx][jy + 1] = true;
                        queueX[tail] = nx; queueY[tail] = jy + 1; tail++;
                    }
                }
            }
        }

        return false;
    }

    private void fillCeilingAndUnderground(TileMap map, int mapW, int mapH) {
        for (int x = 0; x < mapW; x++) {
            for (int y = 0; y < CEILING_Y; y++) {
                map.setTile(x, y, TileType.DIRT);
            }
            for (int y = SURFACE_Y + 2; y < mapH; y++) {
                map.setTile(x, y, TileType.DIRT);
            }
        }
    }

    private void carveSurfacePath(TileMap map, int mapW) {
        int y = SURFACE_Y;
        for (int x = 2; x < mapW - 2; x++) {
            if (x > 5 && MathUtils.random() < 0.12f) {
                int ny = y + (MathUtils.randomBoolean() ? 1 : -1);
                if (ny >= SURFACE_Y - 1 && ny <= SURFACE_Y + 1) {
                    y = ny;
                }
            }

            for (int dx = -1; dx <= 1; dx++) {
                int tx = x + dx;
                if (tx < 1 || tx >= mapW - 1) continue;
                map.setTile(tx, y, TileType.GRASS);
                for (int d = 2; d <= 3; d++) {
                    map.setTile(tx, y + d, TileType.DIRT);
                }
                for (int a = 1; a < y; a++) {
                    if (map.getTile(tx, y - a) != TileType.EMPTY) {
                        map.setTile(tx, y - a, TileType.EMPTY);
                    }
                }
            }
        }
    }

    private void carvePits(TileMap map, int mapW, int mapH) {
        for (int i = 0; i < PIT_COUNT; i++) {
            int px = MathUtils.random(12, mapW - 16);
            int pw = MathUtils.random(2, 3);

            boolean overlap = false;
            for (int dx = -3; dx < pw + 3; dx++) {
                int cx = px + dx;
                if (cx < 1 || cx >= mapW - 1) { overlap = true; break; }
                if (map.getTile(cx, SURFACE_Y) == TileType.EMPTY) {
                    overlap = true;
                    break;
                }
            }
            if (overlap) continue;

            int pitDepth = MathUtils.random(5, 8);
            int pitBottom = SURFACE_Y + pitDepth;

            for (int dx = 0; dx < pw; dx++) {
                for (int dy = 0; dy <= pitDepth; dy++) {
                    map.setTile(px + dx, SURFACE_Y + dy, TileType.EMPTY);
                }
            }

            int landingY = pitBottom + 1;
            if (landingY < mapH) {
                for (int lx = px - 1; lx <= px + pw; lx++) {
                    if (lx >= 0 && lx < mapW) {
                        map.setTile(lx, landingY, TileType.GRASS);
                        map.setTile(lx, landingY + 1, TileType.DIRT);
                    }
                }
            }
        }
    }

    private void generateUndergroundRooms(TileMap map, int mapW, int mapH) {
        int rooms = MathUtils.random(3, 6);
        int prevCX = 10;
        int prevCY = SURFACE_Y + 10;

        for (int i = 0; i < rooms; i++) {
            int rw = MathUtils.random(ROOM_MIN_W, ROOM_MAX_W);
            int rh = MathUtils.random(ROOM_MIN_H, ROOM_MAX_H);

            int rx = MathUtils.random(prevCX + 8, prevCX + 18);
            int ry = MathUtils.random(SURFACE_Y + 4, Math.min(SURFACE_Y + 16, mapH - rh - 3));

            if (rx + rw >= mapW - 4) break;

            carveRoom(map, rx, ry, rw, rh);

            if (i > 0) {
                carveTunnel(map, prevCX, prevCY, rx + rw / 2, ry + rh / 2);
            }
            connectTunnelToSurface(map, rx + rw / 2, ry + rh / 2);

            prevCX = rx + rw / 2;
            prevCY = ry + rh / 2;
        }
    }

    private void carveRoom(TileMap map, int x, int y, int w, int h) {
        for (int cx = x; cx < x + w && cx < map.getWidth(); cx++) {
            for (int cy = y; cy < y + h && cy < map.getHeight(); cy++) {
                if (cy == y) {
                    map.setTile(cx, cy, TileType.GRASS);
                } else if (cy == y + h - 1) {
                    map.setTile(cx, cy, TileType.DIRT);
                } else {
                    map.setTile(cx, cy, TileType.EMPTY);
                }
            }
        }
    }

    private void carveTunnel(TileMap map, int x1, int y1, int x2, int y2) {
        int halfH = TUNNEL_H / 2;
        int cx = x1;
        int cy = y1;

        while (cx != x2) {
            cx += (cx < x2) ? 1 : -1;
            for (int dy = -halfH; dy <= halfH; dy++) {
                int ty = cy + dy;
                if (ty >= 0 && ty < map.getHeight()) {
                    map.setTile(cx, ty, TileType.EMPTY);
                }
            }
        }

        while (cy != y2) {
            cy += (cy < y2) ? 1 : -1;
            for (int dx = -halfH; dx <= halfH; dx++) {
                int tx = cx + dx;
                if (tx >= 0 && tx < map.getWidth()) {
                    map.setTile(tx, cy, TileType.EMPTY);
                }
            }
        }
    }

    private void connectTunnelToSurface(TileMap map, int roomCX, int roomCY) {
        for (int y = roomCY - 1; y >= SURFACE_Y; y--) {
            for (int dx = -1; dx <= 1; dx++) {
                int tx = roomCX + dx;
                if (tx >= 0 && tx < map.getWidth()) {
                    map.setTile(tx, y, TileType.EMPTY);
                }
            }
        }

        for (int dx = -2; dx <= 2; dx++) {
            int tx = roomCX + dx;
            if (tx >= 0 && tx < map.getWidth()) {
                map.setTile(tx, SURFACE_Y, TileType.EMPTY);
            }
        }
    }

    private void addFloatingPlatforms(TileMap map, int mapW) {
        int count = MathUtils.random(6, 12);
        for (int i = 0; i < count; i++) {
            int px = MathUtils.random(3, mapW - 5);
            int py = MathUtils.random(CEILING_Y + 2, SURFACE_Y - 1);

            if (py >= map.getHeight() - 1) continue;

            boolean blocked = false;
            for (int dx = -2; dx <= 4; dx++) {
                int cx = px + dx;
                if (cx < 1 || cx >= mapW - 1) { blocked = true; break; }
                for (int dy = -1; dy <= 1; dy++) {
                    int cy = py + dy;
                    if (cy >= 0 && cy < map.getHeight() && map.isSolid(cx, cy)) {
                        blocked = true;
                        break;
                    }
                }
                if (blocked) break;
            }
            if (blocked) continue;

            int len = MathUtils.random(2, 4);
            for (int dx = 0; dx < len; dx++) {
                if (px + dx < map.getWidth()) {
                    map.setTile(px + dx, py, TileType.PLATFORM);
                }
            }
        }
    }
}
