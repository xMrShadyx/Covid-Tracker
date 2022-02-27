package bg.hhyusein.covidtracker.controller;
/*
 * @created 27/02/2022 - 12:03 AM
 * @project covid-tracker
 * @author xMrShadyx (Hyusein Hyusein)
 */

import bg.hhyusein.covidtracker.models.LocationStats;
import bg.hhyusein.covidtracker.models.RecoveryStats;
import bg.hhyusein.covidtracker.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/")
    public String home(Model model) {
        List<LocationStats> allStats = coronaVirusDataService.getAllStats();
        List<RecoveryStats> allRecoveredStats = coronaVirusDataService.getRecoveredStats();

        int totalReportedCases = allStats.stream().mapToInt(LocationStats::getLatestTotalCases).sum();
        int totalNewCases = allStats.stream().mapToInt(LocationStats::getDiffFromPrevDay).sum();

        int totalRecoveredCases = allRecoveredStats.stream().mapToInt(RecoveryStats::getLatestTotalCases).sum();
        int totalNewRecoveredCases = allRecoveredStats.stream().mapToInt(RecoveryStats::getDiffFromPrevDay).sum();

        model.addAttribute("locationStats", allStats);
        model.addAttribute("totalReportedCases", totalReportedCases);
        model.addAttribute("totalNewCases", totalNewCases);

        model.addAttribute("totalRecoveredCases", totalRecoveredCases);
        model.addAttribute("totalNewRecoveredCases", totalNewRecoveredCases);


        return "home";
    }
}
