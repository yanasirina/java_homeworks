import java.sql.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.InputMismatchException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;

public class DBWorker {
    private Connection connection;
    private final String tableName;

    public DBWorker(String tableName) {
        this.tableName = tableName;
        try {
            String url = "jdbc:mysql://localhost:3306/hw4";
            String user = "root";
            String password = "";

            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Открыто соединение с БД.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean checkIfTableExists(String tableName) {
        try {
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet resultSet = meta.getTables(null, null, tableName, null);
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void listTables() {
        String query = "SHOW TABLES";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            System.out.println("Таблицы в базе данных:");
            while (resultSet.next()) {
                System.out.println(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                + "id INT PRIMARY KEY AUTO_INCREMENT,"
                + "name VARCHAR(50),"
                + "age INT,"
                + "salary DOUBLE)";
        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
            System.out.println("Таблица '" + tableName + "' создана (или уже имелась).");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addWorkerFromInput() {
        if (!checkIfTableExists(tableName)) {
            System.out.println("Таблица не существует, невозможно добавить сотрудника...");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите имя: ");
        String name = scanner.nextLine();
        System.out.print("Введите возраст: ");
        int age = scanner.nextInt();
        System.out.print("Введите зарплату: ");
        double salary = scanner.nextDouble();
        addWorker(new Worker(name, age, salary));
    }

    private void addWorker(Worker worker) {
        String query = "INSERT INTO " + tableName + " (name, age, salary) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, worker.getName());
            preparedStatement.setInt(2, worker.getAge());
            preparedStatement.setDouble(3, worker.getSalary());
            preparedStatement.executeUpdate();
            System.out.println("Добавлен сотрудник: " + worker);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void listAllWorkers() {
        if (!checkIfTableExists(tableName)) {
            System.out.println("Таблица не существует, невозможно вывести записи...");
            return;
        }

        List<Worker> workers = getAllWorkers();
        for (Worker worker : workers) {
            System.out.println(worker);
        }
    }

    private List<Worker> getAllWorkers() {
        List<Worker> workers = new ArrayList<>();
        String query = "SELECT name, age, salary FROM " + tableName;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                double salary = resultSet.getDouble("salary");
                workers.add(new Worker(name, age, salary));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workers;
    }

    public void saveToExcel() {
        if (!checkIfTableExists(tableName)) {
            System.out.println("Таблица не существует, невозможно получить сотрудников...");
            return;
        }

        List<Worker> workers = getAllWorkers();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(tableName);

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Имя");
            headerRow.createCell(1).setCellValue("Возраст");
            headerRow.createCell(2).setCellValue("Зарплата");

            int rowNum = 1;
            for (Worker worker : workers) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(worker.getName());
                row.createCell(1).setCellValue(worker.getAge());
                row.createCell(2).setCellValue(worker.getSalary());
            }

            String excelName = String.format("%s.xlsx", tableName);
            try (FileOutputStream fileOut = new FileOutputStream(excelName)) {
                workbook.write(fileOut);
                System.out.println("Сотрудники сохарнены excel-файл " + excelName);
            }

            System.out.println("Результат экспорта:");
            for (Worker worker : workers) {
                System.out.println(worker);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Соединение с БД закрыто.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void showMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nДобро пожаловать!");
            System.out.println("1. Вывести имеющиеся таблицы");
            System.out.println("2. Создать таблицу");
            System.out.println("3. Добавить сотрудника");
            System.out.println("4. Вывести всех сотрудников");
            System.out.println("5. Сохранить сотрудников в Excel");
            System.out.println("0. Выйти");
            System.out.print("Выберите действие: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> listTables();
                    case 2 -> createTable();
                    case 3 -> addWorkerFromInput();
                    case 4 -> listAllWorkers();
                    case 5 -> saveToExcel();
                    case 0 -> {
                        System.out.println("Выход...");
                        closeConnection();
                        return;
                    }
                    default -> System.out.println("Некорректный вариант, попробуйте еще раз.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Введите целое число.");
                scanner.nextLine();
            }
        }
    }
}
