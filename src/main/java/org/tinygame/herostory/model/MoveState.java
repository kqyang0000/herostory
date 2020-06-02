package org.tinygame.herostory.model;

/**
 * 移动状态
 */
public class MoveState {
    /**
     * 起始位置X
     */
    public float fromPosX;
    /**
     * 起始位置Y
     */
    public float fromPosY;
    /**
     * 目标位置X
     */
    public float toPosX;
    /**
     * 目标位置Y
     */
    public float toPosY;
    /**
     * 起始时间
     */
    public long startTime;

    public float getFromPosX() {
        return fromPosX;
    }

    public void setFromPosX(float fromPosX) {
        this.fromPosX = fromPosX;
    }

    public float getFromPosY() {
        return fromPosY;
    }

    public void setFromPosY(float fromPosY) {
        this.fromPosY = fromPosY;
    }

    public float getToPosX() {
        return toPosX;
    }

    public void setToPosX(float toPosX) {
        this.toPosX = toPosX;
    }

    public float getToPosY() {
        return toPosY;
    }

    public void setToPosY(float toPosY) {
        this.toPosY = toPosY;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
