package com.zjm.model.controller.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author White Tan
 * @description
 * @date 2020/3/12
 */
@ApiModel("Test 返回参数对象")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResTest {
	@ApiModelProperty("结果数字")
	private Integer resultNum;
}
