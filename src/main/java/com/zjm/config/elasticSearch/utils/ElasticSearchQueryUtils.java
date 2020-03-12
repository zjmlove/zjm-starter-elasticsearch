package com.zjm.config.elasticSearch.utils;

import com.google.common.collect.Maps;
import com.zjm.config.elasticSearch.ElasticSearchConfig;
import com.zjm.config.elasticSearch.dto.ElasticQueryDto;
import com.zjm.config.elasticSearch.dto.ElasticQueryOperatorDto;
import com.zjm.config.elasticSearch.dto.ElasticSearchDto;
import com.zjm.config.elasticSearch.dto.SearchAfterResult;
import com.zjm.core.utils.PageBase;
import com.zjm.exception.RRException;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.collapse.CollapseBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import com.zjm.config.elasticSearch.utils.ConstantsUtils.QueryTypeEnum;
import com.zjm.config.elasticSearch.utils.ConstantsUtils.LogicalRelationEnum;
import java.io.IOException;
import java.util.*;

/**
 * @author White Tan
 * @description ES 查询 query 帮助类
 * @date 2020/3/12
 */
@Component
public class ElasticSearchQueryUtils {
	private static final Logger log = LoggerFactory.getLogger(ElasticSearchQueryUtils.class);
	@Autowired
	private RestHighLevelClient restHighLevelClient;
	@Autowired
	private ElasticPerformRequestUtils elasticPerformRequestUtils;
	@Autowired
	private ElasticSearchConfig elasticsearchConfig;

	public ElasticSearchQueryUtils() {
	}

	public List<Map<String, Object>> searchCustomList(SearchRequest searchRequest, boolean needIndex) throws Exception {
		List<Map<String, Object>> mapList = null;
		SearchResponse searchResponse = this.getSearchResponse(this.restHighLevelClient, searchRequest);
		if (searchResponse != null) {
			mapList = this.getResultMapList(searchResponse.getHits().getHits(), needIndex);
		}

		return mapList;
	}

	public SearchResponse searchCustomResponse(SearchRequest searchRequest) throws Exception {
		SearchResponse searchResponse = this.getSearchResponse(this.restHighLevelClient, searchRequest);
		return searchResponse != null ? searchResponse : null;
	}

	public List<String> searchDistinct(ElasticQueryDto queryDto, String field) {
		ElasticSearchDto searchDto = new ElasticSearchDto(queryDto);
		return this.searchDistinct(searchDto, field);
	}

	public SearchResponse searchResponse(ElasticQueryDto queryDto) {
		ElasticSearchDto searchDto = new ElasticSearchDto(queryDto);
		return this.searchResponse(searchDto);
	}

	public List<Map<String, Object>> search(ElasticQueryDto queryDto) {
		ElasticSearchDto searchDto = new ElasticSearchDto(queryDto);
		return this.searchList(searchDto);
	}

	public Long count(ElasticQueryDto queryDto) {
		return this.searchResponse(queryDto).getHits().totalHits;
	}

	public SearchAfterResult searchForSearchAfter(ElasticQueryDto queryDto) {
		if (CollectionUtils.isEmpty(queryDto.getSidx())) {
			throw new RRException("需要设定排序字段");
		} else {
			ElasticSearchDto searchDto = new ElasticSearchDto(queryDto);
			this.searchResponse(searchDto);
			SearchResponse searchResponse = searchDto.getSearchResponse();
			if (searchResponse != null) {
				SearchHits hits = searchResponse.getHits();
				SearchHit[] searchHits = hits.getHits();
				return this.getSearchAfterResult(searchHits);
			} else {
				return null;
			}
		}
	}

	public PageBase searchPage(ElasticQueryDto queryDto) {
		if (queryDto.getQueryPageDto() == null) {
			throw new RRException("QueryPageDto参数不能为空");
		} else {
			ElasticSearchDto searchDto = new ElasticSearchDto(queryDto);
			this.searchList(searchDto);
			long totalHits = 0L;
			if (searchDto.getSearchResponse() != null) {
				totalHits = searchDto.getSearchResponse().getHits().getTotalHits();
			}

			if (!this.elasticsearchConfig.getUseMaxResultWindow()) {
				totalHits = totalHits <= this.elasticsearchConfig.getMaxResultCount() ? totalHits : this.elasticsearchConfig.getMaxResultCount();
			}

			PageBase page = new PageBase(this.searchList(searchDto), totalHits, (long)searchDto.getQueryDto().getQueryPageDto().getLimit(), (long)searchDto.getQueryDto().getQueryPageDto().getCurrPage());
			return page;
		}
	}

