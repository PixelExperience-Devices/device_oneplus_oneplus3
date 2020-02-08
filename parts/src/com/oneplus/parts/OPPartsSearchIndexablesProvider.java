/*
 * Copyright (C) 2016 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oneplus.parts;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.SearchIndexableResource;
import android.provider.SearchIndexablesProvider;

import com.oneplus.parts.activities.ButtonsActivity;
import com.oneplus.parts.activities.TGesturesActivity;

import static android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_CLASS_NAME;
import static android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_ICON_RESID;
import static android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_INTENT_ACTION;
import static android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_INTENT_TARGET_CLASS;
import static android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_INTENT_TARGET_PACKAGE;
import static android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_RANK;
import static android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_RESID;
import static android.provider.SearchIndexablesContract.INDEXABLES_RAW_COLUMNS;
import static android.provider.SearchIndexablesContract.INDEXABLES_XML_RES_COLUMNS;
import static android.provider.SearchIndexablesContract.NON_INDEXABLES_KEYS_COLUMNS;

public class OPPartsSearchIndexablesProvider extends SearchIndexablesProvider {

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor queryXmlResources(String[] projection) {
        MatrixCursor matrixCursor = new MatrixCursor(INDEXABLES_XML_RES_COLUMNS);

        matrixCursor.addRow(generateResourceRef(new SearchIndexableResource(1, R.xml.fragment_buttons, ButtonsActivity.class.getName(), R.drawable.ic_launcher_round)));
        matrixCursor.addRow(generateResourceRef(new SearchIndexableResource(1, R.xml.fragment_tgestures, TGesturesActivity.class.getName(), R.drawable.ic_launcher_round)));

        return matrixCursor;
    }

    private static Object[] generateResourceRef(SearchIndexableResource searchIndexableResource) {
        Object[] ref = new Object[7];

        ref[COLUMN_INDEX_XML_RES_RANK] = searchIndexableResource.rank;
        ref[COLUMN_INDEX_XML_RES_RESID] = searchIndexableResource.xmlResId;
        ref[COLUMN_INDEX_XML_RES_CLASS_NAME] = null;
        ref[COLUMN_INDEX_XML_RES_ICON_RESID] = searchIndexableResource.iconResId;
        ref[COLUMN_INDEX_XML_RES_INTENT_ACTION] = "com.android.settings.action.EXTRA_SETTINGS";
        ref[COLUMN_INDEX_XML_RES_INTENT_TARGET_PACKAGE] = "com.oneplus.parts";
        ref[COLUMN_INDEX_XML_RES_INTENT_TARGET_CLASS] = searchIndexableResource.className;

        return ref;
    }

    @Override
    public Cursor queryRawData(String[] projection) {
        MatrixCursor matrixCursor = new MatrixCursor(INDEXABLES_RAW_COLUMNS);
        return matrixCursor;
    }

    @Override
    public Cursor queryNonIndexableKeys(String[] projection) {
        MatrixCursor matrixCursor = new MatrixCursor(NON_INDEXABLES_KEYS_COLUMNS);
        return matrixCursor;
    }
}
