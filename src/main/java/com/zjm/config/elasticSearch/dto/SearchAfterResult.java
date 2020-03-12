package com.zjm.config.elasticSearch.dto;

import java.util.List;
import java.util.Map;

/**
 * @author White Tan
 * @description
 * @date 2020/3/12
 */
public class SearchAfterResult {
	private String searchAfter;
	private List<Map<String, Object>> searchResult;

	public SearchAfterResult() {
	}

	public SearchAfterResult(String searchAfter, List<Map<String, Object>> searchResult) {
		this.searchAfter = searchAfter;
		this.searchResult = searchResult;
	}

	public String getSearchAfter() {
		return this.searchAfter;
	}

	public void setSearchAfter(String searchAfter) {
		this.searchAfter = searchAfter;
	}

	public List<Map<String, Object>> getSearchResult() {
		return this.searchResult;
	}

	public void setSearchResult(List<Map<String, Object>> searchResult) {
		this.searchResult = searchResult;
	}
}
