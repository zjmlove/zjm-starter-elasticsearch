package com.zjm.model.controller.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author White Tan
 * @description
 * @date 2020/3/12
 */
@ApiModel("Test 请求参数对象")
@Data
public class ReqTest {
	@ApiModelProperty("数字")
	private Integer num;
}
