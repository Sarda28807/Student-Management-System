package com.novastudent.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Date formatting utility.
 */
public class DateUtil {

    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter DISPLAY_DATETIME = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
    private static final DateTimeFormatter SHORT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String formatDisplay(LocalDate date) {
        return date != null ? date.format(DISPLAY_DATE) : "—";
    }

    public static String formatDisplay(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DISPLAY_DATETIME) : "—";
    }

    public static String formatShort(LocalDate date) {
        return date != null ? date.format(SHORT_DATE) : "—";
    }

    public static String formatISO(LocalDate date) {
        return date != null ? date.format(ISO_DATE) : "";
    }

    public static String getRelativeTime(LocalDateTime dateTime) {
        if (dateTime == null) return "—";
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(dateTime, now).toMinutes();

        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + " min ago";
        long hours = minutes / 60;
        if (hours < 24) return hours + " hr ago";
        long days = hours / 24;
        if (days < 7) return days + " day" + (days > 1 ? "s" : "") + " ago";
        if (days < 30) return (days / 7) + " week" + (days / 7 > 1 ? "s" : "") + " ago";
        return formatDisplay(dateTime);
    }
}
