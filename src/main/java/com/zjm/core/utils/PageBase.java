package com.zjm.core.utils;

import java.io.Serializable;
import java.util.List;

/**
 * @author White Tan
 * @description
 * @date 2020/3/12
 */
public class PageBase <T> implements Serializable {
	private static final long serialVersionUID = 1L;
	private long total;
	private long size;
	private long pages;
	private long current;
	private List<T> list;

	public PageBase(List<T> list, long total, long size, long current) {
		this.list = list;
		this.total = total;
		this.size = size;
		this.current = current;
		this.pages = (long)Math.ceil((double)total / (double)size);
	}

	public long getTotal() {
		return this.total;
	}

	public long getSize() {
		return this.size;
	}

	public long getPages() {
		return this.pages;
	}

	public long getCurrent() {
		return this.current;
	}

	public List<T> getList() {
		return this.list;
	}

	public void setTotal(final long total) {
		this.total = total;
	}

	public void setSize(final long size) {
		this.size = size;
	}

	public void setPages(final long pages) {
		this.pages = pages;
	}

	public void setCurrent(final long current) {
		this.current = current;
	}

	public void setList(final List<T> list) {
		this.list = list;
	}

	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		} else if (!(o instanceof PageBase)) {
			return false;
		} else {
			PageBase<?> other = (PageBase)o;
			if (!other.canEqual(this)) {
				return false;
			} else if (this.getTotal() != other.getTotal()) {
				return false;
			} else if (this.getSize() != other.getSize()) {
				return false;
			} else if (this.getPages() != other.getPages()) {
				return false;
			} else if (this.getCurrent() != other.getCurrent()) {
				return false;
			} else {
				Object this$list = this.getList();
				Object other$list = other.getList();
				if (this$list == null) {
					if (other$list != null) {
						return false;
					}
				} else if (!this$list.equals(other$list)) {
					return false;
				}

				return true;
			}
		}
	}

	protected boolean canEqual(final Object other) {
		return other instanceof PageBase;
	}

	public String toString() {
		return "PageBase(total=" + this.getTotal() + ", size=" + this.getSize() + ", pages=" + this.getPages() + ", current=" + this.getCurrent() + ", list=" + this.getList() + ")";
	}

	public PageBase() {
	}
}