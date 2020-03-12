package com.zjm.elasticsearch.controller;

import com.zjm.config.elasticSearch.utils.ElasticPerformRequestUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

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

	@ApiOperation("判断索引是否存在")
	@GetMapping("/indexExist")
	public Boolean indexExist(
			@ApiParam(name = "索引名称")  String indexName){
		return elasticPerformRequestUtils.indexExist(indexName);
	}
}
