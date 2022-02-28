package bg.hhyusein.covidtracker.controller;
/*
 * @created 27/02/2022 - 12:03 AM
 * @project covid-tracker
 * @author xMrShadyx (Hyusein Hyusein)
 */

import bg.hhyusein.covidtracker.models.DeathStats;
import bg.hhyusein.covidtracker.models.LocationStats;
import bg.hhyusein.covidtracker.services.CoronaVirusDataService;
import bg.hhyusein.covidtracker.services.CoronaVirusDeathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    @Autowired
    CoronaVirusDeathService coronaVirusDeathService;

    @GetMapping("/")
    public String home(Model model) {
        List<LocationStats> allStats = coronaVirusDataService.getAllStats();
        List<DeathStats> allDeathStats = coronaVirusDeathService.getDeathStats();

        int totalReportedCases = allStats.stream().mapToInt(LocationStats::getLatestTotalCases).sum();
        int totalNewCases = allStats.stream().mapToInt(LocationStats::getDiffFromPrevDay).sum();

        int totalDeathCases = allDeathStats.stream().mapToInt(DeathStats::getLatestTotalCases).sum();
        int totalNewDeathCases = allDeathStats.stream().mapToInt(DeathStats::getDiffFromPrevDay).sum();

        model.addAttribute("locationStats", allStats);
        model.addAttribute("totalReportedCases", totalReportedCases);
        model.addAttribute("totalNewCases", totalNewCases);

        model.addAttribute("totalDeathCases", totalDeathCases);
        model.addAttribute("totalNewDeathCases", totalNewDeathCases);


        return "home";
    }
}
