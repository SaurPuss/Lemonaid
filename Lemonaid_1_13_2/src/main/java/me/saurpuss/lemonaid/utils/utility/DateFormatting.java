package me.saurpuss.lemonaid.utils.utility;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public interface DateFormatting {

    // Formatting LocalDate, ex: AUG 02
    default String dateToString(LocalDate date, String formatter) {
        if (formatter == null)
            return date.format(DateTimeFormatter.ofPattern("MMM dd"));

        return date.format(DateTimeFormatter.ofPattern(formatter));
    }
    default String dateToString(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("MMM dd"));
    }

    default LocalDate stringToDate(String date, String formatter) {
        if (formatter == null)
            return LocalDate.parse(date, DateTimeFormatter.ofPattern("MMM dd"));

        return LocalDate.parse(date, DateTimeFormatter.ofPattern(formatter));
    }

    default LocalDate stringToDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("MMM dd"));
    }

    default String localDateTimeToString(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "EE MMM dd y, hh:mm:ss a", Locale.getDefault());

        return date.format(formatter);
    }

    default String localDateTimeToString(long millis) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "EE MMM dd y, hh:mm:ss a", Locale.getDefault());

        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis),
                ZoneId.systemDefault());

        return date.format(formatter);
    }
}
