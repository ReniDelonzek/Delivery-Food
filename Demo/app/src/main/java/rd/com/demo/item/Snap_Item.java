package rd.com.demo.item;


import java.util.List;

public class Snap_Item {

    private int type;
    private String mText;
    private List<Object> mApps;
    public boolean title;

    public Snap_Item(int type, List<Object> mApps){
        this.mApps = mApps;
        this.type = type;
    }

    public Snap_Item(int type, String text, List<Object> apps, boolean title) {
        this.type = type;
        mText = text;
        mApps = apps;
        this.title = title;
    }

    public String getText(){
        return mText;
    }
    public List<Object> getApps(){
        return mApps;
    }
    public int getType() {
        return type;
    }
}
