package com.gali.apps.eifoyesh;

import com.orm.SugarRecord;

/**
 * Created by 1 on 4/23/2017.
 */

public class SearchHistoryItem extends SugarRecord {

    String query;
    int type;

    public SearchHistoryItem(String query, int type) {
        this.query = query;
        this.type = type;
    }

}
