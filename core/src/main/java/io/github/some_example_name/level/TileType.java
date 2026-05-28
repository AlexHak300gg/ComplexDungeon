package io.github.some_example_name.level;

public enum TileType {
    EMPTY(0),
    DIRT(1),
    GRASS(2),
    PLATFORM(3),
    EXIT(4);

    public final int id;

    TileType(int id) {
        this.id = id;
    }

    public boolean isSolid() {
        return this != EMPTY;
    }

    public static TileType fromId(int id) {
        for (TileType t : values()) {
            if (t.id == id) return t;
        }
        return EMPTY;
    }
}
