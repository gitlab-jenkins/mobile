package com.hampay.mobile.android.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.hampay.common.core.model.response.dto.TransactionDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by amir on 7/16/15.
 */
public abstract class UserTransactionGenericAdapter<T> extends BaseAdapter {

    public static final String TAG = UserTransactionGenericAdapter.class.getName();

    protected List<TransactionDTO> transactionDTOs = new ArrayList<TransactionDTO>();
    protected LayoutInflater layoutInflater;

    protected int selectedPosition;


    public UserTransactionGenericAdapter(Context context) {
        this.layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return transactionDTOs.size();
    }

    @Override
    public TransactionDTO getItem(int position) {
        return transactionDTOs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract View getView(int position, View convertView,
                                 ViewGroup parent);

    public List<TransactionDTO> getItems() {
        return transactionDTOs;
    }

    public void setItems(List<TransactionDTO> transactionDTOs) {
        this.transactionDTOs = transactionDTOs;
        notifyDataSetChanged();
    }

    public void addItem(TransactionDTO item) {
        transactionDTOs.add(item);
        notifyDataSetChanged();
    }

    public void addItems(List<TransactionDTO> items) {
        this.transactionDTOs.addAll(items);
        notifyDataSetChanged();
    }

    public void addItems(List<TransactionDTO> items, int count) {
        addItems(items.subList(0, count));
    }

    public void addItems(TransactionDTO[] array) {
        List<TransactionDTO> lst = new ArrayList<TransactionDTO>(Arrays.asList(array));
        addItems(lst);
    }

    public void addItems(TransactionDTO[] array, int count) {
        List<TransactionDTO> lst = new ArrayList<TransactionDTO>(Arrays.asList(array));
        addItems(lst, count);
    }

    public void setItem(int position, TransactionDTO item) {
        transactionDTOs.set(position, item);
        notifyDataSetChanged();
    }

    public void remove(int index) {
        transactionDTOs.remove(index);
        notifyDataSetChanged();
    }

    public void remove(TransactionDTO item) {
        transactionDTOs.remove(item);
        notifyDataSetChanged();
    }

    public void clear() {
        transactionDTOs.clear();
        notifyDataSetChanged();
    }

    public int indexOf(T item) {
        return transactionDTOs.indexOf(item);
    }

    public TransactionDTO getSelectedItem() {
        if (selectedPosition < 0 || selectedPosition >= getCount()) {
            return null;
        } else {
            return transactionDTOs.get(selectedPosition);
        }
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

}
