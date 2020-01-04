package parsers;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

public class HeadlessFullPageParser implements Parser {
    LocalDate start, stop;
    int quantity; // -1 is for all posts
    int size; // how much post was saved (just statistic)
    Path saveDir;
    int countThreads = 0;

    public HeadlessFullPageParser(Path saveDir){
        this.saveDir = saveDir;
    }

    @Override
    public List<String> parse(String journal) {
        PostLinksCollector p = new PostLinksCollectorYear(start, stop, quantity, journal);
        ArrayDeque<String> posts = new ArrayDeque<>(p.collect());
        size = posts.size();
        final String userName;
        String userName1;
        try {
            userName1 = journal.substring(8, journal.indexOf("."));
        } catch (Exception e) {
            userName1 = "unknown";
        }
        userName = userName1;
        int maxThreads = 5;
        if (maxThreads > quantity) maxThreads = quantity;

        while (posts.size() > 0){

            for (int i = countThreads; i < maxThreads; i++) {
                String n = posts.pollFirst();
                if (n == null) break;
                Thread thread = new Thread(() -> {
                    try {
                        Document doc = Jsoup.connect(n)
                                .data("query", "Java")
                                .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:50.0)")
                                .timeout(5000)
                                .get();

                        doc.select("header").remove();
                        doc.select("div.b-discoverytimes-wrapper").remove();
                        doc.select("head,script,.hidden,style,form").remove();


                            Path imgDir = Paths.get(saveDir +"/img_" + userName +"_"+ n.substring(n.lastIndexOf("/") + 1, n.lastIndexOf(".")));
                            Files.createDirectories(imgDir);

                            int imgName = 0;

                            for (Element img : doc.select("img[src]")){

                                String replacementName = "";
                                try {
                                    URL url = new URL(img.attr("src"));
                                    BufferedImage bi = ImageIO.read(url);
                                    File file = new File(imgDir + "/" + (url.getFile()));
                                    FileUtils.copyURLToFile(url, file);
                                    replacementName = file.getAbsolutePath();
                                } catch(Exception e) {
                                }
                                img.attr("src", replacementName);
                            }



                        File file = new File(saveDir +"/" + userName +"_"+ n.substring(n.lastIndexOf("/") + 1));
                        FileUtils.writeStringToFile(file, doc.html(), "UTF-8");

                    } catch (IOException e) {
                        e.printStackTrace();
                        size = size-1;
                    }
                    countThreads--;
                    System.out.println("Ended. Thread for " + n);
                });

                countThreads++;
                System.out.println("Started. Thread for " + n);
                thread.start();
            }
        }


        return null;
    }

    @Override
    public void setQuantity(int q) {
        this.quantity = q;
    }

    public int getSize() {
        return size;
    }

    @Override
    public void setPeriod(LocalDate start, LocalDate stop) {
        this.start = start;
        this.stop = stop;
    }
}
