package playground;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ListComparison {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            test(new LinkedList<>());
            test(new ArrayList<>());
            System.out.println();
        }
    }

    private static void test(List<Integer> list) {
        for (int i = 0; i < 1_000_000; i++) {
            list.add(i);
        }
        System.out.println(list.getClass().getSimpleName() + " " + list.size()
                + " elements");

        long time = System.nanoTime();
        try {
            for (int i = 0; i < 100; i++) {
                Integer value = list.remove(500_000);
                list.add(500_000, value);
            }
        } finally {
            time = System.nanoTime() - time;
            System.out.printf("time = %dms%n",
                    (time / 1_000_000));
        }
    }
}
