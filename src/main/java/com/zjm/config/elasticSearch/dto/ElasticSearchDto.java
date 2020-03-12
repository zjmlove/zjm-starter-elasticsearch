package com.zjm.config.elasticSearch.dto;

import com.zjm.exception.RRException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.collapse.CollapseBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author White Tan
 * @description
 * @date 2020/3/12
 */
public class ElasticSearchDto {
	private ElasticQueryDto queryDto;
	private BoolQueryBuilder boolQuery;
	List<BoolQueryBuilder> buildBoolQueryByShouldList = new ArrayList();
	private AggregationBuilder aggregationBuilder;
	private String errorMsg;
	private List<FieldSortBuilder> listSort;
	private SearchResponse searchResponse;
	private CollapseBuilder collapse;

	public ElasticSearchDto(ElasticQueryDto queryDto) {
		this.queryDto = queryDto;
		if (queryDto.getIncludeFields() != null && queryDto.getIncludeFields().length != 0) {
			if (queryDto.getQueryPageDto() != null) {
				ElasticQueryPageDto queryPageDto = queryDto.getQueryPageDto();
				queryPageDto.setCurrPage(queryPageDto.getCurrPage() < 1 ? 1 : queryPageDto.getCurrPage());
				queryPageDto.setLimit(queryPageDto.getLimit());
				queryPageDto.setOffset((queryPageDto.getCurrPage() - 1) * queryPageDto.getLimit());
				queryDto.setQueryPageDto(queryPageDto);
			}

			AggregationBuilder aggregationBuilder = queryDto.getAggregationBuilder();
			if (aggregationBuilder != null) {
				this.aggregationBuilder = aggregationBuilder;
			}

		} else {
			throw new RRException("请指定需要查询的字段");
		}
	}

	public ElasticQueryDto getQueryDto() {
		return this.queryDto;
	}

	public BoolQueryBuilder getBoolQuery() {
		return this.boolQuery;
	}

	public List<BoolQueryBuilder> getBuildBoolQueryByShouldList() {
		return this.buildBoolQueryByShouldList;
	}

	public AggregationBuilder getAggregationBuilder() {
		return this.aggregationBuilder;
	}

	public String getErrorMsg() {
		return this.errorMsg;
	}

	public List<FieldSortBuilder> getListSort() {
		return this.listSort;
	}

	public SearchResponse getSearchResponse() {
		return this.searchResponse;
	}

	public CollapseBuilder getCollapse() {
		return this.collapse;
	}

	public void setQueryDto(final ElasticQueryDto queryDto) {
		this.queryDto = queryDto;
	}

	public void setBoolQuery(final BoolQueryBuilder boolQuery) {
		this.boolQuery = boolQuery;
	}

	public void setBuildBoolQueryByShouldList(final List<BoolQueryBuilder> buildBoolQueryByShouldList) {
		this.buildBoolQueryByShouldList = buildBoolQueryByShouldList;
	}

	public void setAggregationBuilder(final AggregationBuilder aggregationBuilder) {
		this.aggregationBuilder = aggregationBuilder;
	}

	public void setErrorMsg(final String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public void setListSort(final List<FieldSortBuilder> listSort) {
		this.listSort = listSort;
	}

	public void setSearchResponse(final SearchResponse searchResponse) {
		this.searchResponse = searchResponse;
	}

	public void setCollapse(final CollapseBuilder collapse) {
		this.collapse = collapse;
	}


	public String toString() {
		return "ElasticSearchDto(queryDto=" + this.getQueryDto() + ", boolQuery=" + this.getBoolQuery() + ", buildBoolQueryByShouldList=" + this.getBuildBoolQueryByShouldList() + ", aggregationBuilder=" + this.getAggregationBuilder() + ", errorMsg=" + this.getErrorMsg() + ", listSort=" + this.getListSort() + ", searchResponse=" + this.getSearchResponse() + ", collapse=" + this.getCollapse() + ")";
	}
}
