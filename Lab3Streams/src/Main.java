import java.util.Scanner;

public class Main extends Thread {

    private Thread thread;

    public static void main(String[] args) {
        new Thread(new Main()).start();
    }

    @Override
    public void run() {

        Scanner sc = new Scanner(System.in);

        while (sc.hasNext()) {
            String n = sc.nextLine();
            setThread(n);
        }
    }

    int i;

    private void setThread(String n) {
        thread = new Thread(n);
        System.out.println(++i + " thread name: " + thread.getName());
    }
}
