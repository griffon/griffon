package greet;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: danno.ferrin
 * Date: Jan 7, 2009
 * Time: 8:45:44 PM
 */
public class TimeLabel extends JLabel {

    static final DateFormat twitterFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");

    final long timeRepresented;
    long nextUpdate;

    public TimeLabel(String twitterTime) {
        setText("unknown");
        long time = 0;
        try {
            time = twitterFormat.parse(twitterTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace(System.out);
        }
        timeRepresented = time;
    }

    public TimeLabel(long time) {
        setText("unknown");
        timeRepresented = time;
    }

    public void paint(Graphics g) {
        updateTimeString();
        super.paint(g);
    }

    public void print(Graphics g) {
        updateTimeString();
        super.paint(g);
    }

    private void updateTimeString() {
        if (timeRepresented == 0) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime < nextUpdate) return;

        int secs = (int) ((currentTime - timeRepresented) / 1000);
        String dir = (secs < 0) ? "from now" : "ago";
        if (secs < 0) secs = -secs;
        int count = 0;
        String unit = "";
        if (secs < 120) {
            count = 1;
            unit = " minute ";
            nextUpdate = currentTime + (60 - (secs%60))*1000;
        } else if (secs < 3600) {
            count = secs / 60;
            unit = " minutes ";
            nextUpdate = currentTime + (60 - (secs%60))*1000;
        } else if (secs < 7200) {
            count = 1;
            unit = " hour ";
            nextUpdate = currentTime + (3600 - (secs%3600))*1000;
        } else if (secs < 86400) {
            count = secs / 3600;
            unit = " hours ";
            nextUpdate = currentTime + (3600 - (secs%3600))*1000;
        } else if (secs < 172800) {
            count = 1;
            unit = " day ";
            nextUpdate = currentTime + (86400 - (secs%86400))*1000;
        } else {
            count = secs / 86400;
            unit = " days ";
            nextUpdate = currentTime + (86400 - (secs%86400))*1000;
        }
        setText(count + unit + dir);
    }

}
