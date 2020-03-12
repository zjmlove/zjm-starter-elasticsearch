package com.zjm.config.elasticSearch.dto;

import org.elasticsearch.search.aggregations.AggregationBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author White Tan
 * @description
 * @date 2020/3/12
 */
public class ElasticQueryDto {
	private ElasticQueryPageDto queryPageDto;
	private Map<String, String> sidx;
	private boolean checkIndex = false;
	private String[] indices;
	private String[] includeFields;
	private String[] excludeFields;
	private List<ElasticQueryOperatorDto> operators;
	private String[] searchAfter;
	private AggregationBuilder aggregationBuilder;

	public ElasticQueryDto() {
	}

	public ElasticQueryPageDto getQueryPageDto() {
		return this.queryPageDto;
	}

	public Map<String, String> getSidx() {
		return this.sidx;
	}

	public boolean isCheckIndex() {
		return this.checkIndex;
	}

	public String[] getIndices() {
		return this.indices;
	}

	public String[] getIncludeFields() {
		return this.includeFields;
	}

	public String[] getExcludeFields() {
		return this.excludeFields;
	}

	public List<ElasticQueryOperatorDto> getOperators() {
		return this.operators;
	}

	public String[] getSearchAfter() {
		return this.searchAfter;
	}

	public AggregationBuilder getAggregationBuilder() {
		return this.aggregationBuilder;
	}

	public void setQueryPageDto(final ElasticQueryPageDto queryPageDto) {
		this.queryPageDto = queryPageDto;
	}

	public void setSidx(final Map<String, String> sidx) {
		this.sidx = sidx;
	}

	public void setCheckIndex(final boolean checkIndex) {
		this.checkIndex = checkIndex;
	}

	public void setIndices(final String[] indices) {
		this.indices = indices;
	}

	public void setIncludeFields(final String[] includeFields) {
		this.includeFields = includeFields;
	}

	public void setExcludeFields(final String[] excludeFields) {
		this.excludeFields = excludeFields;
	}

	public void setOperators(final List<ElasticQueryOperatorDto> operators) {
		this.operators = operators;
	}

	public void setSearchAfter(final String[] searchAfter) {
		this.searchAfter = searchAfter;
	}

	public void setAggregationBuilder(final AggregationBuilder aggregationBuilder) {
		this.aggregationBuilder = aggregationBuilder;
	}


	public String toString() {
		return "ElasticQueryDto(queryPageDto=" + this.getQueryPageDto() + ", sidx=" + this.getSidx() + ", checkIndex=" + this.isCheckIndex() + ", indices=" + Arrays.deepToString(this.getIndices()) + ", includeFields=" + Arrays.deepToString(this.getIncludeFields()) + ", excludeFields=" + Arrays.deepToString(this.getExcludeFields()) + ", operators=" + this.getOperators() + ", searchAfter=" + Arrays.deepToString(this.getSearchAfter()) + ", aggregationBuilder=" + this.getAggregationBuilder() + ")";
	}
}
