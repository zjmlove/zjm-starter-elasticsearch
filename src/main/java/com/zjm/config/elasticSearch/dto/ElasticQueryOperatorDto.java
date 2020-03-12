package com.zjm.config.elasticSearch.dto;

import java.io.Serializable;

/**
 * @author White Tan
 * @description
 * @date 2020/3/12
 */
public class ElasticQueryOperatorDto <T> implements Serializable {
	private static final long serialVersionUID = 8223188500904314565L;
	private String key;
	private String operate;
	private T value;
	private String operateByRight;
	private String valueByRight;

	public ElasticQueryOperatorDto() {
	}

	public String getKey() {
		return this.key;
	}

	public String getOperate() {
		return this.operate;
	}

	public T getValue() {
		return this.value;
	}

	public String getOperateByRight() {
		return this.operateByRight;
	}

	public String getValueByRight() {
		return this.valueByRight;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	public void setOperate(final String operate) {
		this.operate = operate;
	}

	public void setValue(final T value) {
		this.value = value;
	}

	public void setOperateByRight(final String operateByRight) {
		this.operateByRight = operateByRight;
	}

	public void setValueByRight(final String valueByRight) {
		this.valueByRight = valueByRight;
	}

	public String toString() {
		return "ElasticQueryOperatorDto(key=" + this.getKey() + ", operate=" + this.getOperate() + ", value=" + this.getValue() + ", operateByRight=" + this.getOperateByRight() + ", valueByRight=" + this.getValueByRight() + ")";
	}
}
