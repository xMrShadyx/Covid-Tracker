package bg.hhyusein.covidtracker.services;
/*
 * @created 28/02/2022 - 7:57 PM
 * @project covid-tracker
 * @author xMrShadyx (Hyusein Hyusein)
 */

import bg.hhyusein.covidtracker.models.DeathStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoronaVirusDeathService {

    // Data fetched from: https://github.com/CSSEGISandData/COVID-19/tree/master/csse_covid_19_data
    // Original author: CSSEGISandData.

    public static String DEATH_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv";

    private List<DeathStats> AllDeathStats = new ArrayList<>();

    public List<DeathStats> getDeathStats() {
        return AllDeathStats;
    }

    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fetchDeathData() throws IOException, InterruptedException {
        List<DeathStats> newDeathStats = new ArrayList<>();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(DEATH_DATA_URL))
                .build();

        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        StringReader csvBodyReady = new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReady);

        for (CSVRecord record : records) {
            DeathStats deathStatsStats = new DeathStats();
            deathStatsStats.setState(record.get("Province/State"));
            deathStatsStats.setCountry(record.get("Country/Region"));
            int latestCases = Integer.parseInt(record.get(record.size() - 1));
            int prevDayCases = Integer.parseInt(record.get(record.size() - 2));

            deathStatsStats.setLatestTotalCases(latestCases);
            deathStatsStats.setDiffFromPrevDay(latestCases - prevDayCases);

            newDeathStats.add(deathStatsStats);
        }
        this.AllDeathStats = newDeathStats;
    }

}
