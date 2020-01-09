package parsers;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.List;

public class ImagesOnlyParser implements Parser {
    LocalDate start, stop;
    int quantity; // -1 is for all posts
    int size; // how much post was saved (just statistic)
    Path saveDir;
    int countThreads = 0;

    public ImagesOnlyParser(Path saveDir){
        this.saveDir = saveDir;
    }

    @Override
    public List<String> parse(String journal) {
        PostLinksCollector p = new PostLinksCollectorYear(start, stop, quantity, journal);
        ArrayDeque<String> posts = new ArrayDeque<>(p.collect());
        size = posts.size();

        int maxThreads = 5;
        if (maxThreads > size) maxThreads = size;
        try {
            Files.createDirectories(saveDir);
            while (posts.size() > 0){

                for (int i = countThreads; i < maxThreads; i++) {
                    String n = posts.pollFirst();
                    if (n == null) break;
                    Thread thread = new Thread(() -> {
                        try {
                            Document doc = Jsoup.connect(n)
                                    .data("query", "Java")
                                    .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:50.0)")
                                    .referrer("http://www.google.com")
                                    .timeout(5000)
                                    .get();
                            doc.select("header").remove();
                            doc.select("div.b-discoverytimes-wrapper").remove();
                            doc.select("head,script,.hidden,style,form").remove();

                            Elements imgs = doc.select("img");

                            for (Element img : imgs){
                                try {
                                    URL url = new URL(img.attr("src"));
                                    String imgName = FilenameUtils.getName(url.toString());
                                    if (imgName.contains("?")) imgName = imgName.substring(0, imgName.indexOf("?"));
                                    if (imgName.trim().length() == 0) continue; // I'm not sure that this line will nut skip good images, it needs more tests
                                    File file = new File(saveDir + "/" + imgName);
                                    int imgcount = 1;
                                    while (file.exists()) {
                                        file = new File(saveDir + "/" + imgName + "(" + imgcount + ")"); // save images with same name
                                        imgcount++;
                                    }
                                    FileUtils.copyURLToFile(url, file);
                                } catch(Exception e) {}
                            }

                        } catch (SocketTimeoutException e) {
                            e.printStackTrace();
                            System.out.println("Timeout for " + n);
                            size = size-1;
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println(n);
                            size = size-1;
                        }
                        countThreads--;
                        //System.out.println("Ended. Thread for " + n);
                    });

                    countThreads++;
                    //System.out.println("Started. Thread for " + n);
                    thread.start();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unable to create directory");
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
