package com.peachy.videoplayer.model;

/**
 * Created by peachy on 11/4/17.
 */

public class Media {
    public int id;
    public String type;
    public String file_path;
    public int defaults;
    public int enable;

    @Override
    public String toString() {
        return "Media{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", file_path='" + file_path + '\'' +
                ", defaults=" + defaults +
                ", enable=" + enable +
                '}';
    }
}
