package com.zjm.config.elasticSearch.utils;

import com.alibaba.fastjson.JSON;
import com.zjm.exception.RRException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author White Tan
 * @description ES 查询操作帮助类 ： 创建索引、操作索引：增、删、改
 * @date 2020/3/12
 */
@Component
public class ElasticSearchOperationUtils {
	private static final Logger log = LoggerFactory.getLogger(ElasticSearchOperationUtils.class);
	@Autowired
	private RestHighLevelClient restHighLevelClient;
	@Autowired
	private ElasticPerformRequestUtils elasticPerformRequestUtils;

	public ElasticSearchOperationUtils() {
	}

	public boolean createIndex(String indexName) {
		if (this.elasticPerformRequestUtils.indexExist(indexName)) {
			throw new RRException("索引已存在");
		} else {
			CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
			return this.createIndex(createIndexRequest);
		}
	}

	public boolean createIndex(CreateIndexRequest createIndexRequest) {
		try {
			CreateIndexResponse indexResponse = this.restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
			if (!indexResponse.isAcknowledged()) {
				log.info("创建索引失败");
			}

			return indexResponse.isAcknowledged();
		} catch (IOException var3) {
			log.error("创建索引时出现异常", var3);
			var3.printStackTrace();
			return false;
		}
	}

	public boolean deleteIndex(String indexName) {
		if (!this.elasticPerformRequestUtils.indexExist(indexName)) {
			return true;
		} else {
			boolean flag;
			try {
				DeleteIndexRequest request = new DeleteIndexRequest(indexName);
				this.restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
				flag = true;
			} catch (IOException var4) {
				log.error("删除index时出现异常", var4);
				var4.printStackTrace();
				flag = false;
			}

			return flag;
		}
	}

	public boolean deleteRecordByCondition(String indexName, String type, List<String> ids) {
		try {
			BulkRequest bulkRequest = new BulkRequest();
			Iterator var5 = ids.iterator();

			while(var5.hasNext()) {
				String id = (String)var5.next();
				DeleteRequest deleteRequest = this.getDeleteRequest(indexName, type);
				deleteRequest.id(id);
				bulkRequest.add(deleteRequest);
			}

			this.restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
		} catch (IOException var8) {
			log.error("删除数据时出现异常", var8);
			var8.printStackTrace();
		}

		return true;
	}

	public String insertOne(String indexName, String type, Map<String, Object> data) {
		return this.insertOneForResponse(indexName, type, data).getId();
	}

	public boolean insertOneSync(String indexName, String type, Map<String, Object> data, long overTime) {
		Callable<Boolean> callable = () -> {
			this.insertOneForResponse(indexName, type, data);
			return true;
		};
		return this.getTimeOut(callable, overTime);
	}

	public boolean insertOneASync(String indexName, String type, Map<String, Object> data) {
		ActionListener<IndexResponse> listener = new ActionListener<IndexResponse>() {
			public void onResponse(IndexResponse indexResponse) {
			}

			public void onFailure(Exception e) {
				ElasticSearchOperationUtils.log.error("异步单条插入失败", e);
			}
		};
		IndexRequest indexRequest = this.getIndexRequest(indexName, type);
		indexRequest.source(data);
		this.restHighLevelClient.indexAsync(indexRequest, RequestOptions.DEFAULT, listener);
		return true;
	}

	public boolean insertBulkSync(String indexName, String type, List<Map<String, Object>> data) {
		this.getBulkInsertSync(indexName, type, data);
		return true;
	}

	public boolean insertBulkSyncData(String indexName, String type, List<?> data, String idFieldName) {
		this.getBulkInsertSyncData(indexName, type, data, idFieldName);
		return true;
	}

	public boolean insertBulkSync(String indexName, String type, List<Map<String, Object>> data, long overTime) {
		Callable<Boolean> callable = () -> {
			this.getBulkInsertSync(indexName, type, data);
			return true;
		};
		return this.getTimeOut(callable, overTime);
	}

	public boolean insertBulkASync(String indexName, String type, List<Map<String, Object>> data) {
		ActionListener<BulkResponse> listener = new ActionListener<BulkResponse>() {
			public void onResponse(BulkResponse bulkItemResponses) {
			}

			public void onFailure(Exception e) {
				ElasticSearchOperationUtils.log.error("异步批量插入失败", e);
			}
		};
		BulkRequest bulkRequest = new BulkRequest();
		Iterator var6 = data.iterator();

		while(var6.hasNext()) {
			Map<String, Object> map = (Map)var6.next();
			IndexRequest indexRequest = this.getIndexRequest(indexName, type);
			indexRequest.source(map);
			bulkRequest.add(indexRequest);
		}

		this.restHighLevelClient.bulkAsync(bulkRequest, RequestOptions.DEFAULT, listener);
		return true;
	}

	public boolean upsertOneSyncById(String indexName, String type, Map<String, Object> data, String id) {
		this.upsertOneForResponse(indexName, type, data, id);
		return true;
	}

	public boolean upsertOneSyncById(String indexName, String type, Object object, String id) {
		this.upsertOneForResponse(indexName, type, object, id);
		return true;
	}

