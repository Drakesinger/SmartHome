package ch.hearc.smarthome.admin;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ch.hearc.smarthome.R;

public class SHUserListBaseAdapter extends BaseAdapter
{

	private static ArrayList<SHUser>	actionArrayList;

	private LayoutInflater				mInflater;

	public SHUserListBaseAdapter(Context context, ArrayList<SHUser> results)
	{
		actionArrayList = results;
		mInflater = LayoutInflater.from(context);
	}

	public int getCount( )
	{
		return actionArrayList.size( );
	}

	public Object getItem(int position)
	{
		return actionArrayList.get(position);
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
			convertView = mInflater.inflate(R.layout.activity_list_activity_view, null);
			holder = new ViewHolder( );
			holder.tvAction = (TextView) convertView.findViewById(R.id.al_av_tv_ActivityName);
			holder.tvDescription = (TextView) convertView.findViewById(R.id.al_av_tv_ActivityDesc);

			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag( );
		}

		holder.tvAction.setText(actionArrayList.get(position).getUserName( ));
		holder.tvDescription.setText(actionArrayList.get(position).getPassword( ));

		return convertView;
	}

	static class ViewHolder
	{
		TextView	tvAction;
		TextView	tvDescription;
	}
}
