package com.framework.library.view;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class DialogFragmentListView extends DialogFragment {

	public interface SelectedListener {
		public void itemSelected(int position, DialogFragmentListView dialog);
		public void multipleAccepted(DialogFragmentListView dialog);
	}

	private ListView listView;
	private BaseAdapter adapter;
	private int resId;
	private List<String> data;
	private boolean[] dataBoolean;
	private SelectedListener selectedListener;
	private Map<String, Object> tagReference;

	private int choiceMode = ListView.CHOICE_MODE_SINGLE;

	public static DialogFragmentListView create(
			SelectedListener selectedListener, int resId, List<String> data) {
		DialogFragmentListView dialog = new DialogFragmentListView();
		dialog.selectedListener = selectedListener;
		dialog.resId = resId;
		dialog.data = data;

		return dialog;
	}

	private void initDataBoolean(int size) {
		dataBoolean = new boolean[size];
		Arrays.fill(dataBoolean, Boolean.FALSE);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setStyle(STYLE_NO_TITLE, 0);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		dismiss();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Builder builder =  new AlertDialog.Builder(getActivity())
		.setView(createView());
		
		if(choiceMode == ListView.CHOICE_MODE_MULTIPLE){
			builder.setPositiveButton(android.R.string.ok, multipleChoiceAccepted);
			
			builder.setNegativeButton(android.R.string.cancel, multipleChoiceCancelled);
		}
		
		Dialog dialog = builder.create();
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}

//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		return createView();
//	}
	
	public View createView(){
		if (choiceMode == ListView.CHOICE_MODE_MULTIPLE) {
			initDataBoolean(data.size());
		}

		LinearLayout linearLayout = new LinearLayout(getActivity());
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		linearLayout.setLayoutParams(layoutParams);

		listView = new ListView(getActivity());
		listView.setLayoutParams(layoutParams);
		final int res = resId > 0 ? resId
				: choiceMode == ListView.CHOICE_MODE_MULTIPLE ? android.R.layout.select_dialog_multichoice
						: android.R.layout.select_dialog_item;
		// listView.setAdapter(new ArrayAdapter<String>(getActivity(), res,
		// data));
		listView.setOnItemClickListener(choiceMode == ListView.CHOICE_MODE_MULTIPLE ? multipleChoiceListener
				: singleChoiceListener);
		listView.setChoiceMode(choiceMode);
		
		adapter = new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolder viewHolder = null;

				if (convertView == null) {
					viewHolder = new ViewHolder();
					viewHolder.linearLayout = new LinearLayout(getActivity());
					LayoutParams layoutParams = new LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.MATCH_PARENT);
					viewHolder.linearLayout.setLayoutParams(layoutParams);

					viewHolder.textView = (TextView) getActivity()
							.getLayoutInflater().inflate(res, null);
					viewHolder.linearLayout.addView(viewHolder.textView);

					convertView = viewHolder.linearLayout;
					convertView.setTag(viewHolder);
				} else {
					viewHolder = (ViewHolder) convertView.getTag();
				}

				viewHolder.textView.setText(data.get(position));

				if (viewHolder.textView instanceof CheckedTextView) {
					((CheckedTextView) viewHolder.textView)
							.setChecked(dataBoolean[position]);
				}

				return convertView;
			}

			@Override
			public long getItemId(int arg0) {
				return 0;
			}

			@Override
			public Object getItem(int arg0) {
				return null;
			}

			@Override
			public int getCount() {
				if(data != null){
					return data.size();	
				}else{
					return 0;
				}
			}
		};
		
		listView.setAdapter(adapter);

		linearLayout.addView(listView);
		
		return linearLayout;
	}

	static class ViewHolder {
		LinearLayout linearLayout;
		TextView textView;
	}

	private OnItemClickListener singleChoiceListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {

			itemSelected(position);
			dismiss();
		}
	};

	private OnItemClickListener multipleChoiceListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			dataBoolean[position] = !dataBoolean[position];

			((CheckedTextView) view.findViewById(android.R.id.text1))
					.setChecked(dataBoolean[position]);
		}
	};
	
	private OnClickListener multipleChoiceAccepted = new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			multipleAccepted();
		}
	};
	
	private OnClickListener multipleChoiceCancelled = new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			initDataBoolean(data.size());
			adapter.notifyDataSetChanged();
		}
	};

	public void itemSelected(int position) {
		if (selectedListener != null) {
			selectedListener.itemSelected(position, this);
		}
	}
	
	public void multipleAccepted() {
		if (selectedListener != null) {
			selectedListener.multipleAccepted(this);
		}
	}

	/*
	 * Getters and Setters
	 */

	public ListView getListView() {
		return listView;
	}

	public void setListView(ListView listView) {
		this.listView = listView;
	}

	public int getResId() {
		return resId;
	}

	public void setResId(int resId) {
		this.resId = resId;
	}

	public List<String> getData() {
		return data;
	}

	public void setData(List<String> data) {
		this.data = data;
	}

	public SelectedListener getSelectedListener() {
		return selectedListener;
	}

	public void setSelectedListener(SelectedListener selectedListener) {
		this.selectedListener = selectedListener;
	}

	public Object getTagReference(String key) {
		if (tagReference == null) {
			return null;
		} else {
			return tagReference.get(key);
		}
	}

	public void setTagReference(String key, Object tag) {
		if (tagReference == null) {
			tagReference = new HashMap<String, Object>();
		}

		this.tagReference.put(key, tag);
	}

	public int getChoiceMode() {
		return choiceMode;
	}

	public void setChoiceMode(int choiceMode) {
		this.choiceMode = choiceMode;
	}

	public boolean[] getDataBoolean() {
		return dataBoolean;
	}

	public void setDataBoolean(boolean[] dataBoolean) {
		this.dataBoolean = dataBoolean;
	}
}