import java.sql.*;
import java.util.Scanner;

public class ClinicSystem {
    static final String JDBC_URL = "jdbc:mysql://localhost:3306/clinic";
    static final String USER = "seu_usuario";
    static final String PASSWORD = "sua_senha";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            createTables(connection);

            Scanner scanner = new Scanner(System.in);
            int choice;

            do {
                System.out.println("1. Agendar consulta");
                System.out.println("2. Visualizar consultas");
                System.out.println("3. Sair");
                System.out.print("Escolha uma opção: ");
                choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        scheduleAppointment(connection, scanner);
                        break;
                    case 2:
                        viewAppointments(connection);
                        break;
                    case 3:
                        System.out.println("Saindo do sistema.");
                        break;
                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                }
            } while (choice != 3);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTables(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS clinic");
            statement.executeUpdate("USE clinic");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Patients (" +
                    "patient_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(255) NOT NULL," +
                    "age INT," +
                    "gender VARCHAR(10)," +
                    "contact_number VARCHAR(15)," +
                    "PRIMARY KEY (patient_id)" +
                    ")");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Appointments (" +
                    "appointment_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "patient_id INT," +
                    "appointment_date DATE," +
                    "doctor_name VARCHAR(255)," +
                    "PRIMARY KEY (appointment_id)," +
                    "FOREIGN KEY (patient_id) REFERENCES Patients(patient_id)" +
                    ")");
        }
    }

    private static void scheduleAppointment(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Digite o ID do paciente: ");
        int patientId = scanner.nextInt();

        System.out.println("Digite a data da consulta (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();

        System.out.println("Digite o nome do médico: ");
        String doctorName = scanner.next();

        String sql = "INSERT INTO Appointments (patient_id, appointment_date, doctor_name) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, patientId);
            preparedStatement.setDate(2, Date.valueOf(appointmentDate));
            preparedStatement.setString(3, doctorName);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Consulta agendada com sucesso!");
            } else {
                System.out.println("Erro ao agendar a consulta. Tente novamente.");
            }
        }
    }

    private static void viewAppointments(Connection connection) throws SQLException {
        String sql = "SELECT * FROM Appointments";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            System.out.println("Consultas agendadas:");

            while (resultSet.next()) {
                int appointmentId = resultSet.getInt("appointment_id");
                int patientId = resultSet.getInt("patient_id");
                Date appointmentDate = resultSet.getDate("appointment_date");
                String doctorName = resultSet.getString("doctor_name");

                System.out.println("ID da Consulta: " + appointmentId);
                System.out.println("ID do Paciente: " + patientId);
                System.out.println("Data da Consulta: " + appointmentDate);
                System.out.println("Nome do Médico: " + doctorName);
                System.out.println("-----------------------");
            }
        }
    }
}

