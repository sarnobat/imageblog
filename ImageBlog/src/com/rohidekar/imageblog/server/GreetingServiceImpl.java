package com.rohidekar.imageblog.server;

import java.io.PrintStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import org.quartz.SchedulerException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.rohidekar.imageblog.client.GreetingService;
import com.rohidekar.imageblog.shared.FieldVerifier;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {

	public GreetingServiceImpl() throws SchedulerException, SQLException, ParseException, ClassNotFoundException {
		//CronJob.start();
		//ImageWriterJob.start();
//		System.setOut(new PrintStream(new LoggingOutputStream(logger,Level.INFO),true));
//		System.setErr(new PrintStream(new LoggingOutputStream(logger,Level.ERROR),true));
		ImageWriterJob.writeToDb();
		CronJob.readFromDB();
		System.out.println("Ready to serve");
	}

	public String greetServer(String input) throws IllegalArgumentException {
		// Verify that the input is valid.
		if (!FieldVerifier.isValidName(input)) {
			// If the input is not valid, throw an IllegalArgumentException back
			// to the client.
			throw new IllegalArgumentException("Name must be at least 4 characters long");
		}
		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");
		// Escape data from the client to avoid cross-site script
		// vulnerabilities.
		input = escapeHtml(input);
		userAgent = escapeHtml(userAgent);
		return "Hello, " + input + "!<br><br>I am running " + serverInfo
				+ ".<br><br>It looks like you are using:<br>" + userAgent;
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html
	 *            the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}

	// Make this the first image (for good user experience)
	public String getImage() {
		return "foobar";
	}

//	private static final String[] files = { "091318398394.jpg", "10.jpg", "11-7121ba[1].jpg",
//			"16.jpg", "2-87ba[1].jpg", "2218529_f496.jpg", "2qipfv8.jpg", "33.jpg",
//			"4079064559947215a18co-001.jpg", "48_011.jpg", "4j94pma5p3x9pezc86r.jpg",
//			"884_large.jpg", "Anaganaga-Oka-Dheerudu-movie-stills-3.jpg",
//			"Bollywood Actresses Backless Wallpaper3.jpg", "DeekshaSeth01.jpg",
//			"Deepika Padukone (44) (1).jpg", "Deepika-Padukone-backless-dress4.jpg",
//			"Deepika_Padukone_010111_55 (1)-001.jpg",
//			"Hot Young Sexy Paki Model Showing her Sexy Butt.jpg", "KatrinaKaif3.jpg",
//			"Katrina_Kaif_Hot_10-001.jpg", "LV35.jpg", "PDVD_971.jpg", "PDVD_9711.jpg",
//			"Shriya Saran butt (1).jpg",
//			"actress_hamsanandini_designersareeimages.blogspot.com_1119 (1).jpg",
//			"amrita rao ass hot.jpg", "anushka9-1.jpg", "asin32.jpg",
//			"blue-saree-black-blouse-front-back.jpg", "deepika padukone ass.jpg",
//			"gold-saree-with-designer-blouse-front-back.jpg",
//			"hot-sexy-models-in-saree-photos_actressinsareephotos.blogspot.com_6.jpg",
//			"illeana ass pic.jpg", "jm1qm2ii50v9rpf8i6dc.jpg", "kalamandir_saree (1).jpg",
//			"lisa131sy.jpg", "madhurima3.jpg", "nau4a.jpg",
//			"net-saree-blouse-sareedreams-com-1.jpg",
//			"priyanka-bollyupdatescom-iifa-awards-07.jpg", "riya28yi.jpg", "sdfw8.jpg",
//			"shriya_89_12242009110341321.jpg", "sonal-chauhan2.jpg_595.jpg",
//			"sonal-chauhan2.jpg_5951.jpg", "sonal19.jpg", "tamanna butt.jpg",
//			"tamannabutt (1)-79ef6e4ef7928fb51ff28a839f9171aa.jpg", "urmila167.jpg", };
//	
	public List<String> getImages() {
		List<String> paths = CronJob.getImages();
		System.out.println("Page requested. Return image count: " + paths.size());
		return paths;
	}
}
