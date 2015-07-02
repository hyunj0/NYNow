package nyteam.c4q.nyc.nynow;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by c4q-vanice on 7/2/15.
 */
public class Daily implements Parcelable {
    private long mTime;
    private String mSummary;
    private double mTemperatureMax;
    private String mIcon;
    private String mTimeZone;

    // So main activity can access with the private Daily(Parcel in)
    public Daily(){ }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public int getTemperatureMax() {
        return (int) Math.round(mTemperatureMax);
    }

    public void setTemperatureMax(double temperatureMax) {
        mTemperatureMax = temperatureMax;
    }

    public int getIconId(){ return Forecast.getIconId(mIcon); }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
    }

    public String getDayOfTheWeek(){
        SimpleDateFormat format = new SimpleDateFormat("EEEE"); // This gets the day
        format.setTimeZone(TimeZone.getTimeZone(mTimeZone)); // The day is retrieve from time but we need to know the correct time zone.
        Date dateTime = new Date(mTime * 1000);
        return format.format(dateTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }
    // out is the destination where the package will arrive.
    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeLong(mTime);
        out.writeString(mSummary);
        out.writeDouble(mTemperatureMax);
        out.writeString(mIcon);
        out.writeString(mTimeZone);
    }

    //Method to un-parcel the package. This will be only access by the creator field.
//TODO The order is very important! It goes in and comes out the exact same way.
    private Daily(Parcel in){
        mTime = in.readLong();
        mSummary = in.readString();
        mTemperatureMax = in.readDouble();
        mIcon = in.readString();
        mTimeZone = in.readString();
    }

    public static final Creator<Daily> CREATOR = new Creator<Daily>() {
        @Override
        public Daily createFromParcel(Parcel source) {
            return new Daily(source);
        }

        @Override
        public Daily[] newArray(int size) {
            return new Daily[size];
        }
    };
}

