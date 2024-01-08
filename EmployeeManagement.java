import java.sql.*;
import java.util.Scanner;

public class EmployeeManagement {

    static final String JDBC_URL = "jdbc:mysql://localhost:3306/employee_management_system";
    static final String USER = "root";
    static final String PASSWORD = "123456789";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            initializeDatabase(connection);
            displayMenu(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void initializeDatabase(Connection connection) throws SQLException {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS employees ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "name VARCHAR(255),"
                + "position VARCHAR(255),"
                + "salary DOUBLE)";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableQuery);
        }
    }

    private static void displayMenu(Connection connection) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nEmployee Management System Menu:");
            System.out.println("1. Add new employee");
            System.out.println("2. Update employee details");
            System.out.println("3. Generate employee report");
            System.out.println("4. Exit");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    addEmployee(connection, scanner);
                    break;
                case 2:
                    updateEmployeeDetails(connection, scanner);
                    break;
                case 3:
                    generateEmployeeReport(connection);
                    break;
                case 4:
                    System.out.println("Exiting the program. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }

    private static void addEmployee(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter employee name: ");
            String name = scanner.next();

            System.out.print("Enter employee position: ");
            String position = scanner.next();

            System.out.print("Enter employee salary: ");
            double salary = scanner.nextDouble();

            String insertQuery = "INSERT INTO employees (name, position, salary) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, position);
                preparedStatement.setDouble(3, salary);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Employee added successfully!");
                } else {
                    System.out.println("Failed to add employee. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateEmployeeDetails(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter the ID of the employee you want to update: ");
            int employeeId = scanner.nextInt();

            if (!employeeExists(connection, employeeId)) {
                System.out.println("Employee with ID " + employeeId + " does not exist.");
                return;
            }

            System.out.print("Enter new position for the employee: ");
            String newPosition = scanner.next();

            System.out.print("Enter new salary for the employee: ");
            double newSalary = scanner.nextDouble();

            String updateQuery = "UPDATE employees SET position = ?, salary = ? WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, newPosition);
                preparedStatement.setDouble(2, newSalary);
                preparedStatement.setInt(3, employeeId);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Employee details updated successfully!");
                } else {
                    System.out.println("Failed to update employee details. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean employeeExists(Connection connection, int employeeId) {
        try {
            String checkQuery = "SELECT COUNT(*) FROM employees WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(checkQuery)) {
                preparedStatement.setInt(1, employeeId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void generateEmployeeReport(Connection connection) {
        try {
            String reportQuery = "SELECT id, name, position, salary FROM employees";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(reportQuery)) {
                System.out.println("\nEmployee Report:");
                System.out.printf("%-5s %-20s %-20s %-10s\n", "ID", "Name", "Position", "Salary");
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String position = resultSet.getString("position");
                    double salary = resultSet.getDouble("salary");

                    System.out.printf("%-5d %-20s %-20s %-10.2f\n", id, name, position, salary);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}