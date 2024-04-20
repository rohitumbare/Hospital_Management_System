package hospital_management;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Patient {

	private Connection connection;
	private Scanner scanner;

	public static final String RED = "\u001B[31m";
	public static final String RESET = "\u001B[0m";
	public static final String GREEN = "\u001B[32m";

	public Patient(Connection connection, Scanner scanner) {
		this.connection = connection;
		this.scanner = scanner;
	}

	public void addPatient() {
		System.out.print("Enter Patient Name: ");
		String name = scanner.next();
		System.out.print("Enter Patient Age: ");
		int age = scanner.nextInt();
		System.out.print("Enter Patient Gender: ");
		String gender = scanner.next();

		try {
			String query = "INSERT INTO patients(name, age, gender) VALUES(?, ?, ?)";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, name);
			preparedStatement.setInt(2, age);
			preparedStatement.setString(3, gender);
			int affectedRows = preparedStatement.executeUpdate();
			if (affectedRows > 0) {
				System.out.println(GREEN + "Patient Added Successfully!!" + RESET);
			} else {
				System.out.println(RED + "Failed to add Patient!!" + RESET);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void viewPatients() {
		String query = "select * from patients";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			ResultSet resultSet = preparedStatement.executeQuery();
			System.out.println("Patients: ");
			System.out.println("+------------+--------------------+----------+------------+");
			System.out.println("| Patient Id | Name               | Age      | Gender     |");
			System.out.println("+------------+--------------------+----------+------------+");
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				int age = resultSet.getInt("age");
				String gender = resultSet.getString("gender");
				System.out.printf("| %-10s | %-18s | %-8s | %-10s |\n", id, name, age, gender);
				System.out.println("+------------+--------------------+----------+------------+");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean getPatientById(int id) {
		String query = "SELECT * FROM patients WHERE id = ?";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, id);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
