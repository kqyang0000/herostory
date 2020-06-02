public class TestMain {

    public static void main(String[] args)  {
        for (int i = 0; i < 10000; i++) {
            System.out.println("第 " + i + "次测试");
            (new TestMain()).test1();
        }
    }

    private void test1()  {
        TestUser newUser = new TestUser();
        newUser.currHp = 100;

        Thread t1 = new Thread(() -> {
            newUser.subtractHp(1);
        });
        Thread t2 = new Thread(() -> {
            newUser.subtractHp(1);
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (newUser.currHp != 98) {
            throw new RuntimeException("当前血量错误, currHp = " + newUser.currHp);
        } else {
            System.out.println("当前血量正确");
        }
    }

    private void test2() throws InterruptedException {
        TestUser newUser = new TestUser();
        newUser.currHp = 100;

        Thread t1 = new Thread(() -> {
            newUser.currHp = newUser.currHp - 1;
        });
        Thread t2 = new Thread(() -> {
            newUser.currHp = newUser.currHp - 1;
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        if (newUser.currHp != 98) {
            throw new RuntimeException("当前血量错误, currHp = " + newUser.currHp);
        } else {
            System.out.println("当前血量正确");
        }
    }
}
