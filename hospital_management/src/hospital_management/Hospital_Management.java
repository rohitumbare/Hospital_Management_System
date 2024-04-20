package hospital_management;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import hospital_management.Doctor;
import hospital_management.Patient;

public class Hospital_Management {
	private static final String url = "jdbc:mysql://localhost:3306/hospital";
	private static final String username = "root";
	private static final String password = "root";

	public static final String RED = "\u001B[31m";
	public static final String RESET = "\u001B[0m";
	public static final String GREEN = "\u001B[32m";
	public static final String YELLOW = "\u001B[33m";
	public static final String Magenta = "\u001B[35m";
	public static final String Cyan = "\u001B[36m";

	public static void main(String[] args) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Scanner scanner = new Scanner(System.in);
		try {
			Connection connection = DriverManager.getConnection(url, username, password);
			Patient patient = new Patient(connection, scanner);
			Doctor doctor = new Doctor(connection);
			while (true) {
				System.out.println(Magenta + "*** HOSPITAL MANAGEMENT SYSTEM ***" + RESET);
				System.out.println(Cyan + "1. Add Patient");
				System.out.println("2. View Patients");
				System.out.println("3. View Doctors");
				System.out.println("4. Book Appointment");
				System.out.println("5. Exit");
				System.out.println("Enter your choice: " + RESET);
				int choice = scanner.nextInt();

				switch (choice) {
				case 1:
					patient.addPatient();
					System.out.println();
					break;
				case 2:
					patient.viewPatients();
					System.out.println();
					break;
				case 3:
					doctor.viewDoctors();
					System.out.println();
					break;
				case 4:
					bookAppointment(patient, doctor, connection, scanner);
					System.out.println();
					break;
				case 5:
					System.out.println(Magenta + "THANK YOU! FOR USING HOSPITAL MANAGEMENT SYSTEM!!" + RESET);
					return;
				default:
					System.out.println(YELLOW + "Enter valid choice!!!" + RESET);
					break;
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner) {
		System.out.print("Enter Patient Id: ");
		int patientId = scanner.nextInt();
		System.out.print("Enter Doctor Id: ");
		int doctorId = scanner.nextInt();
		System.out.print("Enter appointment date (YYYY-MM-DD): ");
		String appointmentDate = scanner.next();
		if (patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)) {
			if (checkDoctorAvailability(doctorId, appointmentDate, connection)) {
				String appointmentQuery = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES(?, ?, ?)";
				try {
					PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
					preparedStatement.setInt(1, patientId);
					preparedStatement.setInt(2, doctorId);
					preparedStatement.setString(3, appointmentDate);
					int rowsAffected = preparedStatement.executeUpdate();
					if (rowsAffected > 0) {
						System.out.println(GREEN + "Appointment Booked!" + RESET);
					} else {
						System.out.println(RED + "Failed to Book Appointment!" + RESET);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println(RED + "Doctor not available on this date!!" + RESET);
			}
		} else {
			System.out.println(RED + "Either doctor or patient doesn't exist!!!" + RESET);
		}
	}

	public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection) {
		String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, doctorId);
			preparedStatement.setString(2, appointmentDate);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				int count = resultSet.getInt(1);
				if (count == 0) {
					return true;
				} else {
					return false;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}
