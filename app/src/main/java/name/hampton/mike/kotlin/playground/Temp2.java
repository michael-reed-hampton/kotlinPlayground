package name.hampton.mike.kotlin.playground;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by michaelhampton on 6/11/20.
 */
public class Temp2 extends Temp {


    Map<String, List> stuff = new HashMap<>();

    public Temp2() {
        super();
        stuff.put("", new ArrayList());
    }

    void fop() {
        stuff.put("", new ArrayList());
    }
}
