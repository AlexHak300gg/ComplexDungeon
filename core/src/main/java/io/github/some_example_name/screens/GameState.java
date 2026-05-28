package io.github.some_example_name.screens;

public class GameState {
    public int kills;
    public int score;
    public float biomassAbsorbed;
    public float elapsedTime;
    public boolean gameOver;
    public boolean victory;
    public boolean upgradePending;

    public GameState() {
        reset();
    }

    public void reset() {
        kills = 0;
        score = 0;
        biomassAbsorbed = 0;
        elapsedTime = 0;
        gameOver = false;
        victory = false;
        upgradePending = false;
    }

    public void addKill() {
        kills++;
        score += 100;
    }

    public void addBiomass(float amount) {
        biomassAbsorbed += amount;
        score += (int)(amount * 50);
    }
}
