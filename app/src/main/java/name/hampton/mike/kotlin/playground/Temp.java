package name.hampton.mike.kotlin.playground;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by michaelhampton on 6/11/20.
 */
public class Temp {
    Map<String, List> stuff = new HashMap<>();
    void fop() {
        stuff.put("", new ArrayList());

        Handler handler = null;
        ContentObserver co = new ContentObserver(handler) {
            @Override
            public boolean deliverSelfNotifications() {
                return false;
            }

            @Override
            public void onChange(boolean selfChange, Uri uri) {
            }
        };
    }
}
