package ca.ubc.cs304.database;

import ca.ubc.cs304.model.BranchModel;
import ca.ubc.cs304.model.UserModel;

import java.sql.*;
import java.util.ArrayList;

/**
 * This class handles all database related transactions
 */
public class DatabaseConnectionHandler {
	//private static final String ORACLE_URL = "jdbc:oracle:thin:@dbhost.students.cs.ubc.ca:1522:stu";
	private static final String ORACLE_URL = "jdbc:oracle:thin:@localhost:1522:stu";
	private static final String EXCEPTION_TAG = "[EXCEPTION]";
	private static final String WARNING_TAG = "[WARNING]";
	
	private Connection connection = null;
	
	public DatabaseConnectionHandler() {
		try {
			// Load the Oracle JDBC driver
			// Note that the path could change for new drivers
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
	}
	
	public void close() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
	}

	public void deleteBranch(BranchModel branchModel) {
		try {
			PreparedStatement ps = connection.prepareStatement("DELETE FROM branch WHERE location = ? AND city = ?");
			ps.setString(1, branchModel.getLocation());
			ps.setString(2, branchModel.getCity());
			
			int rowCount = ps.executeUpdate();
			if (rowCount == 0) {
				System.out.println(WARNING_TAG + " Branch " + branchModel.getLocation() + " in " + branchModel.getCity() + " does not exist!");
			}
			
			connection.commit();
	
			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
	}
	
	public void insertBranch(BranchModel model) {
		try {
			PreparedStatement ps = connection.prepareStatement("INSERT INTO branch VALUES (?,?)");
			ps.setString(1, model.getLocation());
			ps.setString(2, model.getCity());

			ps.executeUpdate();
			connection.commit();

			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
	}
	
	public BranchModel[] getBranchInfo() {
		ArrayList<BranchModel> result = new ArrayList<BranchModel>();
		
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM branch");
		
//    		// get info on ResultSet
//    		ResultSetMetaData rsmd = rs.getMetaData();
//
//    		System.out.println(" ");
//
//    		// display column names;
//    		for (int i = 0; i < rsmd.getColumnCount(); i++) {
//    			// get column name and print it
//    			System.out.printf("%-15s", rsmd.getColumnName(i + 1));
//    		}
			
			while(rs.next()) {
				BranchModel model = new BranchModel(rs.getString("location"),
													rs.getString("city"));
				result.add(model);
			}

			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}	
		
		return result.toArray(new BranchModel[result.size()]);
	}


    //View the number of available vehicles for a specific car type, location, and time interval.
//	public ArrayList<VehicleModel> getAvailableVehicle(String type, String location) {
//	    ArrayList<VehicleModel> vehicleModels = new ArrayList<>();
//	    try {
//	       // PreparedStatement ps = connection.prepareStatement("SELECT count(*) FROM Vehicle v)
//            // TODO: figure out what does "time interval" refer to, also not sure what details to show when # of available cars are clicked
//        }
//	    catch (SQLException e) {
//            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
//            rollbackConnection();
//        }
//	    return vehicleModels;
//    }

    public void addCustomerDetails(UserModel userModel) {
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO Customer VALUES (?,?,?,?)");
            ps.setString(1, Integer.toString(userModel.getDlicense()));
            ps.setString(2, userModel.getName());
            ps.setString(3, userModel.getPhoneNum());
            ps.setString(4, userModel.getAddress());

            ps.executeUpdate();
            connection.commit();

            ps.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
    }

    public UserModel[] getCustomerDetails() {
	    ArrayList<UserModel> customerDetails = new ArrayList();
	    try {
	        Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Customer");

            while(rs.next()) {
                UserModel userModel = new UserModel(rs.getInt("dlicense"), rs.getString("name"), rs.getString("phoneNumber"),
                        rs.getString("address"));
                        customerDetails.add(userModel);
                    }

                    rs.close();
                    stmt.close();
                } catch (SQLException e) {
                    System.out.println(EXCEPTION_TAG + " " + e.getMessage());
                }
            return customerDetails.toArray(new UserModel[customerDetails.size()]);
        }

	
//	public void updateBranch(String branch_location, String branch_city) {
//		try {
//		  PreparedStatement ps = connection.prepareStatement("UPDATE branch SET location = ? WHERE city = ?");
//		  ps.setString(1, branch_location);
//		  ps.setString(2, branch_city);
//
//		  int rowCount = ps.executeUpdate();
//		  if (rowCount == 0) {
//		      System.out.println(WARNING_TAG + " Branch " + branch_location + " does not exist!");
//		  }
//
//		  connection.commit();
//
//		  ps.close();
//		} catch (SQLException e) {
//			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
//			rollbackConnection();
//		}
//	}
	
	public boolean login(String username, String password) {
		try {
			if (connection != null) {
				connection.close();
			}
	
			connection = DriverManager.getConnection(ORACLE_URL, username, password);
			connection.setAutoCommit(false);
	
			System.out.println("\nConnected to Oracle!");
			return true;
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			return false;
		}
	}

	private void rollbackConnection() {
		try  {
			connection.rollback();	
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
	}
}
