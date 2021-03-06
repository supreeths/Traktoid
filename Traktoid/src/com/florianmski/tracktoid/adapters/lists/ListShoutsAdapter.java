package com.florianmski.tracktoid.adapters.lists;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.adapters.AdapterInterface;
import com.jakewharton.trakt.entities.Shout;

public class ListShoutsAdapter extends BaseAdapter implements AdapterInterface
{
	private List<Shout> shouts;
	private Context context;
	private Bitmap placeholder = null;

	public ListShoutsAdapter(List<Shout> shouts, Context context)
	{
		this.shouts = shouts;
		this.context = context;
		placeholder = BitmapFactory.decodeResource(context.getResources(), R.drawable.empty);
	}

	@Override
	public void clear()
	{
		this.shouts.clear();
		this.notifyDataSetChanged();
	}

	public void reload(List<Shout> shouts)
	{
		this.shouts = shouts;
		this.notifyDataSetChanged();
	}

	public void addShout(Shout s)
	{
		shouts.add(0, s);
		this.notifyDataSetChanged();
	}

	public void revealSpoiler(int position)
	{
		if(position < getCount() && shouts.get(position).spoiler)
		{
			shouts.get(position).spoiler = false;
			this.notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() 
	{
		return shouts.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return shouts.get(position);
	}

	@Override
	public long getItemId(int position) 
	{
		return 0;
	}

	@Override
	public int getItemViewType(int position) 
	{
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		final ViewHolder holder;

		if (convertView == null) 
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_shout, null);
			holder = new ViewHolder();
			holder.ivAvatar = (ImageView)convertView.findViewById(R.id.imageViewAvatar);
			holder.ivSpoiler = (ImageView)convertView.findViewById(R.id.imageViewSpoiler);
			holder.tvUsername = (TextView)convertView.findViewById(R.id.textViewUsername);
			holder.tvDate = (TextView)convertView.findViewById(R.id.textViewDate);
			holder.tvShout = (TextView)convertView.findViewById(R.id.textViewShout);

			convertView.setTag(holder);
		} 
		else
			holder = (ViewHolder) convertView.getTag();

		Shout s = shouts.get(position);

		final AQuery aq = new AQuery(convertView);
		BitmapAjaxCallback cb = new BitmapAjaxCallback()
		{
			@Override
			public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status)
			{     
				//                    iv.setImageBitmap(Utils.shadowBitmap(Utils.roundBitmap(bm)));
				aq.id(iv).image(Utils.shadowBitmap(Utils.roundBitmap(bm))).animate(android.R.anim.fade_in);
			}

		}.url(s.user.avatar).animation(android.R.anim.fade_in).fileCache(false).memCache(true);

		if(aq.shouldDelay(convertView, parent, s.user.avatar, 0))
			aq.id(holder.ivAvatar).image(placeholder);
		else
			aq.id(holder.ivAvatar).image(cb);

		holder.tvUsername.setText(s.user.username);
		holder.tvDate.setText(new SimpleDateFormat("EEEE dd MMMM yyyy hh:mm").format(s.inserted.getTime()));
		holder.tvShout.setText(s.shout);

		if(s.spoiler)
		{
			holder.tvShout.setVisibility(View.GONE);
			holder.ivSpoiler.setVisibility(View.VISIBLE);
		}
		else
		{
			holder.tvShout.setVisibility(View.VISIBLE);
			holder.ivSpoiler.setVisibility(View.GONE);
		}

		return convertView;
	}

	private static class ViewHolder 
	{
		private ImageView ivAvatar;
		private ImageView ivSpoiler;
		private TextView tvShout;
		private TextView tvUsername;
		private TextView tvDate;
	}
}
