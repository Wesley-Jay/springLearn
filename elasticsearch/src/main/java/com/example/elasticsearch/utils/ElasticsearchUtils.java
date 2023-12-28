package com.example.elasticsearch.utils;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.analysis.Analyzer;
import co.elastic.clients.elasticsearch._types.mapping.DynamicMapping;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import co.elastic.clients.util.ObjectBuilder;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author wsj
 * @description ES相关操作
 * @date 2023/11/9
 */
@Slf4j
@Component
public class ElasticsearchUtils {

    @Autowired
    private ElasticsearchClient client;

    private final  String  indexName = "knowledge_info";

    /**
     * 判断索引是否存在
     *
     * @param indexName 索引名
     * @return 正确与否
     * @throws IOException 错
     */
    public boolean hasIndex(String indexName) throws IOException {
        BooleanResponse exists = client.indices().exists(d -> d.index(indexName));
        return exists.value();
    }

    /**
     * 删除索引
     *
     * @param indexName 索引名
     * @throws IOException 错
     */
    public void deleteIndex(String indexName) throws IOException {
        DeleteIndexResponse response = client.indices().delete(d -> d.index(indexName));
    }

    /**
     * 创建索引
     *
     * @param indexName 索引名
     * @throws IOException 错
     */
    public void createIndex(String indexName) throws IOException {
        CreateIndexResponse indexResponse = client.indices().create(c -> c.index(indexName));
    }

    /**
     * 创建索引，不允许外部直接调用
     *
     * @param indexName 索引名
     * @param mapping 映射
     * @throws IOException 错
     */
    private void createIndex(String indexName, Map<String, Property> mapping) throws IOException {
        CreateIndexResponse createIndexResponse = client.indices().create(c -> {
            c.index(indexName).mappings(mappings -> mappings.properties(mapping));
            return c;
        });
    }


    /**
     * 新增数据
     *
     * @param indexName 索引
     * @throws IOException 错
     */
    public void insertDocument(String indexName, Object obj, String id) throws IOException {
        client.index(i -> i.index(indexName).id(id).document(obj));

    }


    /**
     * 删除数据
     *
     * @param indexName 索引
     * @param id docId
     */
    public void deleteDocument(String indexName, String id) throws IOException {
        client.delete(d -> d.index(indexName).id(id));
    }


    public List<JSONObject> search(String indexName, String query, int top) {
        List<JSONObject> documentParagraphs = new ArrayList<>();
        try {
            SearchResponse<JSONObject> search = client.search(s -> s
                            .index(indexName)
                            .query(q -> q
                                    .match(t -> t
                                            .field("name")
                                            .query(query)
                                    ))
                            .from(0)
                            .size(top)
//                    .sort(f -> f.field(o -> o.field("docId").order(SortOrder.Desc)))
                    , JSONObject.class
            );
            for (Hit<JSONObject> hit : search.hits().hits()) {
                JSONObject pd = hit.source();
                documentParagraphs.add(pd);
            }
        } catch (IOException e) {
            throw new RuntimeException( "查询ES数据失败" + e.getMessage());
        }
        return documentParagraphs;
    }

    public void createIndex(String name,
                            Function<IndexSettings.Builder, ObjectBuilder<IndexSettings>> settingFn,
                            Function<TypeMapping.Builder, ObjectBuilder<TypeMapping>> mappingFn) throws IOException {
        CreateIndexResponse response = client
                .indices()
                .create(c -> c
                        .index(name)
                        .settings(settingFn)
                        .mappings(mappingFn)
                );
        log.info("createIndex方法，acknowledged={}", response.acknowledged());
    }


    public JSONArray retrieval(SearchEntity search) {
        JSONArray array = new JSONArray();
        try {
            SearchRequest.Builder builder = new SearchRequest.Builder();
            builder.index(indexName);
            this.assembleTheQuery(builder, search);
            Function<SearchRequest.Builder, ObjectBuilder<SearchRequest>> function = b -> builder;
            SearchResponse<JSONObject> searchResponse = client.search(function, JSONObject.class );
            for (Hit<JSONObject> hit : searchResponse.hits().hits()) {
                JSONObject info = hit.source();
                if (Objects.isNull(info)) {
                    continue;
                }
                Map<String, List<String>> highlight = hit.highlight();
                if ( highlight.size() > 0) {
                    List<String> title = highlight.get("title");
                    if (title != null && title.size() > 0) {
                        info.put("title",String.join(",", title));
                    }
                    List<String> content = highlight.get("content");
                    if (content != null && content.size() > 0) {
                        info.put("content",String.join(",", content));
                    }
                }
                array.add(info);
            }


            //TotalHits totalHits = searchResponse.hits().total();
            //if (totalHits != null) {
            //    rspData.setTotal(totalHits.value());
            //} else {
            //    rspData.setTotal(0);
            //}
            return array;
        } catch (IOException e) {
            throw new RuntimeException( "查询ES数据失败" + e.getMessage());
        } catch (ElasticsearchException e) {
            throw new RuntimeException( "未找到知识数据，请录入");
        }
    }

