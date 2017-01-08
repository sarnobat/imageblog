package com.rohidekar.imageblog.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

public class CronJob implements Job {

	public static void start() {
		try {
			SchedulerFactory sf = new StdSchedulerFactory();
			Scheduler scheduler = sf.getScheduler();
			// define the job and tie it to our HelloJob class
			JobDetail job = new JobDetail("job2", "group1", CronJob.class);
			// compute a time that is on the next round minute

			// Trigger the job to run on the next round minute
			try {
				CronTrigger trigger = new CronTrigger("trigger2", "group1", "job2", "group1",
						"0/30 * * * * ?");
				// Tell quartz to schedule the job using our trigger
				scheduler.scheduleJob(job, trigger);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			// and start it off
			scheduler.start();
		} catch (SchedulerException se) {
			se.printStackTrace();
		}
	}

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("Job is executing");
		long startTime = System.currentTimeMillis();
		try {
			readFromDB();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("CronJob.java::execute() - Took " + (endTime - startTime) / 1000
				+ "  seconds");
	}

	public static void readFromDB() throws SQLException, ClassNotFoundException {

		// Class.forName("com.mysql.jdbc.ConnectionImpl");
		QueryRunner runner = getQueryRunner();
		// System.out.println("-------- MySQL JDBC Connection Testing ------------");
		Object[] result = runner.query("SELECT * FROM images", resultProcessor);
		setImages(result);
		runner.getDataSource().getConnection().close();
	}

	public static QueryRunner getQueryRunner() {
		BasicDataSource ds = new BasicDataSource();
		// Class.forName("com.mysql.jdbc.Driver");
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		ds.setUrl("jdbc:mysql://sarnobat2002.db.6450211.hostedresource.com:3306/sarnobat2002");
		ds.setUsername("sarnobat2002");
		ds.setPassword("aize2FEN@");
		return new QueryRunner(ds);
	}

	private static ResultSetHandler<Object[]> resultProcessor = new ResultSetHandler<Object[]>() {
		public Object[] handle(ResultSet rs) throws SQLException {
			if (!rs.next()) {
				System.out.println("CronJob::handle() - no results");
				return null;
			}
			int cols = rs.getMetaData().getColumnCount();
			Object[] result = new Object[cols];
			System.out.println("CronJob::handle() - column count = " + cols);
			for (int i = 0; i < cols; i++) {
				Object object = rs.getObject(i + 1);
				result[i] = object;
			}
			System.out.println("CronJob::handle() - no results");
			return result;
		}
	};
	private static final String WEB_PREFIX = "http://bollywoodbutts.s3.amazonaws.com/";

	private static Collection<String> paths = new LinkedHashSet<String>();

	private static void setImages(Object[] result) throws SQLException {
		System.out.println("CronJob::setImages() - count: " + result.length);
		getQueryRunner().query("select * from images", new ResultSetHandler<Object>() {
			public Object handle(ResultSet rs) throws SQLException {
				System.out.println("setImages() - SQL WORKS");
				Collection<String> latestPaths = new LinkedHashSet<String>();
				while (rs.next()) {
					latestPaths.add(WEB_PREFIX + rs.getString("filename"));
				}
				paths = latestPaths;
				System.out.println("setImages() - count: " + latestPaths.size());
				return paths;
			}
		});
		getQueryRunner().getDataSource().getConnection().close();
	}

	public static List<String> getImages() {
		return new LinkedList(paths);
	}
}