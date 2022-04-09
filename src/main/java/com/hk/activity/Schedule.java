package com.hk.activity;

import java.util.Arrays;
import java.util.Calendar;

public class Schedule {
  public long startDate = 0;
  public long endDate = 0;
  public long startTime = 0;
  public long endTime = 0;
  public short startDayOfWeek = 0;
  public short endDayOfWeek = 0;
  public static final long ONE_DAY_MS = 3600*1000*24;
  // 0：连续模式 1：每日模式
  public int mode = 0;

  public static String getPeriodInfo(long startTime, long endTime) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(startTime);
    int wday = calendar.get(Calendar.DAY_OF_WEEK)-1;
    String res = String.format("%d/%d/%d 周%s %d:%d:%d 时长:%s",calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DAY_OF_MONTH),
      wday==0?"日":String.valueOf(wday),calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),calendar.get(Calendar.SECOND),
      getDurationInfo(endTime - startTime));
    return res;
  }

  public static String getDurationInfo(long duration) {
    boolean negative = duration < 0;
    if (negative) {
      duration = -duration;
    }
    String res = "";
    boolean output = false;
    if ((duration%ONE_DAY_MS)>ONE_DAY_MS-3600*1000) {
      res += (duration/ONE_DAY_MS+1)+"天差";
      long gap = ONE_DAY_MS - (duration%ONE_DAY_MS);
      if(gap>=60*1000) {
        res += (gap/(60*1000)) + "分";
        gap %= 60*1000;
        output = true;
      }
      if (gap >= 1000 || output&&gap!=0) {
        res += (gap/1000) + "秒";
        gap %= 1000;
      }
      if (gap != 0 ) {
        res += gap + "毫秒";
      }
      return res;
    }
    if (duration >= ONE_DAY_MS) {
      res += duration / ONE_DAY_MS + "天";
      duration %= ONE_DAY_MS;
      output = true;
    }
    if (duration>=3600000||output&&duration!=0) {
      res += duration / 3600000 + "时";
      duration %= 3600000;
      output = true;
    }
    if (duration>=60000||output&&duration!=0) {
      res += duration / 60000 + "分";
      duration %= 60000;
      output = true;
    }
    if (duration>=1000||output&&duration!=0) {
      res += duration / 1000 + "秒";
      duration %= 1000;
      output = true;
    }
    if (!output || duration != 0) {
      res += duration==0?"0":String.format("%03d",duration);
    }
    if (negative) {
      res = "-" + res;
    }
    return res;
  }

  public static String getClockInfo() {
    String str = clockShiftMs==0?"":"时钟漂移:"+getDurationInfo(clockShiftMs)+" ";
    Calendar calendar = Calendar.getInstance();
    if (clockShiftMs != 0) {
      calendar.setTimeInMillis(calendar.getTimeInMillis()+clockShiftMs);
    }
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    int minute = calendar.get(Calendar.MINUTE);
    int second = calendar.get(Calendar.SECOND);
    int ms = calendar.get(Calendar.MILLISECOND);
    int wday = calendar.get(Calendar.DAY_OF_WEEK);
    str += String.format("%d:%d:%d.%03d %d/%d 周%d",hour,minute,second,ms,month+1,day,wday-1);
    return str;
  }


  public int getStatus(long timestamp) {
    if (timestamp<startDate) return -1;
    if (timestamp>=endDate) return 1;
    return 0;
  }

  public int getWeekStatus(long timestamp) {
    Calendar calendar = Calendar.getInstance();
    if (timestamp != 0) {
      calendar.setTimeInMillis(timestamp);
    } else {
      timestamp = calendar.getTimeInMillis();
      if (clockShiftMs != 0) {
        timestamp += clockShiftMs;
        calendar.setTimeInMillis(timestamp);
      }
    }

    if (startDate != 0 && timestamp < startDate) return -1;
    if (endDate != 0 && timestamp >= endDate) return 1;

    long dayTime = timestamp - getDayStartTimestamp(timestamp);
    int day = calendar.get(Calendar.DAY_OF_WEEK)-1;
    if (day == 0) day = 7;

    if (startDayOfWeek <= endDayOfWeek) {
      if (day < startDayOfWeek || day == startDayOfWeek && dayTime < startTime)
        return -1;
      if (day > endDayOfWeek  || day == endDayOfWeek && dayTime >= endTime)
        return 1;
    } else {
      if ((day < startDayOfWeek || day == startDayOfWeek && dayTime < startTime)
        && (day > endDayOfWeek || day == endDayOfWeek && dayTime >= endTime)) {
        return -1; // 未开始/已结束
      }
    }

    return 0;
  }

  /**
   * 当前或者下一个活动周期的开始时间
   * @return
   */
  public long getPeriodStartTime() {
    if (startDayOfWeek==0&&endDayOfWeek==0) {
      if (mode == 0) {
        return startDate;
      } else {
        long start = getDayStartTimestamp()+startTime;
        return Math.max(start, startDate);
      }
    }
    Calendar calendar = Calendar.getInstance();
    if (clockShiftMs != 0) {
      calendar.setTimeInMillis(calendar.getTimeInMillis() + clockShiftMs);
    }
    long now = calendar.getTimeInMillis();
    if (now >= endDate) {
      return now + ONE_DAY_MS*10;
    }
    int day = calendar.get(Calendar.DAY_OF_WEEK)-1;
    if(day == 0) day = 7;
    if (getWeekStatus(now)==0) {
      int diff = day - startDayOfWeek;
      if (diff<0) diff += 7;
      calendar.add(Calendar.DAY_OF_WEEK,-diff);
    } else {
      int diff = startDayOfWeek - day;
      if (diff < 0) diff +=7;
      calendar.add(Calendar.DAY_OF_MONTH, diff);
    }
    calendar.set(Calendar.HOUR_OF_DAY,0);
    calendar.set(Calendar.MINUTE,0);
    calendar.set(Calendar.SECOND,0);
    calendar.set(Calendar.MILLISECOND,0);
    return calendar.getTimeInMillis() + startTime;

  }
  /**
   * 当前或者下一个活动周期的结束时间
   * @return
   */
  public long getPeriodEndTime() {
    if (startDayOfWeek==0&&endDayOfWeek==0) {
      if (mode == 0) {
        return endDate;
      }
      long timestamp = getDayStartTimestamp()+(endTime<=0?ONE_DAY_MS:endTime);
      return Math.min(timestamp, endDate);
    }
    Calendar calendar = Calendar.getInstance();
    if (clockShiftMs != 0) {
      calendar.setTimeInMillis(calendar.getTimeInMillis() + clockShiftMs);
    }
    long now = calendar.getTimeInMillis();
    if (now >= endDate) {
      return now + ONE_DAY_MS;
    }
    int day = calendar.get(Calendar.DAY_OF_WEEK)-1;
    if(day == 0) day = 7;
    int diff = endDayOfWeek - day;
    if (diff<0) diff += 7;
    calendar.add(Calendar.DAY_OF_WEEK,diff);
    calendar.set(Calendar.HOUR_OF_DAY,0);
    calendar.set(Calendar.MINUTE,0);
    calendar.set(Calendar.SECOND,0);
    calendar.set(Calendar.MILLISECOND,0);

    return calendar.getTimeInMillis() + endTime;
  }

  public static long clockShiftMs=0; //时钟漂移值,ms,用于调试

  public void setFullDayPeriod() {
    startTime = 0;
    endTime = ONE_DAY_MS;
    mode = 1;
  }
  public static long getDayStartTimestamp() {
    Calendar calendar = Calendar.getInstance();
    if (Schedule.clockShiftMs != 0) {
      long timestamp = calendar.getTimeInMillis();
      calendar.setTimeInMillis(timestamp+Schedule.clockShiftMs);
    }
    calendar.set(Calendar.SECOND,0);
    calendar.set(Calendar.MINUTE,0);
    calendar.set(Calendar.HOUR_OF_DAY,0);
    calendar.set(Calendar.MILLISECOND,0);
    return calendar.getTimeInMillis();
  }

  public static Long getDayStartTimestamp(long currTimestamp) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(currTimestamp);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTimeInMillis();
  }

}
