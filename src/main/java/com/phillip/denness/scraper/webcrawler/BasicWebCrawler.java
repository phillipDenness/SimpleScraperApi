package com.phillip.denness.scraper.webcrawler;

import com.phillip.denness.scraper.domain.Scrape;
import com.phillip.denness.scraper.domain.Searchterms;
import com.phillip.denness.scraper.domain.Tag;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Service
public class BasicWebCrawler {
    private HashSet<Scrape> scrapes;
    private Searchterms searchterms;

    public Set<Scrape> doScrape(Searchterms searchterms) {
        scrapes = new HashSet<>();
        this.searchterms = searchterms;

        this.searchterms.getTags().forEach(tag -> {
            if(tag.equals(Tag.href.toString())) {
                getPageElements("a[href]");
            }else if(tag.equals(Tag.div.toString())) {
                getPageElements("div");
            }
        });
        return scrapes;
    }

    private void getPageElements(String tag) {
        try {
            Document document = Jsoup.connect(searchterms.getDomain()).get();
            Elements elements = document.select(tag);

            elements.forEach( element -> {
                        if (isMatch(element)) {
                            addScrape(element);
                        }
                    });

        } catch (IOException e) {
            System.err.println("For '" + searchterms.getDomain() + "': " + e.getMessage());
        }
    }

    private boolean isMatch(Element element) {
        return this.searchterms.getKeywords()
            .stream().filter(keyword -> element.text().toLowerCase().contains(keyword.toLowerCase()))
            .toArray().length > 0;
    }

    private void addScrape(Element element) {
        Scrape scrape = new Scrape();
        scrape.setTag(Tag.href);
        scrape.setText(element.text());
        scrapes.add(scrape);
    }
}
