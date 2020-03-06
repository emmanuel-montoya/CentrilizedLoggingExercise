import java.io.File;
import java.io.IOException;
import java.text.ParseException;

class MainClass {

    public static void main(String[] args)  {

        DummyLogGenerator dummyLogGenerator = new DummyLogGenerator();

        File file = new File(Constants.LOGS_PATH);

        try {
            if(file.list().length == 0 || Constants.OVERWRITE_LOGS){
                dummyLogGenerator.createLogExamples();
            }  else {
                dummyLogGenerator.mergeLogs(Constants.LOGS_PATH);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}