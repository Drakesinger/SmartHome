package ch.hearc.smarthome.bluetooth;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ch.hearc.smarthome.R;

/**
 * Device list base adapter to show the devices in a custom ListView.
 */
public class SHDeviceListBaseAdapter extends BaseAdapter
{
	private ArrayList<SHDevice>	deviceArrayList;

	private LayoutInflater		mInflater;

	public SHDeviceListBaseAdapter(Context context, ArrayList<SHDevice> results)
	{
		deviceArrayList = results;
		mInflater = LayoutInflater.from(context);
	}

	public int getCount( )
	{
		return deviceArrayList.size( );
	}

	public Object getItem(int position)
	{
		return deviceArrayList.get(position);
	}

	public long getItemId(int position)
	{
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;

		if(convertView == null)
		{
			convertView = mInflater.inflate(R.layout.device_list_row_view, null);
			holder = new ViewHolder( );
			holder.tvName = (TextView) convertView.findViewById(R.id.device_list_row_view_tv_name);
			holder.tvAddress = (TextView) convertView.findViewById(R.id.device_list_row_view_tv_address);

			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag( );
		}

		holder.tvName.setText(deviceArrayList.get(position).getName( ));
		holder.tvAddress.setText(deviceArrayList.get(position).getAddress( ));
		return convertView;
	}

	static class ViewHolder
	{
		TextView	tvName;
		TextView	tvAddress;
	}
}
