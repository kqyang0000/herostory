package org.tinygame.herostory.model;

public class User {
    /**
     * 用户id
     */
    private int userId;
    /**
     * 英雄形象
     */
    private String heroAvatar;
    /**
     * 血量
     */
    private int curHp;
    /**
     * 移动状态
     */
    private final MoveState moveState = new MoveState();

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getHeroAvatar() {
        return heroAvatar;
    }

    public int getCurHp() {
        return curHp;
    }

    public void setCurHp(int curHp) {
        this.curHp = curHp;
    }

    public void setHeroAvatar(String heroAvatar) {
        this.heroAvatar = heroAvatar;
    }

    public MoveState getMoveState() {
        return moveState;
    }
}
