package co.amscraft.ontime;

import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultralib.player.PlayerData;
import com.earth2me.essentials.Essentials;
import net.ess3.api.IUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OntimeData extends PlayerData {
    private static final int SECONDS_TO_MINUTE = 60;
    private static final int SECONDS_TO_HOUR = SECONDS_TO_MINUTE * 60;
    private static final int SECONDS_TO_DAY = SECONDS_TO_HOUR * 24;
    private static final long SECONDS_TO_YEAR = 365 * SECONDS_TO_DAY;
    private long total;
    private long daily;
    private String day = "";
    @FieldDescription(save = false)
    private long joined = System.currentTimeMillis();
    @FieldDescription(save = false)
    private long afk;
    private List<Integer> givenRewards = new ArrayList<>();

    public static String format(long ontime) {
        long years = ontime / SECONDS_TO_YEAR;
        long days = (ontime - years * SECONDS_TO_YEAR) / SECONDS_TO_DAY;
        long hours = (ontime - days * SECONDS_TO_DAY - years * SECONDS_TO_YEAR) / SECONDS_TO_HOUR;
        long minutes = (ontime - hours * SECONDS_TO_HOUR - days * SECONDS_TO_DAY - years * SECONDS_TO_YEAR) / SECONDS_TO_MINUTE;
        long seconds = (ontime - hours * SECONDS_TO_HOUR - days * SECONDS_TO_DAY - years * SECONDS_TO_YEAR - minutes * SECONDS_TO_MINUTE) % SECONDS_TO_MINUTE;
        return (years != 0 ? years + "y " : "") + (days != 0 ? (days + "d ") : "") + hours + "h " + minutes + "m " + seconds + "s";
    }

    public static String currentDay() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
    }

    public List<Integer> getGivenRewards() {
        return givenRewards;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getDaily() {
        return daily;
    }

    public void setDaily(long daily) {
        this.daily = daily;
    }

    public void addDaily(long daily) {
        if (daily > 0) {
            this.daily += daily;
        }
    }

    public void addTotal(long total) {
        if (total > 0) {
            this.setTotal(this.getTotal() + total);
        }
    }

    public long getTotalOntime() {
        return this.getTotal() + this.getCurrentOntime();
    }

    public long getDailyOntime() {
        return this.getDaily() + this.getCurrentOntime();
    }

    public long getCurrentOntime() {
        IUser user = Essentials.getPlugin(Essentials.class).getUser(this.getPlayer().getId());
        long afk = 0;
        if (user.isAfk()) {
            afk += user.getAfkSince();
        }
        return (System.currentTimeMillis() - this.getJoined()) / 1000 - this.getAfk() - afk;
    }


    public void addTime(long time) {
        this.addDaily(time);
        this.addTotal(time);
    }

    public String getDay() {
        return this.day;
    }

    public void setToday() {
        this.day = currentDay();
    }

    public boolean isToday() {
        return this.getDay().equals(currentDay());
    }

    public long getAfk() {
        return afk;
    }

    public void setAfk(long afk) {
        this.afk = afk;
    }

    public long getJoined() {
        return joined;
    }
}
