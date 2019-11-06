package me.saurpuss.lemonaid.utils.utility;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public interface DateFormatting {

    // Formatting LocalDate, ex: AUG 02
    default DateTimeFormatter formatter() {
        // TODO get pattern from config


        return DateTimeFormatter.ofPattern("MMM dd");
    }

    default String dateToString(LocalDate date) {
        return date.format(formatter());
    }

    default LocalDate stringToDate(String date) {
        return LocalDate.parse(date, formatter());
    }
}
