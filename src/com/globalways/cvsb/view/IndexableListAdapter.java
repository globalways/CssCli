package com.globalways.cvsb.view;

import java.util.Collections;
import java.util.List;

import com.globalways.cvsb.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IndexableListAdapter extends ArrayAdapter<IndexItemInterface> {

	protected int resource; // store the resource layout id for 1 row
	private boolean inSearchMode = false;

	private ItemSectionIndexer indexer = null;

	public IndexableListAdapter(Context _context, int _resource, List<IndexItemInterface> _items) {
		super(_context, _resource, _items);
		resource = _resource;

		// need to sort the items array first, then pass it to the indexer
		Collections.sort(_items, new IndexableItemComparator());

		setIndexer(new ItemSectionIndexer(_items));

	}

	// get the section textview from row view
	// the section view will only be shown for the first item
	public TextView getSectionTextView(View rowView) {
		TextView sectionTextView = (TextView) rowView.findViewById(R.id.sectionTextView);
		return sectionTextView;
	}

	public void showSectionViewIfFirstItem(View rowView, IndexItemInterface item, int position) {
		TextView sectionTextView = getSectionTextView(rowView);

		// if in search mode then dun show the section header
		if (inSearchMode) {
			sectionTextView.setVisibility(View.GONE);
		} else {
			// if first item then show the header

			if (indexer.isFirstItemInSection(position)) {

				String sectionTitle = indexer.getSectionTitle(item.getItemForIndex());
				sectionTextView.setText(sectionTitle);
				sectionTextView.setVisibility(View.VISIBLE);

			} else
				sectionTextView.setVisibility(View.GONE);
		}
	}

	// do all the data population for the row here
	// subclass overwrite this to draw more items
	public void populateDataForRow(View parentView, IndexItemInterface item, int position) {
		// default just draw the item only
		View infoView = parentView.findViewById(R.id.viewItem);
		TextView nameView = (TextView) infoView.findViewById(R.id.productName);
		nameView.setText(item.getDisplayInfo());
	}

	// this should be override by subclass if necessary
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewGroup parentView;
		IndexItemInterface item = getItem(position);

		if (convertView == null) {
			parentView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
			vi.inflate(resource, parentView, true);
		} else {
			parentView = (LinearLayout) convertView;
		}

		showSectionViewIfFirstItem(parentView, item, position);

		populateDataForRow(parentView, item, position);

		return parentView;

	}

	public boolean isInSearchMode() {
		return inSearchMode;
	}

	public void setInSearchMode(boolean inSearchMode) {
		this.inSearchMode = inSearchMode;
	}

	public ItemSectionIndexer getIndexer() {
		return indexer;
	}

	public void setIndexer(ItemSectionIndexer indexer) {
		this.indexer = indexer;
	}

}
