package CourseList;

import android.content.*;
import android.content.res.Resources;
import android.view.*;
import android.widget.*;

import org.umaru.miyukisyllabus.R;

import java.util.*;
import java.util.zip.Inflater;

import Data.CourseData;

/**
 * Created by Miyuki on 2016/7/29.
 */
public class CourseListAdapter extends BaseAdapter {
    private List<CourseData> data;
    private LayoutInflater mInflater;
    private CourseListOnClick listener;

    private class ViewHolder {
        public Button btn_detail;
        public TextView txt_name;
        public TextView txt_description;
    }

    public CourseListAdapter(Context c, List<CourseData> d, CourseListOnClick l) {
        mInflater = LayoutInflater.from(c);
        data = d;
        listener = l;
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.card_item, null);
            holder = new ViewHolder();
            holder.btn_detail = (Button) convertView.findViewById(R.id.submit);
            holder.btn_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(v, position);
                }
            });
            holder.txt_description = (TextView) convertView.findViewById(R.id.description);
            holder.txt_name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txt_name.setText((String)data.get(position).getName());
        holder.txt_description.setText((String)data.get(position).getDescription());
        return convertView;
    }
}
