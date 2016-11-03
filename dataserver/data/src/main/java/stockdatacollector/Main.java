package stockdatacollector;

import java.io.FileNotFoundException;
import java.io.IOException;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import dataFrame.DataPoint;
import dataFrame.Stock;

/**
 * 
 * @author zhjin
 *
 */
public class Main {
	
	public static Stock stock;
	
	public static String hdfs = "hdfs://localhost:9000";
	public static String root = "hdfs://localhost:9000/user/zhjin/mlinput/data";
	public static String stockName = "Microsoft";
	public static String ticker = "MSFT";
	public static String[] indexList = {"spy", "dji", "ixic", "tnx", "vix"};

	
	public static void main(String[] args) {
		loadStockData();
		loadIndexData();
		stock.setParameters();
		writeToFile("/msft_combined.csv");
	}
	
	
	/**
	 * This file I/O supports only HDFS, not the regular local file system
	 */
	public static void loadStockData() {
		stock = new Stock(stockName, ticker);
		
		Path path = new Path(root + "/msft.csv"); //for HDFS use 
		try {
			Configuration conf = new Configuration();
			conf.set("fs.defaultFS", "hdfs://localhost:9000");
			// see this post on stack overflow http://stackoverflow.com/questions/17265002/hadoop-no-filesystem-for-scheme-file
		    conf.set("fs.hdfs.impl", 
		            org.apache.hadoop.hdfs.DistributedFileSystem.class.getName()
		        );
		    conf.set("fs.file.impl",
		            org.apache.hadoop.fs.LocalFileSystem.class.getName()
		        );
			FileSystem fs = FileSystem.get(conf);
			BufferedReader c = new BufferedReader(new InputStreamReader(fs.open(path)));
			// skip the title line and the first line(because it doesn't have a label
			c.readLine();
			c.readLine();
			String line = c.readLine();
			while (line != null) {
				stock.addStockEntry(line);
				line = c.readLine();
			}
			c.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * This file I/O supports only HDFS, not the regular local file system
	 */
	public static void loadIndexData() {
		for (String index : indexList) {
			
			try {
				
				Path path = new Path(root + "/" + index + ".csv");
				Configuration conf = new Configuration();
				conf.set("fs.defaultFS", "hdfs://localhost:9000");
				// see this post on stack overflow http://stackoverflow.com/questions/17265002/hadoop-no-filesystem-for-scheme-file
			    conf.set("fs.hdfs.impl", 
			            org.apache.hadoop.hdfs.DistributedFileSystem.class.getName()
			        );
			    conf.set("fs.file.impl",
			            org.apache.hadoop.fs.LocalFileSystem.class.getName()
			        );
				FileSystem fs = FileSystem.get(conf);
				BufferedReader c = new BufferedReader(new InputStreamReader(fs.open(path)));
				
				String line = c.readLine();
				
				while (line != null) {
					stock.addIndexEntry(line, index);
					line = c.readLine();
				}
				
				c.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void writeToFile(String file) {
		try {
			Path path = new Path(root + file);
			Configuration conf = new Configuration();
			conf.set("fs.defaultFS", "hdfs://localhost:9000");
			// see this post on stack overflow http://stackoverflow.com/questions/17265002/hadoop-no-filesystem-for-scheme-file
		    conf.set("fs.hdfs.impl", 
		            org.apache.hadoop.hdfs.DistributedFileSystem.class.getName()
		        );
		    conf.set("fs.file.impl",
		            org.apache.hadoop.fs.LocalFileSystem.class.getName()
		        );
			FileSystem fs = FileSystem.get(conf);
			BufferedWriter writer =new BufferedWriter(new OutputStreamWriter(fs.create(path,true)));
			
			String header = "Date,Open,high,low,close,volume,movingAverageFiveDay,"
					+ "movingAverageTenDay,exponentialMovingAverage,rateOfChangeFiveDay,"
					+ "rateOfChangeTenDay,spyClose,spyMovingAverageFiveDay,"
					+ "spyMovingAverageTenDay,spyRateOfChangeFiveDay,spyRateOfChangeTenDay,"
					+ "djiClose,djiMovingAverageFiveDay,djiMovingAverageTenDay,"
					+ "djiRateOfChangeFiveDay,djiRateOfChangeTenDay,ixicClose,"
					+ "ixicMovingAverageFiveDay,ixicMovingAverageTenDay,ixicRateOfChangeFiveDay,"
					+ "ixicRateOfChangeTenDay,tnxClose,tnxMovingAverageFiveDay,tnxMovingAverageTenDay,"
					+ "tnxRateOfChangeFiveDay,tnxRateOfChangeTenDay,vixClose,"
					+ "vixMovingAverageFiveDay,vixMovingAverageTenDay,vixRateOfChangeFiveDay,"
					+ "vixRateOfChangeTenDay,label \n";
			writer.write(header);
			for (String date : stock.dateList) {
				DataPoint data = stock.dataMap.get(date);
				String line = data.date + "," + data.open + "," + data.high + "," + data.low + "," + data.close + "," 
				+ data.volume + "," + data.movingAverageFiveDay + "," + data.movingAverageTenDay + "," 
				+ data.exponentialMovingAverage + "," + data.rateOfChangeFiveDay + "," + data.rateOfChangeTenDay + "," 
				+ data.spyClose + "," + data.spyMovingAverageFiveDay + "," + data.spyMovingAverageTenDay + "," 
				+ data.spyRateOfChangeFiveDay + "," + data.spyRateOfChangeTenDay + "," + data.djiClose + "," 
				+ data.djiMovingAverageFiveDay + "," + data.djiMovingAverageTenDay + "," + data.djiRateOfChangeFiveDay + "," 
				+ data.djiRateOfChangeTenDay + "," + data.ixicClose + "," + data.ixicMovingAverageFiveDay + "," 
				+ data.ixicMovingAverageTenDay + "," + data.ixicRateOfChangeFiveDay + "," + data.ixicRateOfChangeTenDay + "," 
				+ data.tnxClose + "," + data.tnxMovingAverageFiveDay + "," + data.tnxMovingAverageTenDay + "," 
				+ data.tnxRateOfChangeFiveDay + "," + data.tnxRateOfChangeTenDay + "," + data.vixClose + "," 
				+ data.vixMovingAverageFiveDay + "," + data.vixMovingAverageTenDay + "," + data.vixRateOfChangeFiveDay + "," 
				+ data.rateOfChangeTenDay + "," + data.label + " \n";
				
				writer.write(line);
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
