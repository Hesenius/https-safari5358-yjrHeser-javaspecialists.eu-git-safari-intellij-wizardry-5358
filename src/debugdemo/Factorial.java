package debugdemo;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Factorial {
    public long calculate(int n) {
        return calculate(0, n);
    }

    public long calculate(int n, int m) {
        if (n == m) {
            if (n == 0) return 1;
            else return n;
        }
        int k = (n + m) >>> 1;
        return Math.absExact(
                checkOutOfBounds(
                        calculate(n, k - 1) * calculate(k, m)
                )
        );
    }

    private long checkOutOfBounds(long num) {
        if (num < 0) throw new ArithmeticException("Number overflow");
        return num;
    }

    public static void main(String[] args) {
        Factorial factorial = new Factorial();
        Map<Integer, Long> results = IntStream.rangeClosed(0, 10)
                .boxed()
                .collect(Collectors.toUnmodifiableMap(
                        Function.identity(),
                        factorial::calculate
                ));
        results.forEach((n, result) ->
                System.out.printf("factorial(%d) = %d%n", n, result));
    }
}
