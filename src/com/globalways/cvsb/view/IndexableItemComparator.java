package com.globalways.cvsb.view;

import java.util.Comparator;

public class IndexableItemComparator implements Comparator<IndexItemInterface>
{

	@Override
	public int compare(IndexItemInterface lhs, IndexItemInterface rhs)
	{
		if (lhs.getItemForIndex() == null || rhs.getItemForIndex() == null)
			return -1;

		return (lhs.getItemForIndex().compareTo(rhs.getItemForIndex()));

	}

}
