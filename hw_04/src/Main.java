public class Main {
    public static void main(String[] args) {
        Worker worker = new Worker("Alex", 22, 50000.0);
        System.out.println(worker);

        worker.setName("John");
        worker.setAge(30);
        worker.setSalary(60000.0);
        System.out.println(worker);
    }
}