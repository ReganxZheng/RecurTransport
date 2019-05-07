/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
/**
 *
 * @author ZhengJwh
 */
public class Time {
    protected Date start_date;
    protected Date cutoff_date;
    protected Date current;
    DateFormat format = new SimpleDateFormat("dd/MM/yyyy H:m:s");
    public Time(int year, int month, int day, int hour, int min, int sec) {
        current = new Date();
        Date d = new Date(year-1900,month-1,day, hour, min, sec);
        this.start_date=current;
        this.cutoff_date=d;
    }
    
    public void setCutOffDate(Date date) {
        this.cutoff_date=date;
    }
    
    public Date getDate() {return this.cutoff_date;}
    
    public String formatStart() {
        return format.format(start_date);
    }
    public String formatCutOff() {
        return format.format(cutoff_date);
    }
    public int getYear(){
        return this.cutoff_date.getYear()+1900;
    }
    public int getMonth(){
        return this.cutoff_date.getMonth()+1;
    }
    public int getDay(){
        return this.cutoff_date.getDate();
    }
    public int getHour(){
        return this.cutoff_date.getHours();
    }
    public int getMin(){
        return this.cutoff_date.getMinutes();
    }
    @Override
    public String toString() {   
        return "The order start at ("+format.format(start_date)+") and due at ("+format.format(cutoff_date)+")";
    }
}
