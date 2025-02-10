package com.abeer.service;

import java.util.List;

import com.abeer.beans.HistoryBean;
import com.abeer.beans.TrainException;

public interface BookingService {

	public List<HistoryBean> getAllBookingsByCustomerId(String customerEmailId) throws TrainException;

	public HistoryBean createHistory(HistoryBean bookingDetails) throws TrainException;

}
