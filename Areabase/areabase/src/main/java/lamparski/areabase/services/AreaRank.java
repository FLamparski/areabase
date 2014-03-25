package lamparski.areabase.services;
/** !license-block 
    This file is part of Areabase.

    Areabase is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Areabase is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Areabase.  If not, see <http://www.gnu.org/licenses/>.

    Areabase (C) 2013-2014 Filip Wieland <filiplamparski@gmail.com>
*/
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import lamparski.areabase.AreaActivity;
import lamparski.areabase.CacheContentProvider;
import lamparski.areabase.CacheDbOpenHelper.AreaRankTable;
import lamparski.areabase.cardproviders.CrimeCardProvider;
import lamparski.areabase.cardproviders.EconomyCardProvider;
import lamparski.areabase.cardproviders.EnvironmentCardProvider;
import lamparski.areabase.cardproviders.TrendDescription;
import nde2.errors.NDE2Exception;
import nde2.pull.methodcalls.delivery.GetTables;
import nde2.pull.types.Area;
import nde2.pull.types.DataSetFamily;
import nde2.pull.types.Dataset;
import nde2.pull.types.Subject;
import nde2.pull.types.Topic;

import static nde2.helpers.CensusHelpers.findRequiredFamilies;
import static nde2.helpers.CensusHelpers.findSubject;

/**
 * Generates AreaRank for the area based on certain parameters. This score takes into account
 * key socio-economic variables.
 *
 * <p>
 *     The formula for calculating an AreaRank is as follows:
 * </p>
 *
 * <ol>
 *     <li>Start with a mid-value of 38.</li>
 *     <li>For the crime trend, change the value by -10, -5, 0, +5 or +10</li>
 *     <li>For the energy trend, change the value by -4, -2, 0, +2 or +4</li>
 *     <li>For the unemployment rate, change the value by -6, -3, 0, +3 or +6</li>
 *     <li>For the unemployment trend, change the value by -8, -4, 0, +4 or +8</li>
 *     <li>For the rate of free school meal pupils, change the value by -5, -2.5, 0, +2.5 or 5</li>
 *     <li>For the rate of A*-C GCSE pupils, change the value by -5, -2.5, 0, +2.5 or 5</li>
 *     <li>Take the value (as v) and return (v/76)*100</li>
 * </ol>
 *
 * <p>
 *     The changes are triggered by various thresholds that should be adjusted to national average
 *     values or other magics, however at the moment they are quite arbitrary.
 * </p>
 *
 * @author filip
 */
public class AreaRank {
    public static final int KS4_ASTAR_TO_C_ALL_PUPILS = 7700;
    public static final int ALL_PUPILS = 7705;
    public static final int FREE_SCHOOL_MEAL_PUPILS = 7706;
    public static float MID_SCORE = 38.0f;

    /**
     * Returns a score for the given area. Since computing an AreaRank score usually takes a while,
     * the scores are cached.
     * @param area The area to grade
     * @return AreaRank for the area
     * @throws Exception
     */
    public static float forArea(Area area) throws Exception {
        try{
            return cachedScore(area);
        } catch (FileNotFoundException fnfe){
            float score = newScore(area);
            saveScore(area, score);
            return score;
        }
    }

    /**
     * Tries to retrieve a cached score. If one is not available, returns NaN.
     * @param area The area to grade
     * @return Cached AreaRank or NaN.
     * @throws FileNotFoundException
     */
    private static float cachedScore(Area area) throws FileNotFoundException {
        ContentResolver resolver = AreaActivity.getAreabaseApplicationContext().getContentResolver();
        String[] selectionArgs = { Integer.toString(area.getAreaId()),
                Long.toString(System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000l) };
        Cursor c = resolver.query(CacheContentProvider.AREARANK_CACHE_URI,
                new String[] { "*" },
                "areaId = ? AND computedOn > ?",
                selectionArgs, "computedOn DESC");

        if(c == null){
            throw new FileNotFoundException();
        }

        float score = Float.NaN;
        if(c.moveToFirst()){
            score = c.getFloat(c.getColumnIndex(AreaRankTable.FIELD_AREA_RANK));
        }
        c.close();
        if(Float.isNaN(score)){
            throw new FileNotFoundException();
        }
        Log.d("AreaRank",
                String.format("Returning a cached score for %s: %.1f", area.getName(), score));
        return score;
    }

    /**
     * Generate a new score.
     * @param area The area to grade
     * @return AreaRank
     * @throws Exception
     */
    private static float newScore(Area area) throws Exception {
        float score = MID_SCORE;

        Log.d("AreaRank",
                String.format("Computing a new score for %s...", area.getName()));
        Subject economySubject = findSubject(area, "Economic Deprivation");
        score += crimeTrend(area);
        Log.v("AreaRank",
                String.format("Computing a new score for %s... [Crime component done]", area.getName()));
        score += energyTrend(area);
        Log.v("AreaRank",
                String.format("Computing a new score for %s... [Energy component done]", area.getName()));
        score += unemployment(area);
        Log.v("AreaRank",
                String.format("Computing a new score for %s... [Unemployment component done]", area.getName()));
        score += education(area);
        Log.v("AreaRank",
                String.format("Computing a new score for %s... [Education component done]", area.getName()));

        return (score/(MID_SCORE*2))*100;
    }

