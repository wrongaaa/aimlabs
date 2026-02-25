package com.aimlabs.game;

/**
 * 游戏统计数据
 */
public class GameStats {
    private int score = 0;
    private int hits = 0;
    private int misses = 0;
    private int totalShots = 0;
    private int targetsSpawned = 0;
    private int targetsExpired = 0;
    private long totalReactionTime = 0;
    private int reactionCount = 0;
    private long bestReactionTime = Long.MAX_VALUE;
    private double totalTrackTime = 0;
    private double onTargetTime = 0;

    public void recordHit(long reactionTimeMs) {
        hits++;
        totalShots++;
        score += 100;
        if (reactionTimeMs > 0) {
            totalReactionTime += reactionTimeMs;
            reactionCount++;
            if (reactionTimeMs < bestReactionTime) {
                bestReactionTime = reactionTimeMs;
            }
        }
    }

    public void recordMiss() {
        misses++;
        totalShots++;
        score = Math.max(0, score - 10);
    }

    public void recordTargetSpawned() { targetsSpawned++; }
    public void recordTargetExpired() { targetsExpired++; }

    public void addTrackTime(double dt, boolean onTarget) {
        totalTrackTime += dt;
        if (onTarget) onTargetTime += dt;
    }

    public double getAccuracy() {
        if (totalShots == 0) return 0;
        return (double) hits / totalShots * 100;
    }

    public long getAverageReactionTime() {
        if (reactionCount == 0) return 0;
        return totalReactionTime / reactionCount;
    }

    public double getTrackAccuracy() {
        if (totalTrackTime == 0) return 0;
        return onTargetTime / totalTrackTime * 100;
    }

    public void reset() {
        score = 0; hits = 0; misses = 0; totalShots = 0;
        targetsSpawned = 0; targetsExpired = 0;
        totalReactionTime = 0; reactionCount = 0;
        bestReactionTime = Long.MAX_VALUE;
        totalTrackTime = 0; onTargetTime = 0;
    }

    public int getScore() { return score; }
    public void setScore(int s) { this.score = s; }
    public int getHits() { return hits; }
    public int getMisses() { return misses; }
    public int getTotalShots() { return totalShots; }
    public int getTargetsSpawned() { return targetsSpawned; }
    public int getTargetsExpired() { return targetsExpired; }
    public long getBestReactionTime() { return bestReactionTime == Long.MAX_VALUE ? 0 : bestReactionTime; }
    public double getTotalTrackTime() { return totalTrackTime; }
    public double getOnTargetTime() { return onTargetTime; }
}
