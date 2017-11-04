package com.peachy.videoplayer.model;

/**
 * Created by peachy on 11/4/17.
 */

public class Playlist {
    public int id;
    public int media_id;
    public String start_date;
    public String end_date;
    public String start_time;
    public String end_time;
    public String day_of_week;
    public int duration;
    public String options;
    public int draft;

    @Override
    public String toString() {
        return "Playlist{" +
                "id=" + id +
                ", media_id=" + media_id +
                ", start_date='" + start_date + '\'' +
                ", end_date='" + end_date + '\'' +
                ", start_time='" + start_time + '\'' +
                ", end_time='" + end_time + '\'' +
                ", day_of_week='" + day_of_week + '\'' +
                ", duration=" + duration +
                ", options='" + options + '\'' +
                ", draft=" + draft +
                '}';
    }
}
