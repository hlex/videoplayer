package com.peachy.videoplayer;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by peachy on 10/19/17.
 */

public class PermissionUtil {
    public static boolean checkPermission(Activity activity, String permission, int requestCode) {
        int permissionCheck = ContextCompat.checkSelfPermission(activity,
                permission);

        // Here, thisActivity is the current activity
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    permission)) {

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity,
                        new String[]{permission},
                        requestCode);
                return false;
            }
        }
        return true;
    }


}
