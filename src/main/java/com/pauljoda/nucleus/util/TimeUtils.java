package com.pauljoda.nucleus.util;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/**
 * This file was created for Nucleus
 * <p>
 * Nucleus is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * <a href="http://creativecommons.org/licenses/by-nc-sa/4.0/">License</a>
 *
 * @author Paul Davis - pauljoda
 * @since 2/14/2017
 */
public class TimeUtils {
    public static long tick = 0;

    /**
     * The seconds online
     *
     * @return How many seconds on, floored
     */
    public static int seconds() {
        return (int) Math.floor(tick / 20);
    }

    /**
     * How many minutes on
     *
     * @return Minutes, floored
     */
    public static int minutes() {
        return (int) Math.floor(seconds() / 60);
    }

    /**
     * How many hours on
     *
     * @return Hours, floored
     */
    public static int hours() {
        return (int) Math.floor(minutes() / 60);
    }

    /**
     * Used to check if the current seconds is on the time multiple of the given variable
     *
     * @param second What second, for instance checking on 5 will trigger every 5 seconds
     * @return True if on time
     */
    public static boolean onSecond(int second) {
        return seconds() % second == 0;
    }

    /**
     * Used to check if the current minutes is on the time multiple of the given variable
     *
     * @param minute What minute, for instance checking on 5 will trigger every 5 minutes
     * @return True if on time
     */
    public static boolean onMinute(int minute) {
        return minutes() % minute == 0;
    }

    /**
     * Used to check if the current hours is on the time multiple of the given variable
     *
     * @param hour What hour, for instance checking on 5 will trigger every 5 hours
     * @return True if on time
     */
    public static boolean onHour(int hour) {
        return hours() % hour == 0;
    }

    @SubscribeEvent
    public void onWorldTick(LevelTickEvent.Post levelTickEvent) {
        tick = levelTickEvent.getLevel().getGameTime();
    }
}
