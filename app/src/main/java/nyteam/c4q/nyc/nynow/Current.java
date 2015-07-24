package nyteam.c4q.nyc.nynow;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by c4q-vanice on 7/2/15.
 */
public class Current {

    private String mLocation;
    private String mIcon;
    private long mTime;
    private double mTemperature;
    private double mHumidity;
    private double mPrecipitation;
    private String mSummary;
    private String mTimeZone;

    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(String timezone) {
        mTimeZone = timezone;
    }


    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    // method to convert time since it is in UNIX
    public String getFormattedTime(){
        SimpleDateFormat format = new SimpleDateFormat("h:mm a");
        format.setTimeZone(TimeZone.getTimeZone(getTimeZone()));
        Date timeFromDate = new Date(getTime()* 1000);
        String timeString = format.format(timeFromDate);
        return timeString;
    }

    public String getIcon() {
        return mIcon;
    }

    public int getIconId(){ return Forecast.getIconId(mIcon); }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public int getTemperature() {
        return (int) Math.round(mTemperature);
    }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }

    public double getHumidity() {
        return mHumidity;
    }

    public void setHumidity(double humidity) {
        mHumidity = humidity;
    }

    public int getPrecipitation() {
        double precip = mPrecipitation * 100;
        return (int) Math.round(precip);
    }

    public void setPrecipitation(double precipitation) {
        mPrecipitation = precipitation;
    }

}

