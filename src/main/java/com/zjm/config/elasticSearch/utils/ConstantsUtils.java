package com.zjm.config.elasticSearch.utils;

/**
 * @author White Tan
 * @description
 * @date 2020/3/12
 */
public class ConstantsUtils {
	private static String illegalStateException = "Utility class";

	public ConstantsUtils() {
	}

	public static enum LogicalRelationEnum {
		GT("gt", ">"),
		GTE("gte", ">="),
		LT("lt", "<"),
		LTE("lte", "<=");

		private String value;
		private String describe;

		private LogicalRelationEnum(String value, String describe) {
			this.value = value;
			this.describe = describe;
		}

		public String getValue() {
			return this.value;
		}

		public String getDescribe() {
			return this.describe;
		}
	}

	public static enum QueryTypeEnum {
		TERM_QUERY("eq", "termQuery"),
		IN_TERMS("in", "termsQuery"),
		RANGE_QUERY("rangeQuery", "rangeQuery"),
		SHOULD_IN_TERM("sin", "shouldInTerm"),
		SHOULD_IN_WILDCARD("shouldInWildcard", "shouldInWildcard"),
		MUST_NOT_TERM("notIn", "mustNotTerm"),
		MUST_NOT_WILDCARD("mustNotWildcard", "mustNotWildcard"),
		WILDCARD_QUERY("like", "wildcardQuery"),
		QUERY_STRING_QUERY("queryStringQuery", "queryStringQuery"),
		QUERY_STRING_MULTI_QUERY("multiQuery", "queryStringValueInKeys");

		private String value;
		private String describe;

		private QueryTypeEnum(String value, String describe) {
			this.value = value;
			this.describe = describe;
		}

		public String getValue() {
			return this.value;
		}

		public String getDescribe() {
			return this.describe;
		}
	}

	public abstract class QueryPartams {
		public static final int CURR_PAGE_MIN = 1;
		public static final String ASC = "asc";
		public static final String DESC = "desc";
		public static final String INDEX = "_index";
		public static final long MAX_RESULT_COUNT = 10000L;

		private QueryPartams() {
			throw new IllegalStateException(ConstantsUtils.illegalStateException);
		}
	}
}