	public Map<String, Object> queryById(String indexName, String id) {
		Object map = Maps.newHashMap();

		try {
			GetRequest getRequest = new GetRequest(indexName, indexName, id);
			GetResponse response = this.restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
			map = response.getSourceAsMap();
		} catch (IOException var6) {
			var6.printStackTrace();
		}

		return (Map)map;
	}

	public long countIndexDoc(String indexName) {
		long totalHits = 0L;
		if (!this.elasticPerformRequestUtils.indexExist(indexName)) {
			return totalHits;
		} else {
			try {
				SearchRequest searchRequest = new SearchRequest(new String[]{indexName});
				SearchResponse searchResponse = this.restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
				SearchHits hits = searchResponse.getHits();
				totalHits = hits.totalHits;
				if (this.elasticsearchConfig.isShowDsl()) {
					log.info("DSL：" + searchRequest.toString());
				}
			} catch (IOException var7) {
				var7.printStackTrace();
			}

			return totalHits;
		}
	}

	private List<String> searchDistinct(ElasticSearchDto searchDto, String field) {
		ElasticQueryDto queryDto = searchDto.getQueryDto();
		searchDto.setQueryDto(queryDto);
		this.checkAndSeIndicesList(searchDto.getQueryDto().isCheckIndex(), searchDto.getQueryDto().getIndices());
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		boolQuery.must(this.buildMustExist(field));
		searchDto.setBoolQuery(boolQuery);
		searchDto.setCollapse(this.collapse(field));
		boolean isError = false;

		try {
			SearchRequest searchRequest = this.querySearchRequest(searchDto);
			return this.queryResultStringByList(this.restHighLevelClient, searchRequest, field);
		} catch (Exception var7) {
			var7.printStackTrace();
			log.error("查询时出现错误", var7);
			isError = true;
			if (isError) {
				throw new RRException("查询时出现错误");
			} else {
				return null;
			}
		}
	}

	private List<Map<String, Object>> searchList(ElasticSearchDto searchDto) {
		this.searchResponse(searchDto);
		return this.queryResultByList(searchDto);
	}

	private SearchResponse searchResponse(ElasticSearchDto searchDto) {
		this.setSearchDto(searchDto);
		SearchRequest searchRequest = this.querySearchRequest(searchDto);
		this.queryResultByResponse(searchDto, this.restHighLevelClient, searchRequest);
		return searchDto.getSearchResponse();
	}

	private void setSearchDto(ElasticSearchDto searchDto) {
		this.checkAndSeIndicesList(searchDto.getQueryDto().isCheckIndex(), searchDto.getQueryDto().getIndices());
		this.setBoolQuery(searchDto);
		this.setSort(searchDto);
	}

	private void setBoolQuery(ElasticSearchDto searchDto) {
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		boolQuery = this.setBoolQueryByOther(searchDto, boolQuery);
		Iterator var3 = searchDto.getBuildBoolQueryByShouldList().iterator();

		while(var3.hasNext()) {
			BoolQueryBuilder builder = (BoolQueryBuilder)var3.next();
			boolQuery.must(builder);
		}

		searchDto.setBoolQuery(boolQuery);
	}

	private BoolQueryBuilder setBoolQueryByOther(ElasticSearchDto searchDto, BoolQueryBuilder boolQuery) {
		if (searchDto.getQueryDto().getOperators() == null) {
			return boolQuery;
		} else {
			Iterator var3 = searchDto.getQueryDto().getOperators().iterator();

			while(var3.hasNext()) {
				ElasticQueryOperatorDto operatorDto = (ElasticQueryOperatorDto)var3.next();
				this.buildBoolQuery(searchDto, boolQuery, operatorDto);
			}

			searchDto.setBoolQuery(boolQuery);
			return boolQuery;
		}
	}

	private void checkAndSeIndicesList(boolean checkIndex, String[] indices) {
		if (checkIndex) {
			try {
				String[] var3 = indices;
				int var4 = indices.length;

				for(int var5 = 0; var5 < var4; ++var5) {
					String indexName = var3[var5];
					if (!this.elasticPerformRequestUtils.indexExist(indexName)) {
						throw new RRException("不存在查询的Index:" + indexName);
					}
				}
			} catch (Exception var7) {
				log.error("查询时出现错误", var7);
				var7.printStackTrace();
			}
		}

	}

