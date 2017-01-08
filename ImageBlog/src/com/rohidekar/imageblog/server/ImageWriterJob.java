package com.rohidekar.imageblog.server;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbutils.QueryRunner;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.google.common.base.Preconditions;

/**
 * Gets the AWS information so that we can populate the database and avoid
 * billing.
 */
public class ImageWriterJob implements Job {

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("Writer is executing");

	}

	public static Map<String, Map<String, Object>> awsBucketList() {
		Map<String, Map<String, Object>> files = new LinkedHashMap<String, Map<String, Object>>();
		AWSCredentials myCredentials = new BasicAWSCredentials("03PKJYQF3BWHK63NN5R2",
				"Ni+BfF+ehW+tCev8kR3CVJr7EuWWBMO8xmlL8fcP");
		AmazonS3Client client = new AmazonS3Client(myCredentials);
		ObjectListing listing = client.listObjects("bollywoodbutts");
		for (S3ObjectSummary e : listing.getObjectSummaries()) {
			if (e.getKey().startsWith("~VersionArchive") || e.getKey().startsWith("._.")
					|| e.getKey().startsWith(".VolumeIcon")) {
				continue;
			}
			Map<String, Object> row = new HashMap<String, Object>();
			row.put("filename", e.getKey());
			row.put("md5", e.getETag());
			row.put("date_Added", new Date(System.currentTimeMillis()));
			files.put(e.getETag(), row);
			System.out.println(e.getKey());
		}
		return files;
	}

	public static void main(String[] args) {
		awsBucketList();
	}

	public static void start() throws SchedulerException, SQLException, ParseException {

		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler scheduler = sf.getScheduler();
		JobDetail job = new JobDetail("job3", "group2", ImageWriterJob.class);
		CronTrigger trigger = new CronTrigger("trigger2", "group2", "job3", "group2",
				"0/30 * * * * ?");
		scheduler.scheduleJob(job, trigger);
		// I don't think we need this
		// scheduler.start();
		// TODO: Move this to job (after you've got the right scheduling - not
		// every 30 seconds, you'll get billed too much)
		writeToDb();

	}

	public static void writeToDb() throws SQLException {
		Map<String, Map<String, Object>> files = awsBucketList();
		// Delete what's in the DB
		QueryRunner r = CronJob.getQueryRunner();

		r.update("delete from images");
		System.out.println("ImageWriterJob::start() - Old images deleted");

		// Add what we got from AWS
		System.out.println("ImageWriterJob::start() - about to populate array, " + files.size());
		Object[][] o2 = new Object[files.size()][3];
//		ArrayList<Object[]> o3 = new ArrayList<Object[]>(); 
		System.out.println("ImageWriterJob::start() - 1");
		int i = 0;
		System.out.println("ImageWriterJob::start() - 2");
		for (Map<String, Object> row : files.values()) {
			System.out.println("ImageWriterJob::start() - 3");
			// ArrayList<Object> b = new ArrayList<Object>();

			 Object[] o = { Preconditions.checkNotNull(row.get("date_Added")), row.get("filename"),
			 row.get("md5") };
			o2[i][0] = Preconditions.checkNotNull(row.get("date_Added"));
			System.out.println("ImageWriterJob::start() - 4");
			o2[i][1] = row.get("filename");
			System.out.println("ImageWriterJob::start() - 5");
			o2[i][2] = row.get("md5");
//			 Arrays.
//			System.out.println("ImageWriterJob::start() - 6");
//			o3.add(o);
			System.out.println("ImageWriterJob::start() - 7");
			i++;
			System.out.println("ImageWriterJob::start() - array row populated");
		}
		System.out.println("ImageWriterJob::start() - entire array populated");
//		Object[] array = o3.toArray();
		System.out.println("ImageWriterJob::start() - 8");
//		Object[][] array2 = (Object[][]) array;
		r.batch("insert into images (date_added,filename,md5) values (?,?,?)", o2);
		System.out.println("ImageWriterJob::start() - batch insert done (SQL WORKS)");

		r.getDataSource().getConnection().close();
	}
}
