package com.zjm.model.controller.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author White Tan
 * @description
 * @date 2020/3/13
 */
@Data
@ApiModel("创建索引别名 - 入参")
public class ReqCreateIndexAlias {
	@ApiModelProperty("索引名称")
	private String indexName;
	@ApiModelProperty("索引别名")
	private String indexAlias;
}