	private void setSort(ElasticSearchDto searchDto) {
		if (searchDto.getQueryDto().getSidx() != null) {
			List<FieldSortBuilder> listSort = new ArrayList();
			Iterator var3 = searchDto.getQueryDto().getSidx().entrySet().iterator();

			while(var3.hasNext()) {
				Map.Entry<String, String> sidx = (Map.Entry)var3.next();
				if ("asc".equals(sidx.getValue())) {
					listSort.add((new FieldSortBuilder((String)sidx.getKey())).order(SortOrder.ASC));
				} else if ("desc".equals(sidx.getValue())) {
					listSort.add((new FieldSortBuilder((String)sidx.getKey())).order(SortOrder.DESC));
				}
			}

			searchDto.setListSort(listSort);
		}

	}

	private SearchRequest querySearchRequest(ElasticSearchDto searchDto) {
		SearchRequest searchRequest = new SearchRequest(searchDto.getQueryDto().getIndices());
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.fetchSource(searchDto.getQueryDto().getIncludeFields(), searchDto.getQueryDto().getExcludeFields());
		searchSourceBuilder.query(searchDto.getBoolQuery());
		if (searchDto.getAggregationBuilder() != null) {
			searchSourceBuilder.aggregation(searchDto.getAggregationBuilder());
		}

		if (searchDto.getCollapse() != null) {
			searchSourceBuilder.collapse(searchDto.getCollapse());
		}

		if (searchDto.getListSort() != null) {
			Iterator var4 = searchDto.getListSort().iterator();

			while(var4.hasNext()) {
				FieldSortBuilder sort = (FieldSortBuilder)var4.next();
				searchSourceBuilder.sort(sort);
			}
		}

		if (!ArrayUtils.isEmpty(searchDto.getQueryDto().getSearchAfter())) {
			searchSourceBuilder.searchAfter(searchDto.getQueryDto().getSearchAfter());
		}

		if (searchDto.getQueryDto().getQueryPageDto() != null) {
			searchSourceBuilder.from(searchDto.getQueryDto().getQueryPageDto().getOffset());
			searchSourceBuilder.size(searchDto.getQueryDto().getQueryPageDto().getLimit());
		}

		searchRequest.source(searchSourceBuilder);
		if (this.elasticsearchConfig.isShowDsl()) {
			log.info("DSL：" + searchRequest.toString());
		}

		return searchRequest;
	}

	private List<Map<String, Object>> queryResultByList(ElasticSearchDto searchDto) {
		SearchResponse searchResponse = searchDto.getSearchResponse();
		if (searchResponse != null) {
			SearchHits hits = searchResponse.getHits();
			SearchHit[] searchHits = hits.getHits();
			boolean needIndex = false;
			if (Arrays.asList(searchDto.getQueryDto().getIncludeFields()).contains("_index")) {
				needIndex = true;
			}

			return this.getResultMapList(searchHits, needIndex);
		} else {
			return null;
		}
	}

	private void queryResultByResponse(ElasticSearchDto searchDto, RestHighLevelClient client, SearchRequest searchRequest) {
		try {
			SearchResponse searchResponse = this.getSearchResponse(client, searchRequest);
			if (searchResponse != null) {
				RestStatus status = searchResponse.status();
				if (status == null || status.getStatus() != RestStatus.OK.getStatus()) {
					throw new RRException("查询时出现错误" + status.getStatus());
				}

				searchDto.setSearchResponse(searchResponse);
			}

		} catch (Exception var6) {
			log.error("查询时出现错误", var6);
			throw new RRException("查询时出现错误");
		}
	}

	private List<String> queryResultStringByList(RestHighLevelClient client, SearchRequest searchRequest, String columnName) throws Exception {
		List<String> list = null;
		SearchResponse searchResponse = this.getSearchResponse(client, searchRequest);
		if (searchResponse != null) {
			list = this.getResultStringList(searchResponse.getHits().getHits(), columnName);
		}

		return list;
	}

	private SearchResponse getSearchResponse(RestHighLevelClient client, SearchRequest searchRequest) throws Exception {
		SearchResponse searchResponse = client.search(searchRequest, new Header[0]);
		return searchResponse.status() != null && searchResponse.status().getStatus() == RestStatus.OK.getStatus() ? searchResponse : null;
	}

	private List<Map<String, Object>> getResultMapList(SearchHit[] searchHits, boolean needIndex) {
		List<Map<String, Object>> mapList = new ArrayList();
		Map indexValue;
		SearchHit[] var5;
		int var6;
		int var7;
		SearchHit hit;
		if (!needIndex) {
			var5 = searchHits;
			var6 = searchHits.length;

			for(var7 = 0; var7 < var6; ++var7) {
				hit = var5[var7];
				indexValue = hit.getSourceAsMap();
				mapList.add(indexValue);
			}
		} else {
			var5 = searchHits;
			var6 = searchHits.length;

			for(var7 = 0; var7 < var6; ++var7) {
				hit = var5[var7];
				indexValue = hit.getSourceAsMap();
				indexValue.put("_index", hit.getIndex());
				mapList.add(indexValue);
			}
		}

		return mapList;
	}

