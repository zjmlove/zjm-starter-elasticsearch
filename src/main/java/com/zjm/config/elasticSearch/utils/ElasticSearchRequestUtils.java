package com.zjm.config.elasticSearch.utils;

import com.zjm.config.elasticSearch.dto.ElasticQueryDto;
import com.zjm.config.elasticSearch.dto.SearchAfterResult;
import com.zjm.core.utils.PageBase;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author White Tan
 * @description ES 查询请求帮助类
 * @date 2020/3/12
 */
@Component
public class ElasticSearchRequestUtils {
	private static final Logger log = LoggerFactory.getLogger(ElasticSearchRequestUtils.class);
	@Autowired
	private ElasticPerformRequestUtils elasticPerformRequestUtils;
	@Autowired
	private ElasticSearchOperationUtils elasticSearchOperationUtils;
	@Autowired
	private ElasticSearchQueryUtils elasticSearchQueryUtils;

	public ElasticSearchRequestUtils() {
	}

	public List<Map<String, Object>> search(ElasticQueryDto queryDto) {
		return this.elasticSearchQueryUtils.search(queryDto);
	}

	public List<Map<String, Object>> searchCustomList(SearchRequest searchRequest, boolean needIndex) throws Exception {
		return this.elasticSearchQueryUtils.searchCustomList(searchRequest, needIndex);
	}

	public SearchResponse searchCustomResponse(SearchRequest searchRequest) throws Exception {
		return this.elasticSearchQueryUtils.searchCustomResponse(searchRequest);
	}

	public List<String> searchDistinct(ElasticQueryDto queryDto, String field) {
		return this.elasticSearchQueryUtils.searchDistinct(queryDto, field);
	}

	public SearchResponse searchResponse(ElasticQueryDto queryDto) {
		return this.elasticSearchQueryUtils.searchResponse(queryDto);
	}

	public SearchAfterResult searchForSearchAfter(ElasticQueryDto queryDto) {
		return this.elasticSearchQueryUtils.searchForSearchAfter(queryDto);
	}

	public PageBase searchPage(ElasticQueryDto queryDto) {
		return this.elasticSearchQueryUtils.searchPage(queryDto);
	}

	public Map<String, Object> findDateById(String indexName, String id) {
		return this.elasticSearchQueryUtils.queryById(indexName, id);
	}

	public Long count(ElasticQueryDto queryDto) {
		return this.elasticSearchQueryUtils.count(queryDto);
	}

	public long countIndexDoc(String indexName) {
		return this.elasticSearchQueryUtils.countIndexDoc(indexName);
	}

	public boolean createIndex(String indexName) {
		return this.elasticSearchOperationUtils.createIndex(indexName);
	}

	public boolean createIndex(CreateIndexRequest createIndexRequest) {
		return this.elasticSearchOperationUtils.createIndex(createIndexRequest);
	}

	public boolean indexExist(String indexName) {
		return this.elasticPerformRequestUtils.indexExist(indexName);
	}

	public boolean deleteIndex(String indexName) {
		return this.elasticSearchOperationUtils.deleteIndex(indexName);
	}

	public boolean deleteRecordByCondition(String indexName, List<String> ids) {
		return this.deleteRecordByCondition(indexName, (String)null, ids);
	}

	public boolean deleteRecordByCondition(String indexName, String type, List<String> ids) {
		return this.elasticSearchOperationUtils.deleteRecordByCondition(indexName, type, ids);
	}

	public String insertOne(String indexName, Map<String, Object> data) {
		return this.insertOne(indexName, (String)null, data);
	}

	public String insertOne(String indexName, String type, Map<String, Object> data) {
		return this.elasticSearchOperationUtils.insertOne(indexName, type, data);
	}

	public boolean insertOneSync(String indexName, Map<String, Object> data, long overTime) {
		return this.insertOneSync(indexName, (String)null, data, overTime);
	}

	public boolean insertOneSync(String indexName, String type, Map<String, Object> data, long overTime) {
		return this.elasticSearchOperationUtils.insertOneSync(indexName, type, data, overTime);
	}

	public boolean insertOneASync(String indexName, Map<String, Object> data) {
		return this.insertOneASync(indexName, (String)null, data);
	}

	public boolean insertOneASync(String indexName, String type, Map<String, Object> data) {
		return this.elasticSearchOperationUtils.insertOneASync(indexName, type, data);
	}

	public boolean insertBulkSync(String indexName, List<Map<String, Object>> data) {
		return this.insertBulkSync(indexName, (String)null, data);
	}

	public boolean insertBulkSync(String indexName, String type, List<Map<String, Object>> data) {
		return this.elasticSearchOperationUtils.insertBulkSync(indexName, type, data);
	}

	public boolean insertBulkSync(String indexName, List<Map<String, Object>> data, long overTime) {
		return this.insertBulkSync(indexName, (String)null, data, overTime);
	}

	public boolean insertBulkSync(String indexName, String type, List<Map<String, Object>> data, long overTime) {
		return this.elasticSearchOperationUtils.insertBulkSync(indexName, type, data, overTime);
	}

	public boolean insertBulkASync(String indexName, List<Map<String, Object>> data) {
		return this.insertBulkASync(indexName, (String)null, data);
	}

	public boolean insertBulkASync(String indexName, String type, List<Map<String, Object>> data) {
		return this.elasticSearchOperationUtils.insertBulkASync(indexName, type, data);
	}

	public boolean upsertOneSyncById(String indexName, Map<String, Object> data, String id) {
		return this.upsertOneSyncById(indexName, (String)null, data, id);
	}

	public boolean upsertOneSyncById(String indexName, String type, Map<String, Object> data, String id) {
		return this.elasticSearchOperationUtils.upsertOneSyncById(indexName, type, data, id);
	}

	public boolean upsertOneSyncById(String indexName, Map<String, Object> data, String id, long overTime) {
		return this.upsertOneSyncById(indexName, (String)null, data, id, overTime);
	}

	public boolean upsertOneSyncById(String indexName, String type, Map<String, Object> data, String id, long overTime) {
		return this.elasticSearchOperationUtils.upsertOneSyncById(indexName, type, data, id, overTime);
	}

	public boolean upsertOneForASyncResponse(String indexName, Map<String, Object> data, String id) {
		return this.upsertOneForASyncResponse(indexName, (String)null, data, id);
	}

	public boolean upsertOneForASyncResponse(String indexName, String type, Map<String, Object> data, String id) {
		return this.elasticSearchOperationUtils.upsertOneForASyncResponse(indexName, type, data, id);
	}
}
