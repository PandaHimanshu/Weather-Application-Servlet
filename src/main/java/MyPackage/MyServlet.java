package MyPackage;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;
import javax.net.ssl.HttpsURLConnection;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MyServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// API Key
		String apiKey = "11b008792d2c0668fc5d69e0d1f62fef";

		// Get the city from the form input
		String city = request.getParameter("city");

		// Create the URL for the OpenWeatherMap API request
		String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;

		// API Integration
		URL url = new URL(apiUrl);
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setRequestMethod("GET");

		// Data Read from network
		InputStream inputStream = connection.getInputStream();
		InputStreamReader reader = new InputStreamReader(inputStream);

		// want to store in string
		StringBuilder responseContent = new StringBuilder();

		// for input from reader , will create scanner class
		Scanner scanner = new Scanner(reader);

		while (scanner.hasNext()) {
			responseContent.append(scanner.nextLine());
		}
		scanner.close();
//		System.out.println(responseContent);

		// Parse the JSON response to extract temperature, date, and humidity
		Gson gson = new Gson();
		JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);

		// Date & Time
		long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;
		String date = new Date(dateTimestamp).toString();

		// Temperature
		double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
		int temperatureCelsius = (int) (temperatureKelvin - 273.15);

		// Humidity
		int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();

		// Wind Speed
		double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();

		// Weather Condition
		String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main")
				.getAsString();

		// Set the data as request attributes (for sending to the jsp page)
		request.setAttribute("date", date);
		request.setAttribute("city", city);
		request.setAttribute("temperature", temperatureCelsius);
		request.setAttribute("weatherCondition", weatherCondition);
		request.setAttribute("humidity", humidity);
		request.setAttribute("windSpeed", windSpeed);
		request.setAttribute("weatherData", responseContent.toString());

		connection.disconnect();

		request.getRequestDispatcher("index.jsp").forward(request, response);
	}

}