    /**
     * Save the given AreaRank to database.
     * @param area The graded area
     * @param score The score to save
     */
    private static void saveScore(Area area, float score){
        ContentResolver resolver = AreaActivity.getAreabaseApplicationContext().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(AreaRankTable.FIELD_AREA_ID, area.getAreaId());
        values.put(AreaRankTable.FIELD_AREA_RANK, score);
        values.put(AreaRankTable.FIELD_RETRIEVED_ON, System.currentTimeMillis());
        resolver.insert(CacheContentProvider.AREARANK_CACHE_URI, values);
        Log.d("AreaRank",
                String.format("Saving score for %s: %.1f", area.getName(), score));
    }

    /**
     * Analyses the unemployment rate and trend
     * @param area The area to grade
     * @return a modifier for the AreaRank
     * @throws XmlPullParserException
     * @throws IOException
     * @throws NDE2Exception
     */
    private static float unemployment(Area area) throws XmlPullParserException, IOException, NDE2Exception {
        TrendDescription trenddesc = EconomyCardProvider.getUnemploymentRate(area);

        float scoreComponent = 0f;

        float unemplRatio = trenddesc.currentValue;
        if(unemplRatio <= 0.02f){
            scoreComponent += 6f;
        } else if (unemplRatio <= 0.04f){
            scoreComponent += 3f;
        } else if (unemplRatio <= 0.08f) {
            scoreComponent += 0f;
        } else if (unemplRatio <= 0.1f){
            scoreComponent += -3f;
        } else {
            scoreComponent += -6f;
        }

        switch (trenddesc.which){
            case TrendDescription.FALLING_RAPIDLY:
                scoreComponent += 8f;
                break;
            case TrendDescription.FALLING:
                scoreComponent += 4f;
                break;
            case TrendDescription.STABLE:
                scoreComponent += 0f;
                break;
            case TrendDescription.RISING:
                scoreComponent += -4f;
                break;
            case TrendDescription.RISING_RAPIDLY:
                scoreComponent += -8f;
                break;
            default:
                break;
        }

        return scoreComponent;
    }

    /**
     * Analyses the rate of Free School Meals pupils and the A*-C GCSE rate.
     * @param area The area to grade
     * @return a modifier for the AreaRank
     * @throws XmlPullParserException
     * @throws IOException
     * @throws NDE2Exception
     */
    private static float education(Area area) throws XmlPullParserException, IOException, NDE2Exception {
        float scoreComponent = 0f;

        float allKS4Pupils = 0f;
        float freeMealsKS4Pupils = 0f;
        float aStarToCKS4Pupils = 0f;

        Subject eduSubject = findSubject(area, "Education, Skills and Training");
        String[] kw = new String[] { "GCSE and Equivalent Results for Young People by Free School Meal Eligibility, Referenced by Location of Pupil Residence" };
        List<DataSetFamily> dataSetFamilies = findRequiredFamilies(area, eduSubject, kw);
        Set<Dataset> datasets = new GetTables().inFamilies(dataSetFamilies).forArea(area).execute();

        Dataset dataset = datasets.iterator().next();
        for(Topic t : dataset.getTopics().values()){
            if(t.getTopicId() == KS4_ASTAR_TO_C_ALL_PUPILS){
                aStarToCKS4Pupils = dataset.getItems(t).iterator().next().getValue();
            } else if (t.getTopicId() == ALL_PUPILS){
                allKS4Pupils = dataset.getItems(t).iterator().next().getValue();
            } else if (t.getTopicId() == FREE_SCHOOL_MEAL_PUPILS){
                freeMealsKS4Pupils = dataset.getItems(t).iterator().next().getValue();
            }
        }

        float freeSchoolMealRatio = freeMealsKS4Pupils / allKS4Pupils;

        if(freeSchoolMealRatio >= 0.3){
            scoreComponent += -5f;
        } else if(freeSchoolMealRatio >= 0.2){
            scoreComponent += -2.5f;
        } else if(freeSchoolMealRatio >= 0.15){
            scoreComponent += 0f;
        } else if(freeSchoolMealRatio >= 0.1){
            scoreComponent += 2.5f;
        } else {
            scoreComponent += 5f;
        }

        if(aStarToCKS4Pupils >= 95f){
            scoreComponent += 5f;
        } else if (aStarToCKS4Pupils >= 90f){
            scoreComponent += 2.5f;
        } else if (aStarToCKS4Pupils >= 85f){
            scoreComponent += 0f;
        } else if (aStarToCKS4Pupils >= 80f){
            scoreComponent += -2.5f;
        } else {
            scoreComponent += -5f;
        }

        return scoreComponent;
    }

    /**
     * Analyses energy usage trend.
     * @param area The area to grade
     * @return a modifier for the AreaRank
     * @throws XmlPullParserException
     * @throws IOException
     * @throws NDE2Exception
     */
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

    /**
     * Analyses crime trend.
     * @param area The area to grade
     * @return a modifier for the AreaRank
     * @throws Exception
     */
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
