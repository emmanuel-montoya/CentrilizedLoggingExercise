import java.util.regex.Pattern;

public final class Constants {

    public Constants(){}

    public static final boolean OVERWRITE_LOGS = false;
    public static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final int DATE_PATTERN_LENGTH = 20;
    public static final int NUMBER_OF_SERVERS = 50;
    public static final int LOG_ENTRIES_TO_CREATE = 1000;
    public static final int SLEEP_TIME_BETWEEN_TIMESTAMPS = 30;

    public static final String SERVER_NAME = "server-";
    public static final String LOG_EXTENSION = ".log";
    public static final String LOGS_PATH = "temp/";
    public static final String LOG_SERVERS_MERGED_FILE_NAME = "log-servers-merged";

    public static final String[] URL = {"/wp-amin", "/wp-content", "/wp-includes", "/index.php", "/readme.html", "/robots.txt", "/wp-cron.php"};
    public static final int[] RESPONSE_CODES = {200,403,404,500,503};
    public static final String[] METHODS = {"GET","POST","PUT","DELETE"};
    public static final String[] REFERERS = {"https://google.ru/", "https://yandex.ru/", "https://rkn.gov.ru/"};
    public static final String[] USER_AGENTS = {
            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)",
            "Mozilla/5.0 (X11; Linux i686) AppleWebKit/534.24 (KHTML, like Gecko) Chrome/11.0.696.50 Safari/534.24",
            "Mozilla/5.0 (X11; Linux x86_64; rv:6.0a1) Gecko/20110421 Firefox/6.0a1"
    };
}
