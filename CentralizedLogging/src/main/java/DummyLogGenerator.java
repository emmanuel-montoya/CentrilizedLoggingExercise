import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class DummyLogGenerator {

    //https://github.com/xeraa/java-logging/blob/master/src/main/java/net/xeraa/logging/LogMe.java

    public static Logger LOGGER = Logger.getLogger(DummyLogGenerator.class.getName());

    public String createLogExamples() throws InterruptedException {

        LOGGER.info("Starting createLogExamples() method");

        String result = "";
        int countline = Constants.LOG_ENTRIES_TO_CREATE;
        HashMap<Integer, String> serverLoggerMap = new HashMap<>();
        Random rand = new Random();

        while (countline != 0) {
            int serverId = rand.nextInt(Constants.NUMBER_OF_SERVERS);

            String method = Constants.METHODS[rand.nextInt(Constants.METHODS.length)];
            String path = Constants.URL[rand.nextInt(Constants.URL.length)];
            String referer = Constants.REFERERS[rand.nextInt(Constants.REFERERS.length)];
            String useragent = Constants.USER_AGENTS[rand.nextInt(Constants.USER_AGENTS.length)];
            int response = Constants.RESPONSE_CODES[rand.nextInt(Constants.RESPONSE_CODES.length)];
            int count = rand.nextInt(65537);
            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat(Constants.DATE_PATTERN); // Quoted "Z" to indicate UTC, no timezone offset
            df.setTimeZone(tz);
            String nowAsISO = df.format(new Date());

            result = nowAsISO + ", Server " + serverId + " " + method + " " + path + " HTTP/1.1\" " + response + " " + count + " " + referer + " " + useragent + " \n";
            LOGGER.info(result);

            if (!serverLoggerMap.containsKey(serverId)) {
                serverLoggerMap.put(serverId, result);
            } else {
                serverLoggerMap.replace(serverId, serverLoggerMap.get(serverId).concat(result));
            }
            countline--;
            Thread.sleep(Constants.SLEEP_TIME_BETWEEN_TIMESTAMPS);
        }

        serverLoggerMap.forEach((key,value) -> {
            try {
                this.writelog(key, value);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        LOGGER.info("Ending createLogExamples() method");
        return result;
    }

    private void writelog(Integer serverId, String  result) throws IOException {
        String fileName = Constants.LOGS_PATH + Constants.SERVER_NAME + serverId + Constants.LOG_EXTENSION;
        FileWriter writer = new FileWriter(fileName, false);
        try {
            writer.write(result);
            writer.flush();
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }


    //https://stackoverflow.com/questions/39073651/java-merging-multiple-log-files-by-date
    public void mergeLogs(String logsPath) throws IOException, ParseException {

        LOGGER.info("Starting mergeLogs() method");

        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_PATTERN);

        //It creates the a file where the logs will be ordered by timestamp
        PrintWriter writer = new PrintWriter(logsPath + Constants.LOG_SERVERS_MERGED_FILE_NAME + Constants.LOG_EXTENSION, "UTF-8");

        List<BufferedReader> readerIndex = new ArrayList<>();
        List<String> lineList = new ArrayList<>();
        List<Date> dateList = new ArrayList<>();

        for (int i = 0; i < Constants.NUMBER_OF_SERVERS;i++) {
            // 2) Create BufferedReader list with respect to the file.
            String fileName = logsPath + Constants.SERVER_NAME + i + Constants.LOG_EXTENSION;

            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
                readerIndex.add(bufferedReader);
                // 3) Read the 1st line of each line and store it in another list.
                String line = bufferedReader.readLine();
                lineList.add(line);
                // 4) Store the date from the 1st line of each file.
                String date = line.substring(0, Constants.DATE_PATTERN_LENGTH);
                Date convertedDate = sdf.parse(date);
                dateList.add(convertedDate);
            } catch (FileNotFoundException e){
                LOGGER.fine("The log located at " + logsPath +  " was not found.");
            } catch (ParseException e ){
                LOGGER .info("The file located at " + logsPath +  " not contains the proper date format.");
            } catch (IOException e ){
                LOGGER.info(e.getMessage());
            }
        }
        int index = readerIndex.size();
        // 5) While BufferedReader's size is not zero then,
        while (index > 0) {
            // 6) Take the index of minimum date from dateList
            int indexMin = minDateIndex(dateList);
            // 7) Get the Line with the index you got in the previous step (lineToWrite).
            String lineToWrite = lineList.get(indexMin);
            writer.println(lineToWrite);
            // 8) Get the buffered reader with the index.
            BufferedReader br1 = readerIndex.get(indexMin);
            if (br1 != null) {
                //  9) If the BR is not null then read the line.
                String line = "";
                try {
                    line = br1.readLine();
                } catch (Exception e) {
                    LOGGER.info(e.getMessage());
                }

                if (line != null) {
                    // 10)If line is not equal to null then remove the lineList from the index and add the line to index.
                    lineList.remove(indexMin);
                    lineList.add(indexMin, line);
                    if (line.length() > Constants.DATE_PATTERN_LENGTH) {
                        // 11)If the line length is greater than 23 (yyyy-MM-dd HH:mm:ss,SSS) then take the first 23 String from the line.
                        String date = line.substring(0, Constants.DATE_PATTERN_LENGTH);
                        //Matcher matcher = Constants.DATE_FORMAT_PATTERN.matcher(date);
                        //if (matcher.matches()) {
                        // 12)Check if the date matches to the pattern, if matches then add the date to dateList
                        try {
                            Date convertedDate = sdf.parse(date);
                            dateList.remove(indexMin);
                            dateList.add(indexMin, convertedDate);
                        } catch (ParseException e){
                            LOGGER.info("DATE FORMAT COULD NOT BE PARSED");
                        }
                    }
                } else {
                    //If line is null then remove the min indexed line from lineList,dateList,BufferedReader list. Do BufferedReader--.
                    lineList.remove(indexMin);
                    dateList.remove(indexMin);
                    readerIndex.remove(indexMin);
                    br1.close();
                    index--;
                }
            }
        }
        writer.close();
        LOGGER.info("Server's logs were merged and ordered by timestamp");
        LOGGER.info("Ending mergeLogs() method");
    }

    private static int minDateIndex(List<Date> dateList) {
        // return index of min date
        Date minDate = dateList.get(0);
        int minIndex = 0;
        for (int i = 1; i < dateList.size(); i++) {
            Date currentDate = dateList.get(i);
            if (minDate != null) {
                if (currentDate != null) {
                   if (minDate.after(currentDate)) {
                        // We have a new min
                        minDate = dateList.get(i);
                        minIndex = i;
                    } else {
                        // we keep current min
                    }
                } else {
                    // we keep current min
                }
            } else {
                minDate = currentDate;
                minIndex = i;
            }
        }
        return minIndex;
    }
}