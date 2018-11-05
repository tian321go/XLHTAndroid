package com.axeac.android.sdk.provider;

import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.net.Uri;

/**
 * 自定义简单搜索建议提供器
 * @author axeac
 * @version 1.0.0
 * */
public class MapSuggestionProvider extends SearchRecentSuggestionsProvider {

	public final static String AUTHORITY = "cn.knowhowsoft.khmap5.MapSuggestionProvider";
	public final static int MODE = DATABASE_MODE_QUERIES;

	public MapSuggestionProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}

	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor cursor = super.query(uri, projection, selection, selectionArgs,
				sortOrder);
		return cursor;
	}
}