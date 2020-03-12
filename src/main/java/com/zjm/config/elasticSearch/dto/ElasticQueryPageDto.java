package com.zjm.config.elasticSearch.dto;

/**
 * @author White Tan
 * @description
 * @date 2020/3/12
 */
public class ElasticQueryPageDto {
	private int currPage = 1;
	private int limit = 100;
	private int offset = 0;

	public ElasticQueryPageDto() {
	}

	public int getCurrPage() {
		return this.currPage;
	}

	public int getLimit() {
		return this.limit;
	}

	public int getOffset() {
		return this.offset;
	}

	public void setCurrPage(final int currPage) {
		this.currPage = currPage;
	}

	public void setLimit(final int limit) {
		this.limit = limit;
	}

	public void setOffset(final int offset) {
		this.offset = offset;
	}

	public String toString() {
		return "ElasticQueryPageDto(currPage=" + this.getCurrPage() + ", limit=" + this.getLimit() + ", offset=" + this.getOffset() + ")";
	}
}
