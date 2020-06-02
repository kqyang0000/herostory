public class TestUser {
    public int currHp;

    synchronized public void subtractHp(int val) {
        if (val <= 0) {
            return;
        }

        this.currHp = this.currHp - val;
    }
}
