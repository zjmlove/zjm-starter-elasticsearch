package com.zjm.elasticsearch.controller;

import com.alibaba.fastjson.JSON;
import com.zjm.model.controller.request.ReqTest;
import com.zjm.model.controller.response.ResTest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author White Tan
 * @description
 * @date 2020/3/12
 */
@Api(value = "TEST API", tags = {"test api"})
@RestController
@RequestMapping(value = "/es")
@Slf4j
public class TestController {

	@ApiOperation("resultNum API")
	@PostMapping("/resultNum")
	public ResTest resultNum(@RequestBody ReqTest reqTest)
	{
		log.info("1234567889法国红酒看了好久开了个会：[{}]", JSON.toJSONString(reqTest));
		return new ResTest(reqTest.getNum() * 10);
	}
}
