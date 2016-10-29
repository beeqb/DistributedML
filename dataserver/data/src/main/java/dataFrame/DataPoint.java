package dataFrame;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * 
 * @author Zhaohong Jin
 * The Data Source is Yahoo! Finance
 *
 */
public class DataPoint {
	
	//features
	public String stockName;
	public String ticker;
	public int date; //for example, 1999/11/18 = 19991118
	public double open; //opening price
	public double high; //highest price of the day
	public double low; //lowest price of the day
	public double close; //closing price
	public int volume; //total volume in dollars
	
	public LinkedList<DataPoint> fiveDayList;
	public double movingAvaerageFiveDay; //moving average in 5 days
	
	public LinkedList<DataPoint> tenDayList;
	public double movingAverageTenDay; //moving average in 10 days
	
	public double exponentialMovingAverage; // EMA (t) = EMA (t-1) + alpha * (Price (t) - EMA (t-1)) Where, alpha = 2/ (N+1), Thus, for N=9, alpha = 0.20
	
	public double rateOfChangeFiveDay; // The ratio of the current price to the price 5 quotes earlier
	public double rateOfChangeTenDay; // The ratio of the current price to the price 10 quotes earlier
	public double spyClose; //closing price of S&P500 that day
	public double djiClose; //closing price of Dow Jones that day
	public double ixicClose; //closing price of Nasdaq that day
	public double tnxClose; //closing price of 10-yr bond that day
	public double vixClose; //closing volatility of S&P500 that day
	
	public final double alpha = 0.20; //hyperparameter
	
	public int label; // 1 if tomorrow's closing price is higher, 0 otherwise
	
	
	
	/**
	 * update the current five day and ten day moving average up to today
	 * if the list contains less than five data, we still calculate the average 
	 */
	public void setMovingAverage() {
		if (fiveDayList != null) {
			int size = fiveDayList.size();
			int runningSum = 0;
			if (size != 0) {
				for (DataPoint data : fiveDayList) {
					runningSum += data.close;
				}
				movingAvaerageFiveDay = (double) runningSum / (double) size;		
			}
		}
		
		if (tenDayList != null) {
			int size = tenDayList.size();
			int runningSum = 0;
			if (size != 0) {
				for (DataPoint data : tenDayList) {
					runningSum += data.close;
				}
				movingAverageTenDay = (double) runningSum / (double) size;		
			}
		}
	}
	
	
	
	/**
	 * 
	 * @param EMA (t-1)
	 * EMA (t) = EMA (t-1) + alpha * (Price (t) - EMA (t-1))
	 */
	public void setExponentialMovingAverage(double prev) {
		this.exponentialMovingAverage = prev + alpha * (this.close - prev);
	}
	
	
	/**
	 * update the ratio of the current price to the price 5 or 10 quotes earlier
	 * if the five day or ten day data is not available (less than 5 or 10), use the oldest available data
	 */
	public void setRateOfChange() {
		if (fiveDayList != null) {
			DataPoint last = fiveDayList.peekFirst();
			rateOfChangeFiveDay = close / last.close;
		}
		
		if (tenDayList != null) {
			DataPoint last = tenDayList.peekFirst();
			rateOfChangeTenDay = close / last.close;
		}
		
	}
	
	/**
	 * 
	 * @param data
	 * insert the data into the five day or ten day list
	 * if the five day or ten day list is full, drop the oldest data
	 */
	public void add(DataPoint data) {
		if (fiveDayList == null) {
			fiveDayList = new LinkedList<DataPoint>();
			fiveDayList.add(data);
		} else {
			if (fiveDayList.size() == 5) {
				fiveDayList.remove(); //remove from the head
				fiveDayList.add(data);
			}
		}
		
		if (tenDayList == null) {
			tenDayList = new LinkedList<DataPoint>();
			tenDayList.add(data);
		} else {
			if (tenDayList.size() == 10) {
				tenDayList.remove(); //remove from the head
				tenDayList.add(data);
			}
		}
		
		
	}
	
	
	
}