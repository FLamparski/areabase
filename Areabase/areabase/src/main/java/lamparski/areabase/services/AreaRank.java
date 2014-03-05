package lamparski.areabase.services;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import lamparski.areabase.cardproviders.CrimeCardProvider;
import lamparski.areabase.cardproviders.EconomyCardProvider;
import lamparski.areabase.cardproviders.EnvironmentCardProvider;
import lamparski.areabase.cardproviders.TrendDescription;
import nde2.errors.NDE2Exception;
import nde2.errors.ValueNotAvailable;
import nde2.pull.methodcalls.delivery.GetTables;
import nde2.pull.types.Area;
import nde2.pull.types.DataSetFamily;
import nde2.pull.types.Dataset;
import nde2.pull.types.Subject;
import nde2.pull.types.Topic;

import static nde2.helpers.CensusHelpers.findRequiredFamilies;
import static nde2.helpers.CensusHelpers.findSubject;

/**
 * Created by Minkovsky on 04/03/14.
 */
public class AreaRank {
    public static float MID_SCORE = 38.0f;
    public static float getScore(Area area) throws Exception {
        float score = MID_SCORE;

        Subject economySubject = findSubject(area, "Economic Deprivation");
        score += crimeTrend(area);
        score += energyTrend(area);
        TrendDescription incomeTrend = EconomyCardProvider.calculateIncomeTrend(area,
                findRequiredFamilies(area, economySubject, EconomyCardProvider.ECONOMY_KEYWORDS));
        score += analyseIncomeTrend(incomeTrend);
        score += compareIncomeToNational(incomeTrend);
        score += unemployment(area);

        return (score/(MID_SCORE*2))*100;
    }

    private static float unemployment(Area area) throws XmlPullParserException, IOException, NDE2Exception {
        Subject censusSubject = findSubject(area, "Census");
        List<DataSetFamily> families = findRequiredFamilies(area,
                censusSubject, EconomyCardProvider.CENSUS_KEYWORDS);
        Set<Dataset> data = new GetTables().forArea(area).inFamilies(families).execute();
        float numUnemployed = 0;
        float numPeople = 0;
        for(Dataset ds : data){
            for(Topic t : ds.getTopics().values()){
                if(t.getTitle().equals("Economically Active; Unemployed")){
                    numUnemployed = ds.getItems(t).iterator().next().getValue();
                }
                if(t.getTitle().startsWith("All Usual Residents")){
                    numPeople = ds.getItems(t).iterator().next().getValue();
                }
            }
        }

        if(numPeople == 0){
            throw new ValueNotAvailable("Number of people in this area is 0. Abort before division.");
        }

        float unemplRatio = numUnemployed / numPeople;

        if(unemplRatio <= 0.02f){
            return 6f;
        } else if (unemplRatio <= 0.04f){
            return 3f;
        } else if (unemplRatio <= 0.08f) {
            return 0f;
        } else if (unemplRatio <= 0.1f){
            return -3f;
        } else {
            return -6f;
        }
    }

    private static float analyseIncomeTrend(TrendDescription incomeTrend) {
        switch(incomeTrend.which){
            case TrendDescription.FALLING_RAPIDLY:
                return -8f;
            case TrendDescription.FALLING:
                return -4f;
            case TrendDescription.STABLE:
                return 0f;
            case TrendDescription.RISING:
                return 4f;
            case TrendDescription.RISING_RAPIDLY:
                return 8f;
            default:
                return 0;
        }
    }

    private static float compareIncomeToNational(TrendDescription incomeTrend) {
        TrendDescription d = EconomyCardProvider.compareIncomeWithNational(incomeTrend.currentValue);
        switch(d.which){
            case TrendDescription.FALLING_RAPIDLY:
                return -10f;
            case TrendDescription.FALLING:
                return -5f;
            case TrendDescription.STABLE:
                return 0f;
            case TrendDescription.RISING:
                return 5f;
            case TrendDescription.RISING_RAPIDLY:
                return 10f;
            default:
                return 0;
        }
    }

    private static float energyTrend(Area area) throws XmlPullParserException, IOException, NDE2Exception {
        TrendDescription trendDescription = EnvironmentCardProvider.getEnergyTrend(area,
                findRequiredFamilies(area,
                        findSubject(area, EnvironmentCardProvider.ENVIRONMENT_SUBJECT),
                        EnvironmentCardProvider.REQUIRED_FAMILIES));
        switch(trendDescription.which){
            case TrendDescription.FALLING_RAPIDLY:
                return 4f;
            case TrendDescription.FALLING:
                return 2f;
            case TrendDescription.STABLE:
                return 0f;
            case TrendDescription.RISING:
                return -2f;
            case TrendDescription.RISING_RAPIDLY:
                return -4f;
            default:
                return 0;
        }
    }

    private static float crimeTrend(Area area) throws Exception {
        double gradient = CrimeCardProvider.getCrimeTrend(area);
        if (gradient > CrimeCardProvider.TREND_RAPID_LOWER_THRESHOLD
                && gradient <= CrimeCardProvider.TREND_STABLE_LOWER_THRESHOLD) {
            return 5f;
        } else if (gradient > CrimeCardProvider.TREND_STABLE_LOWER_THRESHOLD
                && gradient <= CrimeCardProvider.TREND_STABLE_UPPER_THRESHOLD) {
            return 0f;
        } else if (gradient > CrimeCardProvider.TREND_STABLE_UPPER_THRESHOLD
                && gradient <= CrimeCardProvider.TREND_RAPID_UPPER_THRESHOLD) {
            return -5f;
        } else if (gradient > CrimeCardProvider.TREND_RAPID_UPPER_THRESHOLD) {
            return -10f;
        } else {
            return 10f;
        }
    }
}
