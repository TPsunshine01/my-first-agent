package azaniam.co.za.my_first_agent;

import org.springframework.stereotype.Component;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class SowetoTools {

    private final HttpClient http = HttpClient.newHttpClient();
    private final Random random = new Random();

    public String getWeatherInSoweto() {
        try {
            String url = "https://api.open-meteo.com/v1/forecast?latitude=-26.267&longitude=27.858&current=temperature_2m,relative_humidity_2m,precipitation,weather_code,wind_speed_10m&timezone=Africa%2FJohannesburg";
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
            String body = res.body();

            // FIX: Only look inside the "current" block, not the units block
            int currentStart = body.indexOf("\"current\":");
            String currentBlock = body.substring(currentStart);

            String temp = extract(currentBlock, "\"temperature_2m\":");
            String humidity = extract(currentBlock, "\"relative_humidity_2m\":");
            String rain = extract(currentBlock, "\"precipitation\":");
            String wind = extract(currentBlock, "\"wind_speed_10m\":");
            String code = extract(currentBlock, "\"weather_code\":");

            String desc = "kasi weather";
            if (code.equals("0")) {
                desc = "clear sky, lekker sunshine";
            } else if (code.equals("1") || code.equals("2") || code.equals("3")) {
                desc = "partly cloudy, still chillas weather";
            } else if (code.equals("61") || code.equals("63") || code.equals("65")) {
                desc = "raining, cats and dogs";
            } else if (code.equals("80") || code.equals("81") || code.equals("82")) {
                desc = "rain showers";
            } else if (code.equals("95") || code.equals("96") || code.equals("99")) {
                desc = "thunderstorm, stay inside my guy";
            }

            return "REAL WEATHER IN SOWETO RIGHT NOW: " + temp + " degrees, " + desc + ", humidity " + humidity + "%, rain " + rain + "mm, wind " + wind + " km/h. Use THESE exact numbers.";

        } catch (Exception e) {
            return "Weather tool is offline right now, but it is a typical warm November evening in Soweto, around 22 degrees. Tell Thabo to check again now now.";
        }
    }

    public String getTimeInSoweto() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Africa/Johannesburg"));
        String formatted = now.format(DateTimeFormatter.ofPattern("HH:mm, EEEE dd MMMM yyyy"));
        return "REAL TIME IN SOWETO RIGHT NOW: " + formatted + ". Use this exact time.";
    }

    public String recommendAmapiano(String userMessage) {
        List<String> tracks = Arrays.asList(
                "Kabza De Small and DJ Maphorisa - Asibe Happy ft. Ami Faku",
                "Kelvin Momo - Amukelani",
                "Young Stunna - Adiwele ft. Kabza De Small",
                "MFR Souls - Amanikiniki ft. Kamo Mphela",
                "Daliwonga - Abo Mvelo ft. Mellow and Sleazy",
                "Kabza De Small - Khusela ft. Msaki",
                "DJ Maphorisa and Kabza De Small - Kbuye"
        );
        String pick = tracks.get(random.nextInt(tracks.size()));
        return "REAL RECOMMENDATION: Play this banger: " + pick + ". Hype it up like a DJ.";
    }

    private String extract(String json, String key) {
        try {
            int i = json.indexOf(key) + key.length();
            int end = json.indexOf(",", i);
            if (end == -1) {
                end = json.indexOf("}", i);
            }
            return json.substring(i, end).trim();
        } catch (Exception e) {
            return "?";
        }
    }
}