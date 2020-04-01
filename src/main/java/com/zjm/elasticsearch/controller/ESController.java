package com.zjm.elasticsearch.controller;


import com.google.common.collect.Maps;
import com.zjm.config.elasticSearch.utils.ElasticPerformRequestUtils;
import com.zjm.config.elasticSearch.utils.ElasticSearchOperationUtils;
import com.zjm.config.elasticSearch.utils.ElasticSearchQueryUtils;
import com.zjm.config.elasticSearch.utils.ElasticSearchRequestUtils;
import com.zjm.model.controller.request.ReqCreateIndexAlias;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author White Tan
 * @description
 * @date 2020/3/12
 */
@Api(value = "ES API", tags = {"ES api"})
@RestController
@RequestMapping(value = "/es")
@Slf4j
public class ESController {
	@Resource
	private ElasticPerformRequestUtils elasticPerformRequestUtils;

	@Resource
	private ElasticSearchOperationUtils elasticSearchOperationUtils;

	@Resource
	private ElasticSearchQueryUtils elasticSearchQueryUtils;

	@Resource
	private ElasticSearchRequestUtils elasticSearchRequestUtils;

	@ApiOperation("判断索引是否存在")
	@GetMapping("/indexExist/{indexName}")
	public Boolean indexExist(@ApiParam(name = "indexName", value = "索引名称")
			@PathVariable("indexName") String indexName){
		return elasticPerformRequestUtils.indexExist(indexName);
	}

	@ApiOperation("创建索引")
	@GetMapping("/createIndex/{indexName}")
	public Boolean createIndex(@ApiParam(name = "indexName", value = "索引名称")
								   @PathVariable("indexName") String indexName){
		return elasticSearchOperationUtils.createIndex(indexName);
	}

	@ApiOperation("删除索引")
	@GetMapping("/deleteIndex/{indexName}")
	public Boolean deleteIndex(@ApiParam(name = "indexName", value = "索引名称")
							   @PathVariable("indexName") String indexName){
		return elasticSearchOperationUtils.deleteIndex(indexName);
	}

	@ApiOperation("创建索引数据（向索引插入数据）")
	@PostMapping("/createIndexData")
	public String createIndexData(){
		Map<String, Object> map = Maps.newHashMap();
		map.put("title","python 操作"+Thread.currentThread().getId());
		map.put("author","white tan"+Thread.currentThread().getId());
		map.put("created_time", new Date());
		map.put("updated_time", new Date());
		map.put("number_of_words", 2000);
		map.put("price", new BigDecimal(2.1501).doubleValue());
		map.put("thread_id", Thread.currentThread().getId());
		map.put("thread_name", Thread.currentThread().getName());
		map.put("thread_state", Thread.currentThread().getState());
		map.put("thread_priority", Thread.currentThread().getPriority());
		return elasticSearchOperationUtils.insertOne("blogs", "_doc", map); // 返回ID
//		elasticSearchOperationUtils.insertOneSync("blogs", "_doc", map, 10l); // 返回ID
//		elasticSearchOperationUtils.insertOneASync("blogs", "_doc", map); // 异步执行
//		return "";
	}

	@ApiOperation("更新索引数据（向索引中数据更新）")
	@GetMapping("/modifyIndexData/{id}")
	public Boolean modifyIndexData(@ApiParam(name = "id", value = "索引数据ID")
									   @PathVariable("id") String id){
		Map<String, Object> map = Maps.newHashMap();
		map.put("title","es JAVA 操作 - 改"+Thread.currentThread().getId());
		map.put("author","white tan"+Thread.currentThread().getId());
		map.put("created_time", new Date());
		map.put("updated_time", new Date());
		map.put("number_of_words", 2020);
		map.put("price", new BigDecimal(2.1601).doubleValue());
		map.put("thread_id", Thread.currentThread().getId());
		map.put("thread_name", Thread.currentThread().getName());
		map.put("thread_state", Thread.currentThread().getState());
		map.put("thread_priority", Thread.currentThread().getPriority());
		elasticSearchOperationUtils.upsertOneSyncById("blogs", "_doc", map, id);
		return false;
	}

	@ApiOperation("创建索引别名")
	@PostMapping("/createIndexAlias")
	public Boolean createIndexAlias(@RequestBody ReqCreateIndexAlias reqCreateIndexAlias){

		return false;
	}

	@ApiOperation("查询索引list 信息")
	@GetMapping("/searchCustomList/{alias}")
	public List<Map<String, Object>> searchCustomList(@ApiParam(name = "alias", value = "索引列名")
														  @PathVariable("alias") String alias){
		try {
			SearchRequest searchRequest = new SearchRequest(alias);
			return elasticSearchRequestUtils.searchCustomList(searchRequest, false);
		} catch(Exception e){
			log.error(e.getMessage(), e);
		}
		return null;
	}




}
