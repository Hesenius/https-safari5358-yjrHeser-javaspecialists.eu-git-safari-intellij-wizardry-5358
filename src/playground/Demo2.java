package playground;

import java.util.List;

public class Demo2 {
    public static void main(String[] args) {
    }
    private void foo(int x, int y) {}
    private void bar(int y, int x) {
        foo(y, x);
    }
}