	private SearchAfterResult getSearchAfterResult(SearchHit[] searchHits) {
		List<Map<String, Object>> mapList = new ArrayList();
		SearchHit[] var4 = searchHits;
		int var5 = searchHits.length;

		for(int var6 = 0; var6 < var5; ++var6) {
			SearchHit hit = var4[var6];
			Map<String, Object> indexValue = hit.getSourceAsMap();
			mapList.add(indexValue);
		}

		if (searchHits.length != 0) {
			String searchAfter = searchHits[searchHits.length - 1].getId();
			return new SearchAfterResult(searchAfter, mapList);
		} else {
			return new SearchAfterResult("", Collections.emptyList());
		}
	}

	private List<String> getResultStringList(SearchHit[] searchHits, String columnName) {
		List<String> list = new ArrayList();
		SearchHit[] var4 = searchHits;
		int var5 = searchHits.length;

		for(int var6 = 0; var6 < var5; ++var6) {
			SearchHit hit = var4[var6];
			list.add(hit.getSourceAsMap().get(columnName).toString());
		}

		return list;
	}

	public void buildBoolQuery(ElasticSearchDto searchDto, BoolQueryBuilder boolQuery, ElasticQueryOperatorDto operatorDto) {
		if (QueryTypeEnum.TERM_QUERY.getValue().equals(operatorDto.getOperate())) {
			boolQuery.must(this.buildTermQuery(operatorDto.getKey(), (String)operatorDto.getValue()));
		} else if (QueryTypeEnum.IN_TERMS.getValue().equals(operatorDto.getOperate())) {
			boolQuery.must(this.buildTermsQuery(operatorDto.getKey(), (String[])((String[])operatorDto.getValue())));
		} else if (QueryTypeEnum.QUERY_STRING_QUERY.getValue().equals(operatorDto.getOperate())) {
			boolQuery.must(this.buildQueryStringQuery(operatorDto.getKey(), (String)operatorDto.getValue()));
		} else if (QueryTypeEnum.RANGE_QUERY.getValue().equals(operatorDto.getOperate())) {
			boolQuery.must(this.buildRangeQuery(operatorDto));
		} else if (QueryTypeEnum.WILDCARD_QUERY.getValue().equals(operatorDto.getOperate())) {
			boolQuery.must(this.buildWildcardQuery(operatorDto.getKey(), (String)operatorDto.getValue()));
		} else if (LogicalRelationEnum.GT.getValue().equals(operatorDto.getOperate())) {
			boolQuery.must(this.buildGt(operatorDto.getKey(), (String)operatorDto.getValue()));
		} else if (LogicalRelationEnum.GTE.getValue().equals(operatorDto.getOperate())) {
			boolQuery.must(this.buildGte(operatorDto.getKey(), (String)operatorDto.getValue()));
		} else if (LogicalRelationEnum.LT.getValue().equals(operatorDto.getOperate())) {
			boolQuery.must(this.buildLt(operatorDto.getKey(), (String)operatorDto.getValue()));
		} else if (LogicalRelationEnum.LTE.getValue().equals(operatorDto.getOperate())) {
			boolQuery.must(this.buildLte(operatorDto.getKey(), (String)operatorDto.getValue()));
		} else if (QueryTypeEnum.SHOULD_IN_TERM.getValue().equals(operatorDto.getOperate())) {
			searchDto.getBuildBoolQueryByShouldList().add(this.buildShouldInTerm(operatorDto.getKey(), (String[])((String[])operatorDto.getValue())));
		} else if (QueryTypeEnum.SHOULD_IN_WILDCARD.getValue().equals(operatorDto.getOperate())) {
			searchDto.getBuildBoolQueryByShouldList().add(this.buildShouldInWildcard(operatorDto.getKey(), (String[])((String[])operatorDto.getValue())));
		} else if (QueryTypeEnum.MUST_NOT_TERM.getValue().equals(operatorDto.getOperate())) {
			searchDto.getBuildBoolQueryByShouldList().add(this.buildMustNotByTerm(operatorDto.getKey(), (String[])((String[])operatorDto.getValue())));
		} else if (QueryTypeEnum.MUST_NOT_WILDCARD.getValue().equals(operatorDto.getOperate())) {
			searchDto.getBuildBoolQueryByShouldList().add(this.buildMustNotByWildcard(operatorDto.getKey(), (String[])((String[])operatorDto.getValue())));
		} else if (QueryTypeEnum.QUERY_STRING_MULTI_QUERY.getValue().equals(operatorDto.getOperate())) {
			boolQuery.must(this.buildMultiTermQuery(operatorDto.getValue(), operatorDto.getKey()));
		}

	}

