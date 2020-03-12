package com.zjm.elasticsearch.controller;

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
	public ResTest resultNum(@RequestBody ReqTest reqTest){
		return new ResTest(reqTest.getNum() * 10);
	}
}
