package parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PostLinksCollectorYear implements PostLinksCollector {

    private LocalDate start; //closer to today
    private LocalDate stop; //more far from today
    private int quantity;
    private int countQuantity = 0;
    private String journal;
    private List<String[]> nameAndDate = new ArrayList<>();

    public PostLinksCollectorYear(LocalDate start, LocalDate stop, int quantity, String journal) {
        this.quantity = quantity;
        this.journal = journal;
        if (start != null && stop != null) {
            if (start.compareTo(stop) > 0) {
                this.start = stop;
                this.stop = start;
            }
        }
        if (start != null) {

        }
    }

    @Override
    public List<String> collect() {

        Document doc;
        try {
            doc = Jsoup.connect(journal + "/calendar")
                    .data("query", "Java")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:50.0)")
                    .timeout(5000)
                    .get();
        } catch (IOException e) {
            return null; // it can be replaced with creating different PostLinksCollector with a different algorithm of collecting posts (not via calendar)
        }

            Elements links = doc.select("a[href]");

            List<String> posts = new ArrayList<>();

            // find all available years in calendar (links like 'https://name.livejournal.com/2019/')
            for (Element l : links) {
                String url = l.attr("href");
                if (url != null && url.matches(journal + "/\\d{4}/")) {    //grap a link if it's ended with a year

                    if (stop != null || start != null) {
                        int yearInThisUrl = Integer.parseInt(url.substring(url.length() - 5, url.length() - 1));
                        if (stop != null && yearInThisUrl < stop.getYear()) continue;
                        if (start != null && yearInThisUrl > start.getYear()) continue;
                    }

                    Document pageOfYear;
                    try {
                    pageOfYear = Jsoup.connect(url)
                            .data("query", "Java")
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:50.0)")
                            .timeout(5000)
                            .get();
                    } catch (IOException e) {
                        System.out.println("LOG: Impossible to read from " + url);
                        continue; // go to next year
                    }

                    // inside each year find available months (links like 'https://name.livejournal.com/2019/10/')
                    Elements links1 = pageOfYear.select("a[href]");
                    for (Element m : links1) {
                        String url1 = m.attr("href");
                        if (url1 != null && url1.matches(url + "\\d{2}/")){ //grap a link if it's ended with a month number
                            Document pageOfDay;
                            try {
                                pageOfDay = Jsoup.connect(url1)
                                        .data("query", "Java")
                                        .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:50.0)")
                                        .timeout(5000)
                                        .get();
                            } catch(IOException e) {
                                System.out.println("LOG: Impossible to read from " + url1);
                                continue;
                            }
                            // inside each month find posts
                            Elements links2 = pageOfDay.select("a[href]");

                            String date = null;
                            for (Element n : links2) {
                                String url2 = n.attr("href");
                                if (url2 != null && url2.matches(url1 + "\\d{2}/")){
                                    date = url2.substring(url2.length()-11, url2.length()-1);
                                }
                                if (url2 != null && url2.matches(journal + "/\\d{1,10}[.]html")){  //regex for a post link
                                    String[] s = new String[2];

                                    String title = n.text();
                                    s[0] = title;

                                    s[1] = date;
                                    nameAndDate.add(s);

                                    posts.add(url2);
                                    countQuantity++;
                                    if (quantity != -1 && countQuantity == quantity) return posts;
                                    date = null;
                                }
                            }
                        }
                    }
                }
            }
            return posts;
    }

}
