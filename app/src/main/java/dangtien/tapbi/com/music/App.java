package dangtien.tapbi.com.music;

import android.app.Application;
import android.content.Context;

/**
 * Created by TienBi on 21/09/2016.
 */
public class App extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
    }
    public static Context getContext(){
        return context;
    }
}
