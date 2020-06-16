package org.tinygame.herostory.rank;

public class RankItem {
    /**
     * 排名ID
     */
    private int rankId;
    /**
     * 用户ID
     */
    private int userId;
    /**
     * 用户名称
     */
    private String userName;
    /**
     * 英雄形象
     */
    private String heroAvatar;
    /**
     * 获胜次数
     */
    private int win;

    public int getRankId() {
        return rankId;
    }

    public void setRankId(int rankId) {
        this.rankId = rankId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHeroAvatar() {
        return heroAvatar;
    }

    public void setHeroAvatar(String heroAvatar) {
        this.heroAvatar = heroAvatar;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }
}
