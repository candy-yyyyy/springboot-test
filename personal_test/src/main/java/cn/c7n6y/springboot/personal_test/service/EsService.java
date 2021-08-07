package cn.c7n6y.springboot.personal_test.service;

import cn.c7n6y.springboot.personal_test.pojo.Content;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EsService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public String getWebPageData(String keyWord) throws IOException {
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
        return String.valueOf(!bulkResponse.hasFailures());
    }

    public JSONObject searchDataByKeyword(String keyWord, Integer pageNo, Integer pageSize) throws IOException {
        JSONObject rspJson = new JSONObject();
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        Integer from = (pageNo - 1) * pageSize;
        sourceBuilder.from(from);
        sourceBuilder.size(pageSize);
        sourceBuilder.query(QueryBuilders.termQuery("title", keyWord));
//        sourceBuilder.query(QueryBuilders.fuzzyQuery("title", keyWord));
//        sourceBuilder.query(QueryBuilders.wildcardQuery("title", "*" + keyWord + "*"));
//        sourceBuilder.query(QueryBuilders.matchQuery("title", keyWord));
        sourceBuilder.timeout(TimeValue.timeValueSeconds(60));

        // 高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.requireFieldMatch(true);
        highlightBuilder.field("title");
        highlightBuilder.preTags("<span style='color: red;'>");
        highlightBuilder.preTags("<span style='color: red;'>");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);

        searchRequest.indices("j_goodslist");
        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        /*for (SearchHit hit : searchResponse.getHits().getHits()) {
            System.out.println(hit.getSourceAsString());
        }*/
        TotalHits totalHits = searchResponse.getHits().getTotalHits();
        rspJson.put("total", totalHits.value);
        SearchHit[] hits = searchResponse.getHits().getHits();
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (SearchHit hit : hits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField title = highlightFields.get("title");
            if (title != null) {
                Text[] fragments = title.fragments();
                String new_text = "";
                for (Text text : fragments) {
                    System.out.println(text);
                    new_text += text;
                }
                sourceAsMap.put("title", new_text);
            }
            dataList.add(sourceAsMap);
        }
        rspJson.put("data", dataList);
        return rspJson;
    }
}
