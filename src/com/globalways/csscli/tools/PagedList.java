package com.globalways.csscli.tools;

import java.util.List;


/**
 * 分页列表
 * @author wyp E-mail:onebyte@qq.com
 * @version Time: 2015年7月24日 下午4:00:19
 * @param <E>
 */
public interface PagedList<E> {
	public static final int INIT_PAGE = 1;
	public void setData(boolean isInit, List<E> list);
	public int getNext_page(boolean isReload);
}
