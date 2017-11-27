import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        double a = sc.nextDouble();
        double b = sc.nextDouble();

        System.out.println(PCalc.sum(a, b));
        System.out.println(PCalc.minus(a, b));
        System.out.println(PCalc.mult(a, b));
        System.out.println(PCalc.div(a, b));
    }

}

