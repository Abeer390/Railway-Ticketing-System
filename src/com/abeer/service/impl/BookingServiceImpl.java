package com.abeer.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.abeer.beans.HistoryBean;
import com.abeer.beans.TrainException;
import com.abeer.constant.ResponseCode;
import com.abeer.service.BookingService;
import com.abeer.utility.DBUtil;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class BookingServiceImpl implements BookingService {

	@Override
	public List<HistoryBean> getAllBookingsByCustomerId(String customerEmailId) throws TrainException {
		List<HistoryBean> transactions = null;
		String query = "SELECT * FROM HISTORY WHERE MAILID=?";
		try {
			Connection con = DBUtil.getConnection();
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, customerEmailId);
			ResultSet rs = ps.executeQuery();
			transactions = new ArrayList<HistoryBean>();
			while (rs.next()) {
				HistoryBean transaction = new HistoryBean();
				transaction.setTransId(rs.getString("transid"));
				transaction.setFrom_stn(rs.getString("from_stn"));
				transaction.setTo_stn(rs.getString("to_stn"));
				transaction.setDate(rs.getString("date"));
				transaction.setMailId(rs.getString("mailid"));
				transaction.setSeats(rs.getInt("seats"));
				transaction.setAmount(rs.getDouble("amount"));
				transaction.setTr_no(rs.getString("tr_no"));
				transactions.add(transaction);
			}

			ps.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw new TrainException(e.getMessage());
		}
		return transactions;
	}

	@Override
	public HistoryBean createHistory(HistoryBean details) throws TrainException {
		HistoryBean history = null;
		String query = "INSERT INTO HISTORY VALUES(?,?,?,?,?,?,?,?)";
		try {
			Connection con = DBUtil.getConnection();
			PreparedStatement ps = con.prepareStatement(query);
			String transactionId = UUID.randomUUID().toString();
			ps.setString(1, transactionId);
			ps.setString(2, details.getMailId());
			ps.setString(3, details.getTr_no());
			ps.setString(4, details.getDate());
			ps.setString(5, details.getFrom_stn());
			ps.setString(6, details.getTo_stn());
			ps.setLong(7, details.getSeats());
			ps.setDouble(8, details.getAmount());
			int response = ps.executeUpdate();
			if (response > 0) {
				history = (HistoryBean) details;
				history.setTransId(transactionId);
				String to = details.getMailId();
			    String subject = "Booking Confirmation";
			       String body = "Dear " + details.getMailId() + ",\n\n" +
			                      "Your booking has been confirmed. Details:\n" +
			                      "Transaction ID: " + transactionId + "\n" +
			                      "Date: " + details.getDate() + "\n" +
			                      "From Stations: " + details.getFrom_stn() + "\n" +
			                      "To Stations: " + details.getTo_stn() + "\n" +
			                      "Total Amount: " + details.getAmount() + "\n" +
			                      "Thank you for booking with us!";

			        sendEmail(to, subject, body);
				
			} else {
				throw new TrainException(ResponseCode.INTERNAL_SERVER_ERROR);
			}
			ps.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw new TrainException(e.getMessage());
		}
		return history;
	}
	 private void sendEmail(String to, String subject, String body) {
	        
	        Properties properties = new Properties();
	        properties.put("mail.smtp.host", "smtp.gmail.com"); 
	        properties.put("mail.smtp.port", "587");
	        properties.put("mail.smtp.auth", "true");
	        properties.put("mail.smtp.starttls.enable", "true");

	        String from = "bubtus68@gmail.com";
	        String password = "uaqpluzaoezlospf";

	        Session session = Session.getInstance(properties, new Authenticator() {
	            protected PasswordAuthentication getPasswordAuthentication() {
	                return new PasswordAuthentication(from, password);
	            }
	        });

	        try {
	            Message message = new MimeMessage(session);

	            message.setFrom(new InternetAddress(from));

	            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

	            message.setSubject(subject);

	            message.setText(body);

	            Transport.send(message);

	            System.out.println("Email sent successfully to " + to);

	        } catch (MessagingException e) {
	            e.printStackTrace();
	            System.out.println("Error sending email.");
	        }
	    }

}
