package com.zjm.config.elasticSearch;

import com.zjm.config.elasticSearch.utils.ElasticClientFactory;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.List;

/**
 * @author White Tan
 * @description
 * @date 2020/3/12
 */
@Configuration
@ComponentScan(
		basePackageClasses = {ElasticClientFactory.class}
)
public class ElasticSearchConfig {

	@Value("#{'${elasticSearch.hosts}'.split(',')}")
	private List<String> hosts;
	@Value("${elasticSearch.maxConnectNum}")
	private Integer maxConnectNum;
	@Value("${elasticSearch.maxConnectPerRoute}")
	private Integer maxConnectPerRoute;
	@Value("${elasticSearch.connectTimeoutMillis:60000}")
	private int connectTimeoutMillis;
	@Value("${elasticSearch.socketTimeoutMillis:60000}")
	private int socketTimeoutMillis;
	@Value("${elasticSearch.connectionRequestTimeoutMillis:30000}")
	private int connectionRequestTimeoutMillis;
	@Value("${elasticSearch.maxRetryTimeoutMillis:30000}")
	private int maxRetryTimeoutMillis;
	@Value("${elasticSearch.maxResultCount:10000}")
	private long maxResultCount;
	@Value("${elasticSearch.showDsl:false}")
	private boolean showDsl;
	@Value("${elasticSearch.useMaxResultWindow:false}")
	private boolean useMaxResultWindow;

	public ElasticSearchConfig() {
	}

	public long getMaxResultCount() {
		return this.maxResultCount <= 10000L ? this.maxResultCount : 10000L;
	}

	public boolean getUseMaxResultWindow() {
		return this.useMaxResultWindow;
	}

	@Bean
	public HttpHost[] httpHost() {
		HttpHost[] httpHosts = new HttpHost[this.hosts.size()];

		for(int i = 0; i < this.hosts.size(); ++i) {
			String host = ((String)this.hosts.get(i)).contains("http://") ? (String)this.hosts.get(i) : "http://" + (String)this.hosts.get(i);
			httpHosts[i] = HttpHost.create(host);
		}

		return httpHosts;
	}

	@Bean(
			initMethod = "init",
			destroyMethod = "close"
	)
	public ElasticClientFactory getFactory() {
		return ElasticClientFactory.build(this.httpHost(), this.connectTimeoutMillis, this.socketTimeoutMillis, this.connectionRequestTimeoutMillis, this.maxConnectNum, this.maxConnectPerRoute, this.maxRetryTimeoutMillis);
	}

	@Bean
	@Scope("singleton")
	public RestClient getRestClient() {
		return this.getFactory().getClient();
	}

	@Bean
	@Scope("singleton")
	public RestHighLevelClient getRHLClient() {
		return this.getFactory().getRhlClient();
	}

	public int getMaxRetryTimeoutMillis() {
		return this.maxRetryTimeoutMillis;
	}

	public boolean isShowDsl() {
		return this.showDsl;
	}
}
