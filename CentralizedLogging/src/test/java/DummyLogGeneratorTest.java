import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class DummyLogGeneratorTest {

    SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_PATTERN);

    @Test
    public void createLogExamples() {

        boolean serverLogExists = false;

        for (int serverId = 0; serverId < Constants.NUMBER_OF_SERVERS; serverId++) {
            try {
                String mergedFileName= "temp/server-"+ serverId + ".log";
                BufferedReader bufferedReader = new BufferedReader(new FileReader(mergedFileName));
                String line = bufferedReader.readLine();
                line.substring(0, 20);
                String date = line.substring(0, 20);
                Date convertedDate = sdf.parse(date);
                serverLogExists=true;
            } catch (FileNotFoundException e) {
                System.out.println("The log for server " + serverId + " was not found ");
            } catch (ParseException e) {
                System.out.println("The file for server " + serverId + " not contains the proper date format");
                fail();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        assertTrue(serverLogExists);
    }

    @Test
    public void mergedFiledExists() {
        String mergedFileName= Constants.LOGS_PATH  + Constants.LOG_SERVERS_MERGED_FILE_NAME + Constants.LOG_EXTENSION;
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(mergedFileName));
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        assertNotNull(bufferedReader);
    }

    @Test
    public void validateLogMergedFile() {

        String mergedFileName= Constants.LOGS_PATH  + Constants.LOG_SERVERS_MERGED_FILE_NAME + Constants.LOG_EXTENSION;
        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(new FileReader(mergedFileName));
            List<String> lineList = bufferedReader.lines().collect(Collectors.toList());
            List<Date> dateList = new ArrayList<>();
            for (String line : lineList) {
                if (line != null) {
                    // 4) Store the date from the 1st line of each file.
                    String date = line.substring(0, Constants.DATE_PATTERN_LENGTH);
                    Date convertedDate = null;
                    try {
                        convertedDate = sdf.parse(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    dateList.add(convertedDate);
                }
            }

            int currentIndex = 0;

            while(currentIndex < dateList.size()){

                Date currentDate = dateList.get(currentIndex);

                for (int i = 0; i < dateList.size(); i++) {

                    if(currentIndex == i) break;
                    Date dateToCompare = dateList.get(i);

                    if (currentDate != null) {
                        if (dateToCompare != null) {
                            if(currentIndex < i){
                                if(currentDate.after(dateToCompare)){
                                    fail();
                                }
                            } else {
                                if(currentDate.before(dateToCompare)){
                                    fail();
                                }
                            }
                        }
                    } else {
                        currentIndex = i;
                    }
                }
                currentIndex++;
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}