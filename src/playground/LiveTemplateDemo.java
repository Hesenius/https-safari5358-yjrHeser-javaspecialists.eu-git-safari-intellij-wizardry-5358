package playground;

import java.util.Iterator;
import java.util.List;

public class LiveTemplateDemo {
    public static void main(String[] args) { // main / psvm
        List<String> students = List.of("DK", "TP", "JK", "SK", "MT", "PT", "AN");
        int[] hashCodes = students.stream().mapToInt(Object::hashCode).toArray();


        System.out.println("hello world"); // sout
        System.out.println("students.size() = " + students.size());

        for (String student : students) { // iter
            System.out.println(student);
        }

        for (int i = 0; i < 10; i++) { // fori
            System.out.println("Hello " + i);
        }

        for (Iterator<String> it = students.iterator(); it.hasNext(); ) { // itco
            String next = it.next();
            System.out.println("next = " + next);
        }

        int total = 0;
        for (int i = 0; i < hashCodes.length; i++) {
            int hashCode = hashCodes[i];
            total += hashCode;
        }
        System.out.println("total = " + total);
    }

    public void foo(int id, long nanos) {
        System.out.println("LiveTemplateDemo.foo"); // soutm
        System.out.println("id = " + id + ", nanos = " + nanos); // soutp
    }
}
