package com.app.qqwpick.util;

/**
 * Created by 13621 on 2018/5/30.
 */


import android.annotation.SuppressLint;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

public class DateUtils {

    /**
     * 掉此方法输入所要转换的时间输入例如（"2016年6月16日17时24分24秒"）返回时间戳
     *
     * @param time
     * @return
     */
    public String data(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒",
                Locale.CHINA);
        Date date;
        String times = null;
        try {
            date = sdr.parse(time);
            long l = date.getTime();
            String stf = String.valueOf(l);
            times = stf.substring(0, 10);
//                    LogUtils.d("--444444---", times);
        } catch (Exception e) {
        }
        return times;
    }


    public static Integer StringToTimestamp(String time) {

        int times = 0;
        try {
            times = (int) ((Timestamp.valueOf(time).getTime()) / 1000);
        } catch (Exception e) {
        }
        if (times == 0) {
            System.out.println("String转10位时间戳失败");
        }
        return times;

    }

    /**
     * 计算两个时间的差
     */
    public static Long computingTimeDifference(String time1, String time2) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        try {
            Date date1 = format1.parse(time1);
            Date date2 = format2.parse(time2);
            long time3 = date1.getTime();
            long time4 = date2.getTime();
            return time3 - time4;
        } catch (Exception e) {
            return 0L;
        }
    }

    public static int calculateTime(String deliveryTime) {
        String timeNow = DateUtils.getCurrentTime1();
        Long aLong = DateUtils.computingTimeDifference(deliveryTime, timeNow);
        return DateUtils.longToMin(aLong);
    }

    /**
     * 时间差转换为文字
     */
    public static long translateTimeUtil(Long time) {
        return time / 60000;
    }

    /**
     * 计算两个日期之间间隔天数
     *
     * @param d1 日期1
     * @return 相差天数
     */
    public static double intervalBetweenTwoDate(String d1) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        try {
            Date date1 = format1.parse(d1);
            Date date2 = new Date();
            String format = format2.format(date2);
            Date date3 = format3.parse(format);
            return new BigDecimal(date1.getTime() - date3.getTime())
                    .divide(new BigDecimal(1000 * 3600 * 24), 2, RoundingMode.CEILING)
                    .doubleValue();
        } catch (Exception e) {
            return 0;
        }
    }

    //算两个日期间隔多少天
    public static int getDatePoor(String d2) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = format.parse(getCurrentTime2());
            Date date2 = format.parse(d2);
            int a = (int) ((date1.getTime() - date2.getTime()) / (1000 * 3600 * 24));
            return a;
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * 掉此方法输入所要转换的时间输入例如（"2016-6-16 17:23:51"）返回时间戳
     *
     * @param time
     * @return
     */
    public static String dataOne(String time) {
        if (time.contains("T")) {
            time = time.replace("T", " ");
        }
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss",
                Locale.CHINA);
        Date date;
        String times = null;
        try {
            date = sdr.parse(time);
            long l = date.getTime();
            String stf = String.valueOf(l);
            times = stf.substring(0, 10);
//                    LogUtils.d("--444444---", times);
        } catch (Exception e) {
        }
        return times;
    }


    public static String getTimestamp(String time, String type) {
        SimpleDateFormat sdr = new SimpleDateFormat(type, Locale.CHINA);
        Date date;
        String times = null;
        try {
            date = sdr.parse(time);
            long l = date.getTime();
            String stf = String.valueOf(l);
            times = stf.substring(0, 10);
//                    LogUtils.d("--444444---", times);
        } catch (Exception e) {
        }
        return times;
    }

    @SuppressLint("SimpleDateFormat")
    public static String paseSeckillDate(String strDate) {

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            DateFormat dateFormat = new SimpleDateFormat("HH:mm");
            Date date = format.parse(strDate);
            return dateFormat.format(date);
        } catch (Exception e) {
        }
        return "";
    }

    @SuppressLint("SimpleDateFormat")
    public static String paseMonSeckillDate(String strDate) {

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            DateFormat dateFormat = new SimpleDateFormat("MM-dd");
            Date date = format.parse(strDate);
            return dateFormat.format(date);
        } catch (Exception e) {
        }
        return "";
    }

    @SuppressLint("SimpleDateFormat")
    public static String paseMonHourSeckillsDate(String strDate) {

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            DateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
            Date date = format.parse(strDate);
            return dateFormat.format(date);
        } catch (Exception e) {
        }
        return "";
    }

    @SuppressLint("SimpleDateFormat")
    public static String paseSeckillsDate(String strDate) {

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = format.parse(strDate);
            return dateFormat.format(date);
        } catch (Exception e) {
        }
        return "";
    }


    /**
     * 调用此方法输入所要转换的时间戳输入例如（1466068914）输出（"2016年6月16日 17时21分54秒"）
     *
     * @param time
     * @return
     */
    public String times(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;


    }

    /**
     * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2016年6月16日 17时21分54秒"）
     *
     * @param time
     * @return
     */
    public String timet(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分");
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;

    }

    /**
     * 输入时间戳转为日期
     *
     * @param time
     * @return
     */
    public static String toDate(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日");
        long lcc = Long.valueOf(time);
//     int i = Integer.parseInt(time);
        int i = (int) (lcc);
        String times = sdr.format(new Date(lcc));
        return times;

    }

    @SuppressLint("SimpleDateFormat")
    // 调用此方法输入所要转换的时间戳例如（1402733340）输出（"2016年6月16日 17时21分54秒"）
    public static String times(long timeStamp) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日  #  HH:mm");
        return sdr.format(new Date(timeStamp)).replaceAll("#",
                getWeek(timeStamp));
    }


    private static String getWeek(long timeStamp) {
        int mydate = 0;
        String week = null;
        Calendar cd = Calendar.getInstance();
        cd.setTime(new Date(timeStamp));
        mydate = cd.get(Calendar.DAY_OF_WEEK);
        // 获取指定日期转换成星期几
        if (mydate == 1) {
            week = "周日";
        } else if (mydate == 2) {
            week = "周一";
        } else if (mydate == 3) {
            week = "周二";
        } else if (mydate == 4) {
            week = "周三";
        } else if (mydate == 5) {
            week = "周四";
        } else if (mydate == 6) {
            week = "周五";
        } else if (mydate == 7) {
            week = "周六";
        }
        return week;
    }


    // 并用分割符把时间分成时间数组

    /**
     * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（2016-6-16 17:25:59"）
     *
     * @param time
     * @return
     */
    public String timesOne(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;


    }


    /**
     * 并用分割符把时间分成时间数组
     *
     * @param time
     * @return
     */
    public static String[] timestamp(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        String[] fenge = times.split("[年月日时分秒]");
        return fenge;
    }


    /**
     * 根据传递的类型格式化时间
     *
     * @param str
     * @param type 例如：yy-MM-dd
     * @return
     */
    public static String getDateTimeByMillisecond(String str, String type) {


        Date date = new Date(Long.valueOf(str));


        SimpleDateFormat format = new SimpleDateFormat(type);


        String time = format.format(date);


        return time;
    }


    /**
     * 分割符把时间分成时间数组
     *
     * @param time
     * @return
     */
    public String[] division(String time) {


        String[] fenge = time.split("[年月日时分秒]");


        return fenge;


    }


    /**
     * 输入时间戳变星期
     *
     * @param time
     * @return
     */
    public static String changeweek(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        long lcc = Long.valueOf(time);
        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        Date date = null;
        int mydate = 0;
        String week = null;
        try {
            date = sdr.parse(times);
            Calendar cd = Calendar.getInstance();
            cd.setTime(date);
            mydate = cd.get(Calendar.DAY_OF_WEEK);
            // 获取指定日期转换成星期几
        } catch (Exception e) {

        }
        if (mydate == 1) {
            week = "星期日";
        } else if (mydate == 2) {
            week = "星期一";
        } else if (mydate == 3) {
            week = "星期二";
        } else if (mydate == 4) {
            week = "星期三";
        } else if (mydate == 5) {
            week = "星期四";
        } else if (mydate == 6) {
            week = "星期五";
        } else if (mydate == 7) {
            week = "星期六";
        }
        return week;


    }


    /**
     * 获取日期和星期　例如：2016-6-16 17:26:16 星期四
     *
     * @param time
     * @param type
     * @return
     */
    public static String getDateAndWeek(String time, String type) {
        return getDateTimeByMillisecond(time + "000", type) + "  "
                + changeweekOne(time);
    }


    /**
     * 输入时间戳变星期
     *
     * @param time
     * @return
     */
    public static String changeweekOne(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        long lcc = Long.valueOf(time);
        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        Date date = null;
        int mydate = 0;
        String week = null;
        try {
            date = sdr.parse(times);
            Calendar cd = Calendar.getInstance();
            cd.setTime(date);
            mydate = cd.get(Calendar.DAY_OF_WEEK);
            // 获取指定日期转换成星期几
        } catch (Exception e) {

        }
        if (mydate == 1) {
            week = "星期日";
        } else if (mydate == 2) {
            week = "星期一";
        } else if (mydate == 3) {
            week = "星期二";
        } else if (mydate == 4) {
            week = "星期三";
        } else if (mydate == 5) {
            week = "星期四";
        } else if (mydate == 6) {
            week = "星期五";
        } else if (mydate == 7) {
            week = "星期六";
        }
        return week;


    }


    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日  HH时mm分");
        return sdf.format(new Date());
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getCurrentTime1() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        return sdf.format(new Date());
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getCurrentTime2() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return sdf.format(new Date());
    }

    /**
     * 获取当天0点时间
     *
     * @return
     */
    public static String getTodayZeroTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return sdf.format(new Date()) + " 00:00:00";
    }

    /**
     * 获取当天23.59时间
     *
     * @return
     */
    public static String getTodayEndTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return sdf.format(new Date()) + " 23:59:59";
    }

    /**
     * 获取当月第一天0点时间
     *
     * @return
     */
    public static String getMonthZeroTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String startDay = df.format(new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(startDay.substring(0, 4)), Integer.parseInt(startDay.substring(5, 7)) - 1, 1);
        String firstDayOfMonth = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        return firstDayOfMonth + " 00:00:00";
    }

    /**
     * 获取当月最后一天23.59时间
     *
     * @return
     */
    public static String getMonthEndTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String startDay = df.format(new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(startDay.substring(0, 4)), Integer.parseInt(startDay.substring(5, 7)), 1);
        //这里将日期值减去一天，从而获取到要求的月份最后一天
        calendar.add(Calendar.DATE, -1);
        String lastDayOfMonth = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        return lastDayOfMonth + " 23:59:59";
    }

    /**
     * 获取前一天零点的时间
     *
     * @return
     */
    public static String getYesterdayTime() {
        DateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//格式化一下时间

        Date dNow = new Date(); //当前时间

        Date dBefore = new Date();

        Calendar calendar = Calendar.getInstance(); //得到日历

        calendar.setTime(dNow);//把当前时间赋给日历

        calendar.add(Calendar.DAY_OF_MONTH, -1); //设置为前一天

        dBefore = calendar.getTime(); //得到前一天的时间

        String defaultStartDate = dateFmt.format(dBefore); //格式化前一天

        defaultStartDate = defaultStartDate.substring(0, 10) + " 00:00:00";

//        String defaultEndDate = defaultStartDate.substring(0,10)+" 23:59:59";
        return defaultStartDate;
    }

    /**
     * 输入日期如（2016年6月16日17时26分52秒）返回（星期数）
     *
     * @param time
     * @return
     */
    public String week(String time) {
        Date date = null;
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        int mydate = 0;
        String week = null;
        try {
            date = sdr.parse(time);
            Calendar cd = Calendar.getInstance();
            cd.setTime(date);
            mydate = cd.get(Calendar.DAY_OF_WEEK);
            // 获取指定日期转换成星期几
        } catch (Exception e) {

        }
        if (mydate == 1) {
            week = "星期日";
        } else if (mydate == 2) {
            week = "星期一";
        } else if (mydate == 3) {
            week = "星期二";
        } else if (mydate == 4) {
            week = "星期三";
        } else if (mydate == 5) {
            week = "星期四";
        } else if (mydate == 6) {
            week = "星期五";
        } else if (mydate == 7) {
            week = "星期六";
        }
        return week;
    }


    /**
     * 输入日期如（2016-6-16 17:27:19）返回（星期数）
     *
     * @param time
     * @return
     */
    public String weekOne(String time) {
        Date date = null;
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        int mydate = 0;
        String week = null;
        try {
            date = sdr.parse(time);
            Calendar cd = Calendar.getInstance();
            cd.setTime(date);
            mydate = cd.get(Calendar.DAY_OF_WEEK);
            // 获取指定日期转换成星期几
        } catch (Exception e) {
        }
        if (mydate == 1) {
            week = "星期日";
        } else if (mydate == 2) {
            week = "星期一";
        } else if (mydate == 3) {
            week = "星期二";
        } else if (mydate == 4) {
            week = "星期三";
        } else if (mydate == 5) {
            week = "星期四";
        } else if (mydate == 6) {
            week = "星期五";
        } else if (mydate == 7) {
            week = "星期六";
        }
        return week;
    }

    //获取当前时间前几天的时间
    public static String getDateByDays(int days) {
        try {
            Calendar calendar1 = Calendar.getInstance();
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            calendar1.add(Calendar.DATE, days);
            String days_ago = sdf1.format(calendar1.getTime());
            return days_ago;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 判断是否是同一天
     *
     * @param dt1
     * @param dt2
     * @return
     */
    public static boolean isSameDay(String dt1, String dt2) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        String dateStr = "2019-01-03 10:59:27";
        try {
            Date date = simpleDateFormat.parse(dt1);
            Date date2 = simpleDateFormat.parse(dt2);
            String strDate1 = simpleDateFormat.format(date);
            String strDate2 = simpleDateFormat.format(date2);
            return strDate1.equalsIgnoreCase(strDate2);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return false;
    }

    /**
     * 判断是否是同一天 同一小时 同一分钟
     *
     * @param dt1
     * @param dt2
     * @return
     */
    public static boolean isSameHour(String dt1, String dt2) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        String dateStr = "2019-01-03 10:59:27";
        try {
            Date date = simpleDateFormat.parse(dt1);
            Date date2 = simpleDateFormat.parse(dt2);
            String strDate1 = simpleDateFormat.format(date);
            String strDate2 = simpleDateFormat.format(date2);
            return strDate1.equalsIgnoreCase(strDate2);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return false;
    }

    /**
     * 获取年月日时分
     *
     * @param str
     * @return
     */
    public static String getDateTime(String str) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date = simpleDateFormat.parse(str);
            String d = simpleDateFormat.format(date);
            return d;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取月日时分
     *
     * @param str
     * @return
     */
    public static String getDateTimeMdsf(String str) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date = simpleDateFormat.parse(str);
            String d = simpleDateFormat.format(date);
            return d.substring(d.indexOf("-") + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取月日
     *
     * @param str
     * @return
     */
    public static String getDateTimeMd(String str) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = simpleDateFormat.parse(str);
            String d = simpleDateFormat.format(date);
            return d.substring(d.indexOf("-") + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取年月日
     *
     * @param str
     * @return
     */
    public static String getDateTimeDay(String str) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = simpleDateFormat.parse(str);
            String d = simpleDateFormat.format(date);
            return d;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取时分
     *
     * @param str
     * @return
     */
    public static String getTime(String str) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date = simpleDateFormat.parse(str);
            String d = simpleDateFormat.format(date);
            return d.substring(d.indexOf(" ") + 1);
//            return simpleDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取月日
     *
     * @return
     */
    public static String getDateTimeMd() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = simpleDateFormat.parse(getCurrentTime2());
            String d = simpleDateFormat.format(date);
            d = d.substring(d.indexOf("-") + 1);
            return d.replaceAll("-", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将String转化成date
     *
     * @throws ParseException
     */
    public static Date pStringToDate(String str, String sfgs)
            throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat(sfgs);
        return sf.parse(str);
    }


    public static boolean isTheSameDay(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR))
                && (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH))
                && (c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH));
    }

    public static int longToMin(long time) {
        BigDecimal divide = new BigDecimal(time).divide(new BigDecimal(60000), 0, BigDecimal.ROUND_DOWN);
        return divide.intValue();
    }

    /**
     * 通过开始时间和间隔获得结束时间。
     *
     * @param start
     * @param span
     * @return String
     */
    public static String getEndTime(String start, int span) {
        SimpleDateFormat datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (start.isEmpty() || span == 0) {
            return null;
        }
        long temp = getDateFromStr(start);
        temp += span * 60L * 1000L;
        return datetime.format(new Date(temp));
    }

    /**
     * 通过字符串获得long型时间
     *
     * @param dateStr
     * @return long
     */
    public static long getDateFromStr(String dateStr) {
        SimpleDateFormat datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long temp = 0L;
        Date date = null;
        try {
            date = datetime.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
            return temp;
        }
        temp = date.getTime();
        return temp;
    }
}

