import java.util.Scanner;

class Message extends Thread {

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Working...");
        }
    }
}

public class Main {

    static Message Calc;

    public static void main(String[] args) {

        Calc = new Message();
        Calc.start();

        while(true) {
            Scanner sc = new Scanner(System.in);
            double a = sc.nextDouble();
            double b = sc.nextDouble();
            System.out.println(a + b);
        }
    }
}
