package org.springframework.scheduling.support;

import ch.qos.logback.classic.spi.CallerData;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scheduling/support/CronSequenceGenerator.class */
public class CronSequenceGenerator {
    private final String expression;
    @Nullable
    private final TimeZone timeZone;
    private final BitSet months;
    private final BitSet daysOfMonth;
    private final BitSet daysOfWeek;
    private final BitSet hours;
    private final BitSet minutes;
    private final BitSet seconds;

    public CronSequenceGenerator(String expression) {
        this(expression, TimeZone.getDefault());
    }

    public CronSequenceGenerator(String expression, TimeZone timeZone) {
        this.months = new BitSet(12);
        this.daysOfMonth = new BitSet(31);
        this.daysOfWeek = new BitSet(7);
        this.hours = new BitSet(24);
        this.minutes = new BitSet(60);
        this.seconds = new BitSet(60);
        this.expression = expression;
        this.timeZone = timeZone;
        parse(expression);
    }

    private CronSequenceGenerator(String expression, String[] fields) {
        this.months = new BitSet(12);
        this.daysOfMonth = new BitSet(31);
        this.daysOfWeek = new BitSet(7);
        this.hours = new BitSet(24);
        this.minutes = new BitSet(60);
        this.seconds = new BitSet(60);
        this.expression = expression;
        this.timeZone = null;
        doParse(fields);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getExpression() {
        return this.expression;
    }

    public Date next(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeZone(this.timeZone);
        calendar.setTime(date);
        calendar.set(14, 0);
        long originalTimestamp = calendar.getTimeInMillis();
        doNext(calendar, calendar.get(1));
        if (calendar.getTimeInMillis() == originalTimestamp) {
            calendar.add(13, 1);
            doNext(calendar, calendar.get(1));
        }
        return calendar.getTime();
    }

    private void doNext(Calendar calendar, int dot) {
        List<Integer> resets = new ArrayList<>();
        int second = calendar.get(13);
        List<Integer> emptyList = Collections.emptyList();
        int updateSecond = findNext(this.seconds, second, calendar, 13, 12, emptyList);
        if (second == updateSecond) {
            resets.add(13);
        }
        int minute = calendar.get(12);
        int updateMinute = findNext(this.minutes, minute, calendar, 12, 11, resets);
        if (minute == updateMinute) {
            resets.add(12);
        } else {
            doNext(calendar, dot);
        }
        int hour = calendar.get(11);
        int updateHour = findNext(this.hours, hour, calendar, 11, 7, resets);
        if (hour == updateHour) {
            resets.add(11);
        } else {
            doNext(calendar, dot);
        }
        int dayOfWeek = calendar.get(7);
        int dayOfMonth = calendar.get(5);
        int updateDayOfMonth = findNextDay(calendar, this.daysOfMonth, dayOfMonth, this.daysOfWeek, dayOfWeek, resets);
        if (dayOfMonth == updateDayOfMonth) {
            resets.add(5);
        } else {
            doNext(calendar, dot);
        }
        int month = calendar.get(2);
        int updateMonth = findNext(this.months, month, calendar, 2, 1, resets);
        if (month != updateMonth) {
            if (calendar.get(1) - dot > 4) {
                throw new IllegalArgumentException("Invalid cron expression \"" + this.expression + "\" led to runaway search for next trigger");
            }
            doNext(calendar, dot);
        }
    }

    private int findNextDay(Calendar calendar, BitSet daysOfMonth, int dayOfMonth, BitSet daysOfWeek, int dayOfWeek, List<Integer> resets) {
        int count = 0;
        while (true) {
            if (!daysOfMonth.get(dayOfMonth) || !daysOfWeek.get(dayOfWeek - 1)) {
                int i = count;
                count++;
                if (i >= 366) {
                    break;
                }
                calendar.add(5, 1);
                dayOfMonth = calendar.get(5);
                dayOfWeek = calendar.get(7);
                reset(calendar, resets);
            } else {
                break;
            }
        }
        if (count >= 366) {
            throw new IllegalArgumentException("Overflow in day for expression \"" + this.expression + "\"");
        }
        return dayOfMonth;
    }

    private int findNext(BitSet bits, int value, Calendar calendar, int field, int nextField, List<Integer> lowerOrders) {
        int nextValue = bits.nextSetBit(value);
        if (nextValue == -1) {
            calendar.add(nextField, 1);
            reset(calendar, Collections.singletonList(Integer.valueOf(field)));
            nextValue = bits.nextSetBit(0);
        }
        if (nextValue != value) {
            calendar.set(field, nextValue);
            reset(calendar, lowerOrders);
        }
        return nextValue;
    }

    private void reset(Calendar calendar, List<Integer> fields) {
        for (Integer num : fields) {
            int field = num.intValue();
            calendar.set(field, field == 5 ? 1 : 0);
        }
    }

    private void parse(String expression) throws IllegalArgumentException {
        String[] fields = StringUtils.tokenizeToStringArray(expression, " ");
        if (!areValidCronFields(fields)) {
            throw new IllegalArgumentException(String.format("Cron expression must consist of 6 fields (found %d in \"%s\")", Integer.valueOf(fields.length), expression));
        }
        doParse(fields);
    }

    private void doParse(String[] fields) {
        setNumberHits(this.seconds, fields[0], 0, 60);
        setNumberHits(this.minutes, fields[1], 0, 60);
        setNumberHits(this.hours, fields[2], 0, 24);
        setDaysOfMonth(this.daysOfMonth, fields[3]);
        setMonths(this.months, fields[4]);
        setDays(this.daysOfWeek, replaceOrdinals(fields[5], "SUN,MON,TUE,WED,THU,FRI,SAT"), 8);
        if (this.daysOfWeek.get(7)) {
            this.daysOfWeek.set(0);
            this.daysOfWeek.clear(7);
        }
    }

    private String replaceOrdinals(String value, String commaSeparatedList) {
        String[] list = StringUtils.commaDelimitedListToStringArray(commaSeparatedList);
        for (int i = 0; i < list.length; i++) {
            String item = list[i].toUpperCase();
            value = StringUtils.replace(value.toUpperCase(), item, "" + i);
        }
        return value;
    }

    private void setDaysOfMonth(BitSet bits, String field) {
        setDays(bits, field, 31 + 1);
        bits.clear(0);
    }

    private void setDays(BitSet bits, String field, int max) {
        if (field.contains(CallerData.NA)) {
            field = "*";
        }
        setNumberHits(bits, field, 0, max);
    }

    private void setMonths(BitSet bits, String value) {
        String value2 = replaceOrdinals(value, "FOO,JAN,FEB,MAR,APR,MAY,JUN,JUL,AUG,SEP,OCT,NOV,DEC");
        BitSet months = new BitSet(13);
        setNumberHits(months, value2, 1, 12 + 1);
        for (int i = 1; i <= 12; i++) {
            if (months.get(i)) {
                bits.set(i - 1);
            }
        }
    }

    private void setNumberHits(BitSet bits, String value, int min, int max) {
        String[] fields = StringUtils.delimitedListToStringArray(value, ",");
        for (String field : fields) {
            if (!field.contains("/")) {
                int[] range = getRange(field, min, max);
                bits.set(range[0], range[1] + 1);
            } else {
                String[] split = StringUtils.delimitedListToStringArray(field, "/");
                if (split.length > 2) {
                    throw new IllegalArgumentException("Incrementer has more than two fields: '" + field + "' in expression \"" + this.expression + "\"");
                }
                int[] range2 = getRange(split[0], min, max);
                if (!split[0].contains("-")) {
                    range2[1] = max - 1;
                }
                int delta = Integer.parseInt(split[1]);
                if (delta <= 0) {
                    throw new IllegalArgumentException("Incrementer delta must be 1 or higher: '" + field + "' in expression \"" + this.expression + "\"");
                }
                int i = range2[0];
                while (true) {
                    int i2 = i;
                    if (i2 <= range2[1]) {
                        bits.set(i2);
                        i = i2 + delta;
                    }
                }
            }
        }
    }

    private int[] getRange(String field, int min, int max) {
        int[] result = new int[2];
        if (field.contains("*")) {
            result[0] = min;
            result[1] = max - 1;
            return result;
        }
        if (!field.contains("-")) {
            int intValue = Integer.valueOf(field).intValue();
            result[1] = intValue;
            result[0] = intValue;
        } else {
            String[] split = StringUtils.delimitedListToStringArray(field, "-");
            if (split.length > 2) {
                throw new IllegalArgumentException("Range has more than two fields: '" + field + "' in expression \"" + this.expression + "\"");
            }
            result[0] = Integer.valueOf(split[0]).intValue();
            result[1] = Integer.valueOf(split[1]).intValue();
        }
        if (result[0] >= max || result[1] >= max) {
            throw new IllegalArgumentException("Range exceeds maximum (" + max + "): '" + field + "' in expression \"" + this.expression + "\"");
        }
        if (result[0] < min || result[1] < min) {
            throw new IllegalArgumentException("Range less than minimum (" + min + "): '" + field + "' in expression \"" + this.expression + "\"");
        }
        if (result[0] > result[1]) {
            throw new IllegalArgumentException("Invalid inverted range: '" + field + "' in expression \"" + this.expression + "\"");
        }
        return result;
    }

    public static boolean isValidExpression(@Nullable String expression) {
        if (expression == null) {
            return false;
        }
        String[] fields = StringUtils.tokenizeToStringArray(expression, " ");
        if (!areValidCronFields(fields)) {
            return false;
        }
        try {
            new CronSequenceGenerator(expression, fields);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static boolean areValidCronFields(@Nullable String[] fields) {
        return fields != null && fields.length == 6;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CronSequenceGenerator)) {
            return false;
        }
        CronSequenceGenerator otherCron = (CronSequenceGenerator) other;
        return this.months.equals(otherCron.months) && this.daysOfMonth.equals(otherCron.daysOfMonth) && this.daysOfWeek.equals(otherCron.daysOfWeek) && this.hours.equals(otherCron.hours) && this.minutes.equals(otherCron.minutes) && this.seconds.equals(otherCron.seconds);
    }

    public int hashCode() {
        return (17 * this.months.hashCode()) + (29 * this.daysOfMonth.hashCode()) + (37 * this.daysOfWeek.hashCode()) + (41 * this.hours.hashCode()) + (53 * this.minutes.hashCode()) + (61 * this.seconds.hashCode());
    }

    public String toString() {
        return getClass().getSimpleName() + ": " + this.expression;
    }
}