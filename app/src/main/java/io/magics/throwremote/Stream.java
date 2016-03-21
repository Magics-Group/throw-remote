package io.magics.throwremote;

public class Stream {

    public String title;
    public Integer length;
    public Integer position;
    public Integer time;
    public Boolean playing;
    public Boolean muted;
    public Boolean ended;

    public Stream() {
        title = "";
        length = 0;
        position = 0;
        time = 0;
        playing = false;
        muted = false;
        ended = false;
    }
}