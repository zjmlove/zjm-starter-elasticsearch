package com.zjm.config.elasticSearch.utils;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author White Tan
 * @description
 * @date 2020/3/12
 */
public class ElasticClientFactory {
	private static final Logger log = LoggerFactory.getLogger(ElasticClientFactory.class);
	public static int CONNECT_TIMEOUT_MILLIS;
	public static int SOCKET_TIMEOUT_MILLIS;
	public static int CONNECTION_REQUEST_TIMEOUT_MILLIS;
	public static int MAX_CONN_PER_ROUTE;
	public static int MAX_CONN_TOTAL;
	public static int MAX_RETRY_TIMEOUT_MILLIS;
	private static HttpHost[] HTTP_HOSTS;
	private RestClientBuilder builder;
	private RestClient restClient;
	private RestHighLevelClient restHighLevelClient;
	private static ElasticClientFactory elasticClientFactory = new ElasticClientFactory();

	private ElasticClientFactory() {
	}

	public static ElasticClientFactory build(HttpHost[] httpHosts, Integer connectTimeOut, Integer socketTimeOut, Integer connectionRequestTime, Integer maxConnectNum, Integer maxConnectPerRoute, Integer maxRetryTimeoutMillis) {
		HTTP_HOSTS = httpHosts;
		CONNECT_TIMEOUT_MILLIS = connectTimeOut;
		SOCKET_TIMEOUT_MILLIS = socketTimeOut;
		CONNECTION_REQUEST_TIMEOUT_MILLIS = connectionRequestTime;
		MAX_CONN_TOTAL = maxConnectNum;
		MAX_CONN_PER_ROUTE = maxConnectPerRoute;
		MAX_RETRY_TIMEOUT_MILLIS = maxRetryTimeoutMillis;
		return elasticClientFactory;
	}

	public void init() {
		this.builder = RestClient.builder(HTTP_HOSTS).setMaxRetryTimeoutMillis(MAX_RETRY_TIMEOUT_MILLIS);
		this.setConnectTimeOutConfig();
		this.setMutiConnectConfig();
		this.restClient = this.builder.build();
		this.restHighLevelClient = new RestHighLevelClient(this.builder);
	}

	public void setConnectTimeOutConfig() {
		this.builder.setRequestConfigCallback((requestConfigBuilder) -> {
			requestConfigBuilder.setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
			requestConfigBuilder.setSocketTimeout(SOCKET_TIMEOUT_MILLIS);
			requestConfigBuilder.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT_MILLIS);
			return requestConfigBuilder;
		});
	}

	public void setMutiConnectConfig() {
		this.builder.setHttpClientConfigCallback((httpClientBuilder) -> {
			httpClientBuilder.setMaxConnTotal(MAX_CONN_TOTAL);
			httpClientBuilder.setMaxConnPerRoute(MAX_CONN_PER_ROUTE);
			return httpClientBuilder;
		});
	}

	public RestClient getClient() {
		return this.restClient;
	}

	public RestHighLevelClient getRhlClient() {
		return this.restHighLevelClient;
	}

	public void close() {
		if (this.restClient != null) {
			try {
				this.restClient.close();
			} catch (IOException var2) {
				var2.printStackTrace();
				log.error("关闭RestClient时出现错误", var2);
			}
		}

	}
}
