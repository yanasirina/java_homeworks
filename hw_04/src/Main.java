import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Worker worker = new Worker("Alex", 22, 50000.0);
        System.out.println(worker);

        worker.setName("John");
        worker.setAge(30);
        worker.setSalary(60000.0);
        System.out.println(worker);

        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите название таблицы: ");
        String tableName = scanner.nextLine();
        DBWorker dbWorker = new DBWorker(tableName);
        dbWorker.showMenu();
    }
}