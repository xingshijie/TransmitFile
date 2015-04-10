package com.xingshijie.transmitfile;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class FileService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     *
     */
    public FileService() {
        super("FileService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            new HttpFileServer().startFileServer();
        } catch (Exception e) {
            Log.e("", "创建文件服务器出错");
            e.printStackTrace();
        }
    }
}
