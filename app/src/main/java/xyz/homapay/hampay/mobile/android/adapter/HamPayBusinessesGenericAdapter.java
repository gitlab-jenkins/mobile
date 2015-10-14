package xyz.homapay.hampay.mobile.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import xyz.homapay.hampay.common.core.model.response.dto.BusinessDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public abstract class HamPayBusinessesGenericAdapter<T> extends BaseAdapter {

	public static final String TAG = HamPayBusinessesGenericAdapter.class.getName();

	protected List<BusinessDTO> businessDTOs = new ArrayList<BusinessDTO>();
	protected LayoutInflater layoutInflater;

	protected int selectedPosition;

	public HamPayBusinessesGenericAdapter(Context context) {
		this.layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return businessDTOs.size();
	}

	@Override
	public BusinessDTO getItem(int position) {
		return businessDTOs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public abstract View getView(int position, View convertView,
			ViewGroup parent);

	public List<BusinessDTO> getItems() {
		return businessDTOs;
	}

	public void setItems(List<BusinessDTO> businessDTOs) {
		this.businessDTOs = businessDTOs;
		notifyDataSetChanged();
	}

	public void addItem(BusinessDTO item) {
		businessDTOs.add(item);
		notifyDataSetChanged();
	}

	public void addItems(List<BusinessDTO> items) {
		this.businessDTOs.addAll(items);
		notifyDataSetChanged();
	}

	public void addItems(List<BusinessDTO> items, int count) {
		addItems(items.subList(0, count));
	}

	public void addItems(BusinessDTO[] array) {
		List<BusinessDTO> lst = new ArrayList<BusinessDTO>(Arrays.asList(array));
		addItems(lst);
	}

	public void addItems(BusinessDTO[] array, int count) {
		List<BusinessDTO> lst = new ArrayList<BusinessDTO>(Arrays.asList(array));
		addItems(lst, count);
	}

	public void setItem(int position, BusinessDTO item) {
		businessDTOs.set(position, item);
		notifyDataSetChanged();
	}

	public void remove(int index) {
		businessDTOs.remove(index);
		notifyDataSetChanged();
	}

	public void remove(BusinessDTO item) {
		businessDTOs.remove(item);
		notifyDataSetChanged();
	}

	public void clear() {
		businessDTOs.clear();
		notifyDataSetChanged();
	}

	public int indexOf(T item) {
		return businessDTOs.indexOf(item);
	}

	public BusinessDTO getSelectedItem() {
		if (selectedPosition < 0 || selectedPosition >= getCount()) {
			return null;
		} else {
			return businessDTOs.get(selectedPosition);
		}
	}

	public int getSelectedPosition() {
		return selectedPosition;
	}

	public void setSelectedPosition(int selectedPosition) {
		this.selectedPosition = selectedPosition;
	}
}
