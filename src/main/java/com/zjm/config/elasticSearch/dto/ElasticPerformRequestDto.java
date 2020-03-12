package com.zjm.config.elasticSearch.dto;

import org.apache.http.Header;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import com.alibaba.fastjson.JSONObject;

/**
 * @author White Tan
 * @description ES 执行请求 DTO 对象
 * @date 2020/3/12
 */
public class ElasticPerformRequestDto {
	String method;
	String endpoint;
	Map<String, String> params = Collections.emptyMap();
	Header[] headers;
	JSONObject applicationJson;

	public ElasticPerformRequestDto() {
	}

	public String getMethod() {
		return this.method;
	}

	public String getEndpoint() {
		return this.endpoint;
	}

	public Map<String, String> getParams() {
		return this.params;
	}

	public Header[] getHeaders() {
		return this.headers;
	}

	public JSONObject getApplicationJson() {
		return this.applicationJson;
	}

	public void setMethod(final String method) {
		this.method = method;
	}

	public void setEndpoint(final String endpoint) {
		this.endpoint = endpoint;
	}

	public void setParams(final Map<String, String> params) {
		this.params = params;
	}

	public void setHeaders(final Header[] headers) {
		this.headers = headers;
	}

	public void setApplicationJson(final JSONObject applicationJson) {
		this.applicationJson = applicationJson;
	}

	public String toString() {
		return "ElasticPerformRequestDto(method=" + this.getMethod() + ", endpoint=" + this.getEndpoint() + ", params=" + this.getParams() + ", headers=" + Arrays.deepToString(this.getHeaders()) + ", applicationJson=" + this.getApplicationJson() + ")";
	}
}