	private QueryBuilder buildTermQuery(String key, String value) {
		return QueryBuilders.termQuery(key, value);
	}

	private QueryBuilder buildMultiTermQuery(Object value, String key) {
		String[] keys = StringUtils.split(key, ",");
		return QueryBuilders.multiMatchQuery(value, keys);
	}

	private QueryBuilder buildTermsQuery(String key, String... values) {
		return QueryBuilders.termsQuery(key, values);
	}

	private QueryBuilder buildWildcardQuery(String key, String value) {
		return QueryBuilders.wildcardQuery(key, "*" + value + "*");
	}

	private QueryBuilder buildQueryStringQuery(String key, String value) {
		return QueryBuilders.queryStringQuery(value).field(key);
	}

	private BoolQueryBuilder buildShouldInTerm(String key, String... values) {
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		boolQuery.should(this.buildTermsQuery(key, values));
		return boolQuery;
	}

	private BoolQueryBuilder buildShouldInWildcard(String key, String... values) {
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		String[] var4 = values;
		int var5 = values.length;

		for(int var6 = 0; var6 < var5; ++var6) {
			String value = var4[var6];
			boolQuery.should(this.buildWildcardQuery(key, value));
		}

		return boolQuery;
	}

	private BoolQueryBuilder buildMustNotByTerm(String key, String... values) {
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		String[] var4 = values;
		int var5 = values.length;

		for(int var6 = 0; var6 < var5; ++var6) {
			String value = var4[var6];
			boolQuery.mustNot(this.buildTermsQuery(key, value));
		}

		return boolQuery;
	}

	private BoolQueryBuilder buildMustNotByWildcard(String key, String... values) {
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		String[] var4 = values;
		int var5 = values.length;

		for(int var6 = 0; var6 < var5; ++var6) {
			String value = var4[var6];
			boolQuery.mustNot(this.buildWildcardQuery(key, value));
		}

		return boolQuery;
	}

	private QueryBuilder buildRangeQuery(ElasticQueryOperatorDto operatorDto) {
		boolean minflag = true;
		boolean maxflag = true;
		if (operatorDto.getOperate() != null && operatorDto.getOperateByRight() != null && LogicalRelationEnum.GT.getValue().equals(operatorDto.getOperateByRight())) {
			minflag = false;
		}

		if (operatorDto.getOperate() != null && operatorDto.getOperateByRight() != null && LogicalRelationEnum.LT.getValue().equals(operatorDto.getOperateByRight())) {
			maxflag = false;
		}

		return this.buildRangeQuery(operatorDto.getKey(), operatorDto.getValue(), operatorDto.getValueByRight(), minflag, maxflag);
	}

	private QueryBuilder buildRangeQuery(String key, Object valueFrom, Object valueTo, boolean minflag, boolean maxflag) {
		return QueryBuilders.rangeQuery(key).from(valueFrom).to(valueTo).includeLower(minflag).includeUpper(maxflag);
	}

	private QueryBuilder buildGt(String key, String value) {
		return QueryBuilders.rangeQuery(key).gt(value);
	}

	private QueryBuilder buildGte(String key, String value) {
		return QueryBuilders.rangeQuery(key).gte(value);
	}

	private QueryBuilder buildLt(String key, String value) {
		return QueryBuilders.rangeQuery(key).lt(value);
	}

	private QueryBuilder buildLte(String key, String value) {
		return QueryBuilders.rangeQuery(key).lte(value);
	}

	private QueryBuilder buildMustExist(String key) {
		return QueryBuilders.existsQuery(key);
	}

	private QueryBuilder buildMustNotExist(BoolQueryBuilder boolQuery, String key) {
		return boolQuery.mustNot(this.buildMustExist(key));
	}

	private CardinalityAggregationBuilder aggregationBuildersCardinality(String aliasName, String name, long precision) {
		return ((CardinalityAggregationBuilder) AggregationBuilders.cardinality(aliasName).field(name)).precisionThreshold(precision);
	}

	private TermsAggregationBuilder aggregationBuildersTerms(String aliasName, String name, int size) {
		return ((TermsAggregationBuilder)AggregationBuilders.terms(aliasName).field(name)).size(size);
	}

	private CollapseBuilder collapse(String field) {
		return new CollapseBuilder(field);
	}
}
