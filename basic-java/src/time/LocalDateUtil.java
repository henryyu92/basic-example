package time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * LocalDate、LocalTime、LocalDateTime 分别表示不包含时区的日期、时间、日期和时间
 */
public class LocalDateUtil {

    /**
     * 当前系统时间
     * @return
     */
    public static LocalDateTime currentDateTime(){
        return LocalDateTime.now();
    }

    public static LocalDateTime currentDateTime(ZoneId zoneId){
        return LocalDateTime.now(zoneId);
    }

    public static LocalDate currentDate(){
        LocalDateTime localDateTime = currentDateTime();
        return localDateTime.toLocalDate();
    }
}