    private void assembleTheQuery(SearchRequest.Builder builder, SearchEntity search) {
        List<Condition> conditions = search.getConditions().stream()
                .filter(item -> StringUtils.isNotEmpty(item.getValue().toString())).collect(Collectors.toList());
        if (conditions.size() >0) {
            Query must = null;
            List<Query> filterList = new ArrayList<>();
            for (Condition condition :conditions) {
                String fieldName = condition.getField();
                String fieldValue = condition.getValue().toString();
                if (Objects.equals("inputContent", fieldName)) {
                    List<String> fields = Arrays.asList("title", "content");
                    must = MultiMatchQuery.of(m -> m.fields(fields).query(fieldValue))._toQuery();
                } else if (Objects.equals("updateTime", fieldName)) {
                    DateCommonEnum commonEnum = DateCommonEnum.getByCode(fieldValue);
                    Date date = DateTimeUtils.calcDate(new Date(), commonEnum.getNumber(),commonEnum.getUnit());
                    String dateStr = DateTimeUtils.getDateStr(date,1);
                    Query filter = RangeQuery.of(r -> r.field(fieldName).gte(JsonData.of(dateStr)))._toQuery();
                    filterList.add(filter);
                }  else {
                    List<FieldValue> values = new ArrayList<>();
                    String[] param = fieldValue.split(",");
                    for (String id : param) {
                        values.add(FieldValue.of(id));
                    }
                    Query filter = TermsQuery.of(t -> t.field( fieldName)
                            .terms(new TermsQueryField.Builder().value(values).build())
                    )._toQuery();
                    filterList.add(filter);
                }
            }
            if (Objects.nonNull(must)) {
                Map<String , HighlightField> highlight = new HashMap<>(2);
                HighlightField highlightField = HighlightField.of(f -> f.preTags("<span style='color:red'>").postTags("</span>"));
                highlight.put("title",highlightField);
                highlight.put("content",highlightField);
                Query mustMatch = must;
                if (filterList.size() > 0) {
                    builder.query(q -> q.bool(b -> b.must(mustMatch).filter(filterList)))
                            .highlight(h -> h.fields(highlight));
                } else {
                    builder.query(q -> q.bool(b -> b.must(mustMatch)))
                            .highlight(h -> h.fields(highlight));
                }
            } else if (filterList.size() > 0) {
                builder.query(q -> q.bool(b -> b.filter(filterList)));
            }
        }
        if (StringUtils.isNotEmpty(search.getOrderByColumn())) {
            SortOrder order;
            if (Objects.equals("asc",search.getIsAsc())) {
                order = SortOrder.Asc;
            } else {
                order = SortOrder.Desc;
            }
            builder.sort(s ->s.field( o -> o.field(search.getOrderByColumn()).order(order)));
        } else {
            builder.sort(s ->s.field( o -> o.field("id").order(SortOrder.Desc)));
        }
        int pageNum = search.getPageNum() -1 ;
        int pageSize = search.getPageSize();
        builder.from(pageNum * pageSize );
        builder.size(search.getPageSize());
    }

    private  void createIndexAndSetting() throws IOException {
        // 索引名
        Analyzer analyzer = Analyzer.of(builder ->
                builder._custom("my_analyzer","ik_max_word"));
        // 构建setting
        Function<IndexSettings.Builder, ObjectBuilder<IndexSettings>> settingFn = sBuilder -> sBuilder
                .index(iBuilder -> iBuilder
                        .analysis(builder -> builder.analyzer("default.type",analyzer))
                );
        DynamicMapping dynamicMapping = DynamicMapping.Runtime;
        // 索引字段，每个字段都有自己的property
        Property keywordProperty = Property.of(pBuilder -> pBuilder.keyword(builder -> builder.ignoreAbove(256)));
        Property integerProperty = Property.of(pBuilder -> pBuilder.integer(builder -> builder));
        Property floatProperty = Property.of(pBuilder -> pBuilder.float_(builder -> builder));
        Property textProperty = Property.of(pBuilder -> pBuilder.text(builder -> builder));
        Property timeProperty = Property.of(pBuilder -> pBuilder.date(builder -> builder.format("yyyy-MM-dd HH:mm:ss")));

        // 构建mapping
        Function<TypeMapping.Builder, ObjectBuilder<TypeMapping>> mappingFn = mBuilder -> mBuilder
                .properties("id", integerProperty)
                .properties("title", textProperty)
                .properties("description", textProperty)
                .properties("version", keywordProperty)
                .properties("equipmentCode", keywordProperty)
                .properties("source", keywordProperty)
                .properties("contentType", keywordProperty)
                .properties("fileType", keywordProperty)
                .properties("editType", keywordProperty)
                .properties("status", keywordProperty)
                .properties("content", textProperty)
                .properties("articleLink", keywordProperty)
                .properties("fileUrl", keywordProperty)
                .properties("keywords", keywordProperty)
                .properties("score", floatProperty)
                .properties("likeNumber", integerProperty)
                .properties("commentNumber", integerProperty)
                .properties("viewNumber", integerProperty)
                .properties("createBy", keywordProperty)
                .properties("createName", textProperty)
                .properties("updateBy", keywordProperty)
                .properties("updateTime", timeProperty)
                .properties("createTime", timeProperty)
                .properties("rejectReason", keywordProperty)
                .properties("collect", integerProperty)
                .properties("dateStr", keywordProperty)
                .properties("approveBy", keywordProperty)
                .dynamic(dynamicMapping);

        // 创建索引，并指定setting和mapping
        this.createIndex(indexName, settingFn, mappingFn);
    }
}

