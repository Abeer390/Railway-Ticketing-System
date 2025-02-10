package com.abeer.service.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.abeer.beans.TrainException;
import com.abeer.beans.UserBean;
import com.abeer.constant.ResponseCode;
import com.abeer.constant.UserRole;
import com.abeer.service.UserService;
import com.abeer.utility.DBUtil;

public class UserServiceImpl implements UserService {

	private final String TABLE_NAME;
	String db1Url = "jdbc:mysql://localhost:3306/DB1";
    String db2Url = "jdbc:mysql://localhost:3306/DB2";
    String db3Url = "jdbc:mysql://localhost:3306/DB3";
    String db4Url = "jdbc:mysql://localhost:3306/DB4";
    String db5Url = "jdbc:mysql://localhost:3306/DB5";
    String username = "RESERVATION";
    String password = "MANAGER";

	public UserServiceImpl(UserRole userRole) {
		if (userRole == null) {
			userRole = UserRole.CUSTOMER;
		}
		this.TABLE_NAME = userRole.toString();
	}

	@Override
	public UserBean getUserByEmailId(String customerEmailId) throws TrainException {
		UserBean customer = null;
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE MAILID=?";
		try {
			Connection con = DBUtil.getConnection();
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, customerEmailId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				customer = new UserBean();
				customer.setFName(rs.getString("fname"));
				customer.setLName(rs.getString("lname"));
				customer.setAddr(rs.getString("addr"));
				customer.setMailId(rs.getString("mailid"));
				customer.setPhNo(rs.getLong("phno"));
			} else {
				throw new TrainException(ResponseCode.NO_CONTENT);
			}
			ps.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw new TrainException(e.getMessage());
		}
		return customer;
	}

	@Override
	public List<UserBean> getAllUsers() throws TrainException {
		List<UserBean> customers = null;
		String query = "SELECT * FROM  " + TABLE_NAME;
		try {
			Connection con = DBUtil.getConnection();
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			customers = new ArrayList<UserBean>();
			while (rs.next()) {
				UserBean customer = new UserBean();
				customer.setFName(rs.getString("fname"));
				customer.setLName(rs.getString("lname"));
				customer.setAddr(rs.getString("addr"));
				customer.setMailId(rs.getString("mailid"));
				customer.setPhNo(rs.getLong("phno"));
				customers.add(customer);
			}

			if (customers.isEmpty()) {
				throw new TrainException(ResponseCode.NO_CONTENT);
			}
			ps.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw new TrainException(e.getMessage());
		}
		return customers;
	}

	@Override
	public String updateUser(UserBean customer) {
		String responseCode = ResponseCode.FAILURE.toString();
		String query = "UPDATE  " + TABLE_NAME + " SET FNAME=?,LNAME=?,ADDR=?,PHNO=? WHERE MAILID=?";
		try {
			Connection con = DBUtil.getConnection();
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, customer.getFName());
			ps.setString(2, customer.getLName());
			ps.setString(3, customer.getAddr());
			ps.setLong(4, customer.getPhNo());
			ps.setString(5, customer.getMailId());
			int response = ps.executeUpdate();
			if (response > 0) {
				responseCode = ResponseCode.SUCCESS.toString();
			}
			ps.close();
		} catch (SQLException | TrainException e) {
			responseCode += " : " + e.getMessage();
		}
		return responseCode;
	}

	@Override
	public String deleteUser(UserBean customer) {
		String responseCode = ResponseCode.FAILURE.toString();
		String query = "DELETE FROM " + TABLE_NAME + " WHERE MAILID=?";
		try {
			Connection con = DBUtil.getConnection();
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, customer.getMailId());

			int response = ps.executeUpdate();
			if (response > 0) {
				responseCode = ResponseCode.SUCCESS.toString();
			}
			ps.close();
		} catch (SQLException | TrainException e) {
			responseCode += " : " + e.getMessage();
		}
		return responseCode;
	}

	@Override
	public String registerUser(UserBean customer) {
	    String responseCode = ResponseCode.FAILURE.toString();
	    String query = "INSERT INTO " + TABLE_NAME + " VALUES(?,?,?,?,?,?)";
	    try {
	    	
	        Connection con = DBUtil.getConnection();
	        PreparedStatement ps = con.prepareStatement(query);
	        ps.setString(1, customer.getMailId());
	        ps.setString(2, customer.getPWord());
	        ps.setString(3, customer.getFName());
	        ps.setString(4, customer.getLName());
	        ps.setString(5, customer.getAddr());
	        ps.setLong(6, customer.getPhNo());

	        int rowsAffected = ps.executeUpdate(); 
	        if ("dhaka".equalsIgnoreCase(customer.getAddr())) {
	            // Insert into DB1
	            Connection db1Con = DriverManager.getConnection(db1Url, username, password);
	            PreparedStatement db1Ps = db1Con.prepareStatement(query);
	            db1Ps.setString(1, customer.getMailId());
	            db1Ps.setString(2, customer.getPWord());
	            db1Ps.setString(3, customer.getFName());
	            db1Ps.setString(4, customer.getLName());
	            db1Ps.setString(5, customer.getAddr());
	            db1Ps.setLong(6, customer.getPhNo());
	            int rowsAffectedDb1 = db1Ps.executeUpdate();
	            if (rowsAffectedDb1 > 0) {
	                System.out.println("Data inserted successfully into DB1.");
	            }
	            db1Ps.close();
	            db1Con.close();
	        } else if ("chittagong".equalsIgnoreCase(customer.getAddr())) {
	            // Insert into DB2
	            Connection db2Con = DriverManager.getConnection(db2Url, username, password);
	            PreparedStatement db2Ps = db2Con.prepareStatement(query);
	            db2Ps.setString(1, customer.getMailId());
	            db2Ps.setString(2, customer.getPWord());
	            db2Ps.setString(3, customer.getFName());
	            db2Ps.setString(4, customer.getLName());
	            db2Ps.setString(5, customer.getAddr());
	            db2Ps.setLong(6, customer.getPhNo());
	            int rowsAffectedDb2 = db2Ps.executeUpdate();
	            if (rowsAffectedDb2 > 0) {
	                System.out.println("Data inserted successfully into DB2.");
	            }
	            db2Ps.close();
	            db2Con.close();
	        } else if ("barishal".equalsIgnoreCase(customer.getAddr())) {
	            // Insert into DB3
	            Connection db3Con = DriverManager.getConnection(db3Url, username, password);
	            PreparedStatement db3Ps = db3Con.prepareStatement(query);
	            db3Ps.setString(1, customer.getMailId());
	            db3Ps.setString(2, customer.getPWord());
	            db3Ps.setString(3, customer.getFName());
	            db3Ps.setString(4, customer.getLName());
	            db3Ps.setString(5, customer.getAddr());
	            db3Ps.setLong(6, customer.getPhNo());
	            int rowsAffectedDb3 = db3Ps.executeUpdate();
	            if (rowsAffectedDb3 > 0) {
	                System.out.println("Data inserted successfully into DB3.");
	            }
	            db3Ps.close();
	            db3Con.close();
	        } else if ("khulna".equalsIgnoreCase(customer.getAddr())) {
	            // Insert into DB4
	            Connection db4Con = DriverManager.getConnection(db4Url, username, password);
	            PreparedStatement db4Ps = db4Con.prepareStatement(query);
	            db4Ps.setString(1, customer.getMailId());
	            db4Ps.setString(2, customer.getPWord());
	            db4Ps.setString(3, customer.getFName());
	            db4Ps.setString(4, customer.getLName());
	            db4Ps.setString(5, customer.getAddr());
	            db4Ps.setLong(6, customer.getPhNo());
	            int rowsAffectedDb4 = db4Ps.executeUpdate();
	            if (rowsAffectedDb4 > 0) {
	                System.out.println("Data inserted successfully into DB4.");
	            }
	            db4Ps.close();
	            db4Con.close();
	        }
	        else {
	            // Insert into DB5
	            Connection db5Con = DriverManager.getConnection(db5Url, username, password);
	            PreparedStatement db5Ps = db5Con.prepareStatement(query);
	            db5Ps.setString(1, customer.getMailId());
	            db5Ps.setString(2, customer.getPWord());
	            db5Ps.setString(3, customer.getFName());
	            db5Ps.setString(4, customer.getLName());
	            db5Ps.setString(5, customer.getAddr());
	            db5Ps.setLong(6, customer.getPhNo());
	            int rowsAffectedDb5 = db5Ps.executeUpdate();
	            if (rowsAffectedDb5 > 0) {
	                System.out.println("Data inserted successfully into DB5.");
	            }
	            db5Ps.close();
	            db5Con.close();
	        }
	        if (rowsAffected > 0) {
	            responseCode = ResponseCode.SUCCESS.toString();
	        }
	        
	        ps.close();
	    } catch (SQLException | TrainException e) {
	        if (e.getMessage().toUpperCase().contains("ORA-00001")) {
	            responseCode += " : " + "User With Id: " + customer.getMailId() + " is already registered ";
	        } else {
	            responseCode += " : " + e.getMessage();
	        }
	    }
	    return responseCode;
	}

	@Override
	public UserBean loginUser(String username, String password) throws TrainException {
		UserBean customer = null;
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE MAILID=? AND PWORD=?";
		try {
			Connection con = DBUtil.getConnection();
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, username);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				customer = new UserBean();
				customer.setFName(rs.getString("fname"));
				customer.setLName(rs.getString("lname"));
				customer.setAddr(rs.getString("addr"));
				customer.setMailId(rs.getString("mailid"));
				customer.setPhNo(rs.getLong("phno"));
				customer.setPWord(rs.getString("pword"));
			} else {
				throw new TrainException(ResponseCode.UNAUTHORIZED);
			}
			ps.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw new TrainException(e.getMessage());
		}
		return customer;
	}

}