	public boolean upsertOneSyncById(String indexName, String type, Map<String, Object> data, String id, long overTime) {
		Callable<Boolean> callable = () -> {
			this.upsertOneForResponse(indexName, type, data, id);
			return null;
		};
		return this.getTimeOut(callable, overTime);
	}

	public boolean upsertOneForASyncResponse(String indexName, String type, Map<String, Object> data, String id) {
		ActionListener<UpdateResponse> listener = new ActionListener<UpdateResponse>() {
			public void onResponse(UpdateResponse updateResponse) {
			}

			public void onFailure(Exception e) {
				ElasticSearchOperationUtils.log.error("{} 异步修改执行失败", indexName, e);
			}
		};
		UpdateRequest request = this.getUpdateRequest(indexName, type);
		request.id(id);
		request.doc(data);
		request.upsert(data);
		this.restHighLevelClient.updateAsync(request, RequestOptions.DEFAULT, listener);
		return true;
	}

	private UpdateResponse upsertOneForResponse(String indexName, String type, Map<String, Object> data, String id) {
		UpdateResponse response = null;

		try {
			UpdateRequest request = this.getUpdateRequest(indexName, type);
			request.id(id);
			request.doc(data);
			request.upsert(data);
			response = this.restHighLevelClient.update(request, RequestOptions.DEFAULT);
		} catch (IOException var7) {
			log.error("{} 根据ID：{} 更新数据失败", indexName, id);
			var7.printStackTrace();
		}

		return response;
	}

	private UpdateResponse upsertOneForResponse(String indexName, String type, Object jsonData, String id) {
		UpdateResponse response = null;

		try {
			UpdateRequest request = this.getUpdateRequest(indexName, type);
			request.id(id);
			request.doc(JSON.toJSONString(jsonData), XContentType.JSON);
			request.upsert(JSON.toJSONString(jsonData), XContentType.JSON);
			response = this.restHighLevelClient.update(request, RequestOptions.DEFAULT);
		} catch (IOException var7) {
			log.error("{} 根据ID：{} 更新数据失败", indexName, id);
			var7.printStackTrace();
		}

		return response;
	}

	private void getBulkInsertSyncData(String indexName, String type, List<?> dataList, String idFieldNmae) {
		try {
			BulkRequest bulkRequest = new BulkRequest();
			dataList.forEach((data) -> {
				UpdateRequest request = this.getUpdateRequest(indexName, type);

				try {
					Object o = FieldUtils.readField(data, idFieldNmae, true);
					request.id(String.valueOf(o));
				} catch (IllegalAccessException var8) {
					var8.printStackTrace();
				}

				request.doc(JSON.toJSONString(data), XContentType.JSON);
				request.upsert(JSON.toJSONString(data), XContentType.JSON);
				bulkRequest.add(request);
			});
			this.restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
		} catch (IOException var6) {
			log.error("{} 批量插入失败", indexName, var6);
		}

	}

	private void getBulkInsertSync(String indexName, String type, List<Map<String, Object>> data) {
		try {
			BulkRequest bulkRequest = new BulkRequest();
			Iterator var5 = data.iterator();

			while(var5.hasNext()) {
				Map<String, Object> map = (Map)var5.next();
				IndexRequest indexRequest = this.getIndexRequest(indexName, type);
				indexRequest.source(map);
				bulkRequest.add(indexRequest);
			}

			this.restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
		} catch (IOException var8) {
			log.error("{} 批量插入失败", indexName, var8);
		}

	}

	private boolean getTimeOut(Callable<?> task, long timeOut) {
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		FutureTask<?> futureTask = (FutureTask)executorService.submit(task);
		executorService.execute(futureTask);

		try {
			futureTask.get(timeOut, TimeUnit.SECONDS);
		} catch (InterruptedException var7) {
			var7.printStackTrace();
		} catch (ExecutionException var8) {
			var8.printStackTrace();
		} catch (TimeoutException var9) {
			var9.printStackTrace();
		}

		return false;
	}

	private IndexResponse insertOneForResponse(String indexName, String type, Map<String, Object> data) {
		IndexResponse indexResponse = null;

		try {
			IndexRequest indexRequest = this.getIndexRequest(indexName, type);
			indexRequest.source(data);
			indexResponse = this.restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
		} catch (IOException var6) {
			log.error("{} 插入数据失败", indexName, var6);
		}

		return indexResponse;
	}

	private IndexRequest getIndexRequest(String indexName, String type) {
		IndexRequest indexRequest = new IndexRequest(indexName);
		if (!StringUtils.isEmpty(type)) {
			indexRequest.type(type);
		}

		return indexRequest;
	}

	private UpdateRequest getUpdateRequest(String indexName, String type) {
		UpdateRequest request = new UpdateRequest();
		request.index(indexName);
		if (!StringUtils.isEmpty(type)) {
			request.type(type);
		}

		return request;
	}

	private DeleteRequest getDeleteRequest(String indexName, String type) {
		DeleteRequest request = new DeleteRequest(indexName);
		if (!StringUtils.isEmpty(type)) {
			request.type(type);
		}

		return request;
	}
}
