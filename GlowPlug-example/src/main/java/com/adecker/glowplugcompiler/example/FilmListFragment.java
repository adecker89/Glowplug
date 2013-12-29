package com.adecker.glowplugcompiler.example;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.adecker.glowplugcompiler.example.model.FilmEntity;
import com.adecker.glowplugcompiler.example.model.MyFilm;
import com.adecker.glowplugcompiler.example.model.SakilaHelper;

public class FilmListFragment extends Fragment implements AbsListView.OnItemClickListener {

	private static final String SELECTION = "selection";
	private static final String SELECTION_ARGS = "selectionArgs";
	private String selection;
	private String[] selectionArgs;
	private OnFragmentInteractionListener mListener;
	/**
	 * The fragment's ListView/GridView.
	 */
	private AbsListView mListView;
	/**
	 * The Adapter which will be used to populate the ListView/GridView with
	 * Views.
	 */
	private ListAdapter mAdapter;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public FilmListFragment() {
	}

	public static FilmListFragment newInstance(String selection, String[] selectionArgs) {
		FilmListFragment fragment = new FilmListFragment();
		Bundle args = new Bundle();
		args.putString(SELECTION, selection);
		args.putStringArray(SELECTION_ARGS, selectionArgs);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			selection = getArguments().getString(SELECTION);
			selectionArgs = getArguments().getStringArray(SELECTION_ARGS);
		}
		SakilaHelper dbHelper = new SakilaHelper(getActivity());
		mAdapter = new FilmAdapter(getActivity(), FilmEntity.select(dbHelper.getReadableDatabase(), selection,
				selectionArgs));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_item, container, false);

		mListView = (AbsListView) view.findViewById(android.R.id.list);
		mListView.setAdapter(mAdapter);

		mListView.setOnItemClickListener(this);

		return view;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (null != mListener) {
			mListener.onFragmentInteraction(null);
		}
	}

	/**
	 * The default content for this Fragment has a TextView that is shown when
	 * the list is empty. If you would like to change the text, call this method
	 * to supply the text it should use.
	 */
	public void setEmptyText(CharSequence emptyText) {
		View emptyView = mListView.getEmptyView();

		if (emptyText instanceof TextView) {
			((TextView) emptyView).setText(emptyText);
		}
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p/>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onFragmentInteraction(String id);
	}

	public static class FilmAdapter extends CursorAdapter {

		private final MyFilm film;

		public FilmAdapter(Context context, Cursor c) {
			super(context, c, 0);
			film = new MyFilm();
		}

		@Override
		public void bindView(View v, Context context, Cursor cursor) {
			TextView tv = (TextView) v.findViewById(android.R.id.text1);
			tv.setText(film.fromCursor(cursor).toString());
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
		}
	}
}
