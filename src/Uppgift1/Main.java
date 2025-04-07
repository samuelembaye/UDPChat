package Uppgift1;

public class Main {
    public static void main(String[] args) {
        Person p1 = new Person();
        Person p2 = new Person("Asli",25);

        p1.setName("Sami");
        p1.setAge(25);
        System.out.println(p1);
        System.out.println(p2);
        Util.printer(p1.toString());
    }
}
