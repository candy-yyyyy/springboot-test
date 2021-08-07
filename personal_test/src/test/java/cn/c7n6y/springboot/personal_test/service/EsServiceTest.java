package cn.c7n6y.springboot.personal_test.service;

import cn.c7n6y.springboot.personal_test.pojo.Content;
import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EsServiceTest {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void getWebPageData() throws IOException {
        String keyWord = "java";
        // 抓取页面
        String url = "https://search.jd.com/Search?keyword=" + keyWord + "&enc=utf-8";
        Document document = Jsoup.connect(url)
                .userAgent("Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)")
                .get();
        Element elementById = document.getElementById("J_goodsList");
        Elements li = elementById.getElementsByTag("li");
        ArrayList<Content> contentList = new ArrayList<>();
        for (Element item : li) {
            String title = item.getElementsByClass("p-name").eq(0).text();
            if (StringUtils.isEmpty(title)) {
                continue;
            }
            String price = item.getElementsByClass("p-price").eq(0).text();
            String pic = item.getElementsByTag("img").eq(0).attr("src");
            contentList.add(new Content(price, title, pic));
        }

        // 写入es
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout(TimeValue.timeValueSeconds(2));
        contentList.forEach(content -> {
            bulkRequest.add(
                    new IndexRequest("j_goodslist")
                            .source(JSON.toJSONString(content), XContentType.JSON)
            );
        });
        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(String.valueOf(!bulkResponse.hasFailures()));
    }
}