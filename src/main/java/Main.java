import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        ParserConfig.setJournal("https://_replace_this_name_.livejournal.com"); // replace with lj url without last slash!
        ParserConfig.setSaveDir(Paths.get("/home/_replace_this_path_/ljTest/")); // replace with dir where you want to save data
        ParserConfig.setQuantity(4); // how much posts you want to save, -1 for all posts
        ParserConfig.setWantedResult(0); // just don't touch it while now
        ParserConfig.requestParse();
    }
}
