package errorStudy;

public class TestException2 {
    public static void main(String[] args) {
        TestException2 exception = new TestException2();
        exception.m1();
    }

    public void m1() {
        m2();
    }

    public void m2() {
        m3();
    }

    public void m3() {
        String name = null;
        System.out.println(name.length());
    }

}
