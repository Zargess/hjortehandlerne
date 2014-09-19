package com.example.hjortehandlerneapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.microsoft.sqlserver.jdbc.*;

public class SqlManager {
	public static final String CONNECTIONSTRING = "jdbc:sqlserver://ehprrxgjbl.database.windows.net:1433;database=Test;"
			+ "user=zargess@ehprrxgjbl;password=TETRhUIq7KJb3jHHOmm1;encrypt=true;hostNameInCertificate=*."
			+ "database.windows.net;loginTimeout=30;";
	public static final String GETALL = "SELECT * FROM Users";

	public static String createNewUser(String Name, String Password) {
		return "INSERT INTO Users (Name, Password) VALUES ('" + Name + "', '"
				+ Password + "')";
	}

	public static String EditUserPosition(String Id, String Password,
			String Location) {
		return "UPDATE Users SET Location='" + Location + "' WHERE Id=" + Id
				+ "AND Password='" + Password + "'";
	}

	public static void EditInformation(String query) {
		Connection connection = null;
		Statement statement = null;

		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

			connection = DriverManager.getConnection(CONNECTIONSTRING);

			statement = connection.createStatement();

			statement.execute(query);
		} catch (ClassNotFoundException cnfe) {

			System.out.println("ClassNotFoundException " + cnfe.getMessage());
		} catch (Exception e) {
			System.out.println("Exception " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				// Close resources.
				if (null != connection)
					connection.close();
				if (null != statement)
					statement.close();
			} catch (SQLException sqlException) {
				// No additional action if close() statements fail.
			}
		}
	}

	public static ResultSet FetchInformation(String query) {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

			connection = DriverManager.getConnection(CONNECTIONSTRING);

			statement = connection.createStatement();

			resultSet = statement.executeQuery(query);
		} catch (ClassNotFoundException cnfe) {

			System.out.println("ClassNotFoundException " + cnfe.getMessage());
		} catch (Exception e) {
			System.out.println("Exception " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				// Close resources.
				if (null != connection)
					connection.close();
				if (null != statement)
					statement.close();
			} catch (SQLException sqlException) {
				// No additional action if close() statements fail.
			}
		}
		return resultSet;
	}
}
