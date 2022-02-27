package bg.hhyusein.covidtracker.services;
/*
 * @created 26/02/2022 - 11:32 PM
 * @project covid-tracker
 * @author xMrShadyx (Hyusein Hyusein)
 */

import bg.hhyusein.covidtracker.models.LocationStats;
import bg.hhyusein.covidtracker.models.RecoveryStats;
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
public class CoronaVirusDataService {

    // Data fetched from: https://github.com/CSSEGISandData/COVID-19/tree/master/csse_covid_19_data
    // Original author: CSSEGISandData.

    public static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    public static String RECOVER_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv";

    private List<LocationStats> allStats = new ArrayList<>();
    private List<RecoveryStats> allRecoveredStats = new ArrayList<>();

    public List<RecoveryStats> getRecoveredStats() {
        return allRecoveredStats;
    }

    public List<LocationStats> getAllStats() {
        return allStats;
    }

    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fetchVirusData() throws IOException, InterruptedException {
        List<LocationStats> newStats = new ArrayList<>();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_DATA_URL))
                .build();

        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        StringReader csvBodyReady = new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReady);

        for (CSVRecord record : records) {
            LocationStats locationStats = new LocationStats();
            locationStats.setState(record.get("Province/State"));
            locationStats.setCountry(record.get("Country/Region"));
            int latestCases = Integer.parseInt(record.get(record.size() - 1));
            int prevDayCases = Integer.parseInt(record.get(record.size() - 2));

            locationStats.setLatestTotalCases(latestCases);
            locationStats.setDiffFromPrevDay(latestCases - prevDayCases);

            newStats.add(locationStats);
        }
        this.allStats = newStats;
    }

    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fetchRecoveredData() throws IOException, InterruptedException {
        List<RecoveryStats> newRecoveryStats = new ArrayList<>();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(RECOVER_DATA_URL))
                .build();

        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        StringReader csvBodyReady = new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReady);

        for (CSVRecord record : records) {
            RecoveryStats recoveryStatsStats = new RecoveryStats();
            recoveryStatsStats.setState(record.get("Province/State"));
            recoveryStatsStats.setCountry(record.get("Country/Region"));
            int latestCases = Integer.parseInt(record.get(record.size() - 1));
            int prevDayCases = Integer.parseInt(record.get(record.size() - 2));

            recoveryStatsStats.setLatestTotalCases(latestCases);
            recoveryStatsStats.setDiffFromPrevDay(latestCases - prevDayCases);

            newRecoveryStats.add(recoveryStatsStats);
        }
        this.allRecoveredStats = newRecoveryStats;
    }
}
