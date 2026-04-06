package com.albee.albeepoint.api.util;
 
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.albee.albeepoint.api.common.constant.AlbeeConst;
import com.albee.albeepoint.api.common.constant.ErrorCode;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DateUtil {

    public static final Logger log = LogManager.getLogger(DateUtil.class);

    public static Timestamp getNowTimestamp(){
        return Timestamp.valueOf(LocalDateTime.now());
    }

    public static String getTodayString(){
        String formatStr = "yyyyMMdd";
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        return format.format(new Date());
    }

    public static String getTodayString(String formatStr){
        if(formatStr.trim() == ""){
            formatStr = "yyyyMMdd";
        }

        SimpleDateFormat format = new SimpleDateFormat((formatStr));
        return format.format(LocalDate.now());
    }

    public static String getNowDateTimeString(){
        String formatStr = "yyyyMMddHHmmss";
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        return format.format(new Date());
    }

    public static String getNowTimeZoneDayString(){
        String dtStr = getNowTimeZoneDateTimeString();
        return dtStr.substring(0, 8);
    }

    public static String getNowTimeZoneDateTimeString(){
        String formatStr = "yyyyMMddHHmmss";
        return LocalDateTime.now().plusHours(AlbeeConst.timeZonePlusOffset).format(DateTimeFormatter.ofPattern(formatStr));
    }

    public static String getNowDateTimeString(String formatStr){
        if(formatStr.trim() == ""){
            formatStr = "yyyyMMddHHmmss";
        }

        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        return format.format(new Date());
    }




    // 해당 조건에 대해 마지막날짜 구하기
    public static Timestamp getDDay(String dv, LocalDate baseDt, int calcVal){
        LocalDate dDay = null;
        switch(dv){
            case "DAY" : dDay = baseDt.plusDays(calcVal); break;
            case "WEEK" : dDay = getLastDay("W", baseDt.plusWeeks(Long.valueOf(calcVal)));break;
            case "MONTH" : dDay = getLastDay("M", baseDt.plusMonths(Long.valueOf(calcVal)));break;
            case "YEAR" : dDay = getLastDay("Y", baseDt.plusYears(Long.valueOf(calcVal)));break;
        }

        LocalDateTime lastDt = dDay.atStartOfDay().plusHours(AlbeeConst.timeZoneOffset);

        Timestamp lastTs = Timestamp.valueOf(lastDt.plusDays(1).plusNanos(-1));
        return lastTs;
    }


    public static LocalDate getLastDay(String dv, LocalDate baseDd){
        LocalDate lastDay = null;

        if(dv.equalsIgnoreCase("M")){
            lastDay = baseDd.withDayOfMonth((baseDd.lengthOfMonth()));
        }else if(dv.equalsIgnoreCase("Y")){
            lastDay = baseDd.withDayOfYear(baseDd.getDayOfYear());
        }else if(dv.equalsIgnoreCase("W")){
            int gapDays = 0;
            switch(baseDd.getDayOfWeek()){
                case MONDAY: gapDays = 6; break;
                case TUESDAY: gapDays = 5; break;
                case WEDNESDAY: gapDays = 4; break;
                case THURSDAY: gapDays = 3; break;
                case FRIDAY: gapDays = 2; break;
                case SATURDAY: gapDays = 1; break;
                case SUNDAY: gapDays = 0; break;
            }

            lastDay = baseDd.plusDays(gapDays);
        }

        return lastDay;
    }

    public static String getFirstDayString(String dv, String baseVal){
        LocalDate day = LocalDate.of(Integer.valueOf(baseVal.substring(0, 4))
                        , Integer.valueOf(baseVal.substring(4, 6))
                        , Integer.valueOf(baseVal.substring(6, 8)));
        String firstDay = "";

        if(dv.equalsIgnoreCase("DAY")) {
            firstDay = baseVal;
        }else if(dv.equalsIgnoreCase("MONTH")){
            firstDay = baseVal.substring(0, 6) + "01";
        }else if(dv.equalsIgnoreCase("YEAR")){
            firstDay = baseVal.substring(0, 4) + "0101";
        }else if(dv.equalsIgnoreCase("WEEK")){
            int gapDays = 0;
            switch(day.getDayOfWeek()){
                case MONDAY: gapDays = 0; break;
                case TUESDAY: gapDays = 1; break;
                case WEDNESDAY: gapDays = 2; break;
                case THURSDAY: gapDays = 3; break;
                case FRIDAY: gapDays = 4; break;
                case SATURDAY: gapDays = 5; break;
                case SUNDAY: gapDays = 6; break;
            }

            firstDay = day.plusDays(gapDays).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        }

        return firstDay;
    }

    public static String getLastDayString(String dv, String baseVal){
        LocalDate day = LocalDate.of(Integer.valueOf(baseVal.substring(0, 4))
                , Integer.valueOf(baseVal.substring(4, 6))
                , Integer.valueOf(baseVal.substring(6, 8)));
        String lastDay = "";

        if(dv.equalsIgnoreCase("DAY")) {
            lastDay = baseVal;
        }else if(dv.equalsIgnoreCase("M")){
            lastDay = baseVal.substring(0, 6) + StrUtil.leftPad(String.valueOf(day.lengthOfMonth()), 2, "0");
        }else if(dv.equalsIgnoreCase("Y")){
            lastDay = baseVal.substring(0, 4) + "1231";
        }else if(dv.equalsIgnoreCase("W")){
            int gapDays = 0;
            switch(day.getDayOfWeek()){
                case MONDAY: gapDays = 6; break;
                case TUESDAY: gapDays = 5; break;
                case WEDNESDAY: gapDays = 4; break;
                case THURSDAY: gapDays = 3; break;
                case FRIDAY: gapDays = 2; break;
                case SATURDAY: gapDays = 1; break;
                case SUNDAY: gapDays = 0; break;
            }

            lastDay = day.plusDays(gapDays).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        }

        return lastDay;
    }

    public static Boolean isBefore(String val){
        LocalDate day = LocalDate.of(Integer.valueOf(val.substring(0, 4))
                , Integer.valueOf(val.substring(4, 6))
                , Integer.valueOf(val.substring(6, 8)));

        LocalDate today = LocalDate.now();
        return day.isBefore(today);
    }

    public static Boolean isFuture(String val){
        LocalDate day = LocalDate.of(Integer.valueOf(val.substring(0, 4))
                , Integer.valueOf(val.substring(4, 6))
                , Integer.valueOf(val.substring(6, 8)));

        LocalDate today = LocalDate.now();
        return day.isAfter(today);
    }

    public static Boolean isBetween(String val1, String val2){
        long today = Long.valueOf(getTodayString());
        long d1 = Long.valueOf(val1);
        long d2 = Long.valueOf(val2);
        if(today >= d1 && today <= d2){
            return true;
        }else{
            return false;
        }
    }

    public static Boolean isBetween(LocalDate val1, LocalDate val2){
        LocalDate today = LocalDate.now();
        if((val1.isBefore(today) || val1.isEqual(today)) && (today.isBefore(val2) || today.equals(val2))){
            return true;
        }else{
            return false;
        }
    }


    public static Boolean isBetween(String day, String val1, String val2){
        long d = Long.valueOf(day);
        long d1 = Long.valueOf(val1);
        long d2 = Long.valueOf(val2);
        if(d >= d1 && d <= d2){
            return true;
        }else{
            return false;
        }
    }

    public static Boolean isBetween(LocalDate day, LocalDate val1, LocalDate val2){
        if((val1.isBefore(day) || val1.isEqual(day)) && (day.isBefore(val2) || day.equals(val2))){
            return true;
        }else{
            return false;
        }
    }

    public static void betweenTsEc(Timestamp val1, Timestamp val2, ErrorCode errorCode){
        betweenTsEc(val1, val2, errorCode);
    }


    public static void betweenTsEc(Timestamp val1, Timestamp val2, ErrorCode errorCode, String message){
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime valDt1 = val1.toLocalDateTime();
        LocalDateTime valDt2 = val2.toLocalDateTime();

        // valDt1 가 today 보다 이전(before || equal) 이고 valDt2가 today 보다 나중(after)
        if((valDt1.isBefore(today) || valDt1.isEqual(today)) && (today.isBefore(valDt2) || today.equals(valDt2))){
            return ;
        }else{
            log.error(errorCode.getErrorCode() + "[" + (VdUtil.isEmpty(message) ? errorCode.getMessage() : message) + "]");
            throw new AlbeepointException(errorCode, VdUtil.isEmpty(message) ? errorCode.getMessage() : message);
        }
    }

    public static Boolean isBetween(Timestamp val1, Timestamp val2){
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime valDt1 = val1.toLocalDateTime();
        LocalDateTime valDt2 = val2.toLocalDateTime();

        // valDt1 가 today 보다 이전(before || equal) 이고 valDt2가 today 보다 나중(after)
        if((valDt1.isBefore(today) || valDt1.isEqual(today)) && (today.isBefore(valDt2) || today.equals(valDt2))){
            return true;
        }else{
            return false;
        }
    }


    public static Boolean isBetweenAp(LocalDate val1, LocalDate val2){
        return isBetweenAp(Timestamp.valueOf(LocalDateTime.now())
                , Timestamp.valueOf(LocalDateTime.of(val1, LocalTime.of(0, 0, 0,0)))
                , Timestamp.valueOf(LocalDateTime.of(val2, LocalTime.of(23, 59, 29,999999999))));
    }

    public static Boolean isBetweenAp(LocalDate day, LocalDate val1, LocalDate val2){
        return isBetweenAp(Timestamp.valueOf(LocalDateTime.of(day, LocalTime.of(0, 0, 0,0)))
                , Timestamp.valueOf(LocalDateTime.of(val1, LocalTime.of(0, 0, 0,0)))
                , Timestamp.valueOf(LocalDateTime.of(val2, LocalTime.of(23, 59, 29,999999999))));
    }

    public static Boolean isBetweenAp(LocalDateTime val1, LocalDateTime val2){
        return isBetweenAp(Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(val1), Timestamp.valueOf(val2));
    }

    public static Boolean isBetweenAp(LocalDateTime day, LocalDateTime val1, LocalDateTime val2){
        return isBetweenAp(Timestamp.valueOf(day), Timestamp.valueOf(val1), Timestamp.valueOf(val2));
    }


    public static Boolean isBetweenAp(Timestamp val1, Timestamp val2){
        return isBetweenAp(Timestamp.valueOf(LocalDateTime.now()), val1, val2);
    }

    public static Boolean isBetweenAp(Timestamp day, Timestamp val1, Timestamp val2){
        if((val1.before(day) || val1.equals(day)) && (day.before(val2) || day.equals(val2))){
            return true;
        }else{
            return false;
        }
    }



    public static String convTsToStr8(Timestamp dt){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(dt);
    }

    public static LocalDate convStringToLocalDate(String dayStr){
        if(dayStr == null || dayStr.length() < 8){
            return null;
        }

        dayStr = dayStr.trim().replaceAll("-", "").replaceAll(":", "");
        return LocalDate.of(Integer.parseInt(dayStr.substring(0, 4))
                , Integer.parseInt(dayStr.substring(4, 6))
                , Integer.parseInt(dayStr.substring(6, 8)));
    }

    public static String convLocalDateToString(LocalDate ld){
        return ld.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    // BASIC_ISO_DATE : 20210902
    public static String convLocalDateTimeToString(LocalDateTime dt){
        return dt.toLocalDate().format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    public static LocalDate convLocalDateTimeToLocalDate(LocalDateTime dayDt){
        if(dayDt == null){
            return null;
        }

        return dayDt.toLocalDate();
    }

    // day : yyyymmdd, time hhmmssSSS
    public static LocalDateTime convStringToLocalDateTime(String day, String time){
        if(VdUtil.isEmpty(day) || VdUtil.isEmpty(time)){
            return null;
        }

        LocalDate lt = LocalDate.of(Integer.parseInt(day.substring(0, 4))
                , Integer.parseInt(day.substring(4, 6))
                , Integer.parseInt(day.substring(6, 8)));
        int hour = Integer.parseInt(time.substring(0, 2));
        int minute = Integer.parseInt(time.substring(2, 4));
        int second = Integer.parseInt(time.substring(4, 6));
        int milliSecond = Integer.parseInt(time.substring(6, time.length()));
        LocalDateTime dt = LocalDateTime.of(lt, LocalTime.of(hour, minute, second, milliSecond));  // .plusHours(AlbeeConst.timeZoneOffset);
        return dt;
    }

    // day : yyyymmdd, time hhmmssSSS
    public static Timestamp convStringToTimestamp(String day){
        if(VdUtil.isEmpty(day)){
            return null;
        }

        LocalDate lt = LocalDate.of(Integer.parseInt(day.substring(0, 4))
                , Integer.parseInt(day.substring(4, 6))
                , Integer.parseInt(day.substring(6, 8)));
        int hour = 0;
        int minute = 0;
        int second = 0;
        int milliSecond = 0;
        LocalDateTime dt = LocalDateTime.of(lt, LocalTime.of(hour, minute, second, milliSecond));  // .plusHours(AlbeeConst.timeZoneOffset);
        return Timestamp.valueOf(dt);
    }

    // day : yyyymmdd, time hhmmssSSS
    public static Timestamp convStringToTimestamp(String day, Integer timeZoneOffset){
        if(VdUtil.isEmpty(day)){
            return null;
        }

        LocalDate lt = LocalDate.of(Integer.parseInt(day.substring(0, 4))
                , Integer.parseInt(day.substring(4, 6))
                , Integer.parseInt(day.substring(6, 8)));
        int hour = 0;
        int minute = 0;
        int second = 0;
        int milliSecond = 0;
        LocalDateTime dt = LocalDateTime.of(lt, LocalTime.of(hour, minute, second, milliSecond)).plusHours(timeZoneOffset);  // .plusHours(AlbeeConst.timeZoneOffset);
        return Timestamp.valueOf(dt);
    }

    // day : yyyymmdd, time hhmmssSSS
    public static Timestamp convStringToTimestampForStart(String day){
        if(VdUtil.isEmpty(day)){
            return null;
        }

        LocalDate lt = LocalDate.of(Integer.parseInt(day.substring(0, 4))
                , Integer.parseInt(day.substring(4, 6))
                , Integer.parseInt(day.substring(6, 8)));
        int hour = 0;
        int minute = 0;
        int second = 0;
        int milliSecond = 0;
        // LocalDateTime dt = LocalDateTime.of(lt, LocalTime.of(hour, minute, second, milliSecond));
        LocalDateTime dt = LocalDateTime.of(lt, LocalTime.of(hour, minute, second, milliSecond)).plusHours(AlbeeConst.timeZoneOffset);
        return Timestamp.valueOf(dt);
    }

    // day : yyyymmdd, time hhmmssSSS
    public static Timestamp convStringToTimestampForEnd(String day){
        if(VdUtil.isEmpty(day)){
            return null;
        }

        LocalDateTime dt = LocalDateTime.of(Integer.parseInt(day.substring(0, 4))
                        ,Integer.parseInt(day.substring(4, 6))
                        ,Integer.parseInt(day.substring(6, 8))
                        ,23, 59, 59, 999999999).plusHours(AlbeeConst.timeZoneOffset);
        return Timestamp.valueOf(dt);
    }

    public static LocalDate convDateToLocalDate(Date dayDt){
        if(dayDt == null){
            return null;
        }

        String dt = dayDt.toString();
        LocalDate lt = LocalDate.of(Integer.parseInt(dt.substring(0, 4))
                , Integer.parseInt(dt.substring(4, 6))
                , Integer.parseInt(dt.substring(6, 8)));
        return lt;
    }

    /*
        +:val1 이 미래, -:val2 가 미래, 0: 같은날
     */
    public static Long getDiffDays(String val1, String val2){
        if(val1 == null || val1.trim().replaceAll("-", "").length() != 8
            || val2 == null || val2.trim().replaceAll("-", "").length() != 8){
            return null;
        }

        val1 = val1.trim().replaceAll("-", "");
        val2 = val2.trim().replaceAll("-", "");

        LocalDate day1 = LocalDate.of(Integer.parseInt(val1.substring(0, 4))
                , Integer.parseInt(val1.substring(4, 6))
                , Integer.parseInt(val1.substring(6, 8)));

        LocalDate day2 = LocalDate.of(Integer.parseInt(val2.substring(0, 4))
                , Integer.parseInt(val2.substring(4, 6))
                , Integer.parseInt(val2.substring(6, 8)));

        return day2.until(day1, ChronoUnit.DAYS);
    }

    public static Long getDiffDays(LocalDateTime val1, LocalDateTime val2){
        if(val1 == null || val2 == null){
            return null;
        }

        LocalDate day1 = val1.toLocalDate();
        LocalDate day2 = val2.toLocalDate();

        return day2.until(day1, ChronoUnit.DAYS);
    }

    public static Long getDiffDays(Timestamp val1, Timestamp val2){
        if(val1 == null || val2 == null){
            return null;
        }

        LocalDate day1 = val1.toLocalDateTime().toLocalDate();
        LocalDate day2 = val2.toLocalDateTime().toLocalDate();

        return day2.until(day1, ChronoUnit.DAYS);
    }
}
