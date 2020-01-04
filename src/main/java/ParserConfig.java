
import parsers.HeadlessFullPageParser;
import parsers.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class ParserConfig {

    private static String journal;
    private static LocalDate dateStart;
    private static LocalDate dateEnd;

    private static Path saveDir;
    private static Parser parser;
    private static int quantity = -1;

    public static void setJournal(String j) {
        journal = j;
    }

    public static void setPeriod(String start, String end){
        //dateStart = start;
        //dateEnd = end;
    }

    public static void setSaveDir(Path saveDir) {
        try {
            Files.createDirectories(saveDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ParserConfig.saveDir = saveDir;
    }

    public static void setWantedResult(int i) {
        switch (i) {
            case 0: parser = new HeadlessFullPageParser(saveDir); return;
        }
    }

    public static int requestSize(){
        return parser.getSize();
    }

    public static void setQuantity(int q) {
        quantity = q;
    }

    public static void requestParse(){
        parser.setPeriod(dateStart,dateEnd);
        parser.setQuantity(quantity);
        List<String> files = parser.parse(journal);
    }


}
