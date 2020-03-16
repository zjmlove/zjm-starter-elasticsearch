package com.zjm.config.elasticSearch.utils;

import com.alibaba.fastjson.JSONObject;
import com.zjm.config.elasticSearch.dto.ElasticPerformRequestDto;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author White Tan
 * @description ES 执行请求帮助类；索引存在、设置索引分片和备份
 * @date 2020/3/12
 */
@Component
public class ElasticPerformRequestUtils {
	private static final Logger log = LoggerFactory.getLogger(ElasticPerformRequestUtils.class);
	@Autowired
	private RestHighLevelClient restHighLevelClient;
	@Autowired
	private RestClient restClient;

	public ElasticPerformRequestUtils() {
	}

	public boolean indexExist(String indexName) {
		boolean exist = false;

		try {
			GetIndexRequest getIndexRequest = new GetIndexRequest();
			getIndexRequest.indices(new String[]{indexName});
			exist = this.restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
		} catch (IOException var4) {
			log.error("验证索引是否存在时出现异常", var4);
			var4.printStackTrace();
		}

		return exist;
	}
	//	number_of_shards  是数据分片数，默认为5，有时候设置为3
	//	number_of_replicas 是数据备份数，如果只有一台机器，设置为0
	public boolean setNumberOfReplicas(String indexName, Integer number) {
		int numberOfReplicas = number != null && number >= 0 ? number : 0;
		ElasticPerformRequestDto performRequestDto = new ElasticPerformRequestDto();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("number_of_replicas", numberOfReplicas);
		performRequestDto.setApplicationJson(jsonObject);
		performRequestDto.setMethod("PUT");
		performRequestDto.setEndpoint(indexName + "*/_settings");
		return this.doRequest(performRequestDto);
	}

	public boolean deleteTemplate(String templateName) {
		ElasticPerformRequestDto performRequestDto = new ElasticPerformRequestDto();
		performRequestDto.setMethod("DELETE");
		performRequestDto.setEndpoint("_template/" + templateName);
		return this.doRequest(performRequestDto);
	}

	private boolean doRequest(ElasticPerformRequestDto performRequestDto) {
		boolean result = false;

		try {
			result = performRequestResult(this.restClient, performRequestDto);
		} catch (Exception var4) {
			var4.printStackTrace();
			log.error("查询时出现错误", var4);
		}

		return result;
	}

	public static boolean performRequestResult(RestClient restClient, ElasticPerformRequestDto performRequestDto) throws IOException {
		Response response = getResponseByperformRequest(restClient, performRequestDto);
		return response == null ? false : "OK".equals(response.getStatusLine().getReasonPhrase());
	}

	public static Response getResponseByperformRequest(RestClient restClient, ElasticPerformRequestDto performRequestDto) throws IOException {
		Response response;
		if (performRequestDto.getApplicationJson() == null) {
			response = restClient.performRequest(performRequestDto.getMethod(), performRequestDto.getEndpoint(), new Header[0]);
		} else {
			HttpEntity httpEntity = new NStringEntity(performRequestDto.getApplicationJson().toJSONString(), ContentType.APPLICATION_JSON);
			response = restClient.performRequest(performRequestDto.getMethod(), performRequestDto.getEndpoint(), performRequestDto.getParams(), httpEntity, new Header[0]);
		}

		return response;
	}
}
