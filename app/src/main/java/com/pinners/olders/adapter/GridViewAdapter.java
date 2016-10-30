package com.pinners.olders.adapter;

/**
 * Created by Administrator on 2016-06-22.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinners.olders.R;
import com.pinners.olders.dto.Contact;

import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter {
    private ArrayList<Contact> listViewItemList = new ArrayList<Contact>();

    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_grid_item, parent, false);
        }

        TextView gridNameTv = (TextView)convertView.findViewById(R.id.gridNameTv);
        TextView gridPhoneNumberTv = (TextView)convertView.findViewById(R.id.gridPhoneNumberTv);
        ImageView gridPhotoIv = (ImageView)convertView.findViewById(R.id.gridPhotoIv);

        Contact listViewItem = listViewItemList.get(position);

        int random = (int) (Math.random() * 3) + 1;
        if(random == 1) gridPhotoIv.setImageResource(R.drawable.f1);
        else if(random == 2) gridPhotoIv.setImageResource(R.drawable.f2);
        else  gridPhotoIv.setImageResource(R.drawable.f3);
        gridNameTv.setText(listViewItem.getName());
        gridPhoneNumberTv.setText(listViewItem.getPhoneNumber());

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    public void addItem( String name, String phoneNumber, String favorite) {
        Contact item = new Contact();

        item.setName(name);
        item.setPhoneNumber(phoneNumber);
        item.setFavorite(favorite);

        listViewItemList.add(item);
    }
}