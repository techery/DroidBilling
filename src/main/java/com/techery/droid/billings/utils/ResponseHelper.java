package com.techery.droid.billings.utils;

import android.content.Intent;
import android.os.Bundle;

import com.techery.droid.billings.Constants;

public class ResponseHelper {
    public static String getResponseDesc(int code) {
        String[] iab_msgs = ("0:OK/1:User Canceled/2:Unknown/" +
                "3:Billing Unavailable/4:Item unavailable/" +
                "5:Developer Error/6:Error/7:Item Already Owned/" +
                "8:Item not owned").split("/");
        String[] iabhelper_msgs = ("0:OK/-1001:Remote exception during initialization/" +
                "-1002:Bad response received/" +
                "-1003:Purchase signature verification failed/" +
                "-1004:Send intent failed/" +
                "-1005:User cancelled/" +
                "-1006:Unknown purchase response/" +
                "-1007:Missing token/" +
                "-1008:Unknown error/" +
                "-1009:Subscriptions not available/" +
                "-1010:Invalid consumption attempt").split("/");

        if (code <= Constants.IABHELPER_ERROR_BASE) {
            int index = Constants.IABHELPER_ERROR_BASE - code;
            if (index >= 0 && index < iabhelper_msgs.length) return iabhelper_msgs[index];
            else return String.valueOf(code) + ":Unknown IAB Helper Error";
        } else if (code < 0 || code >= iab_msgs.length) {
            return String.valueOf(code) + ":Unknown";
        } else {
            return iab_msgs[code];
        }
    }

    public int getResponseCodeFromBundle(Bundle b) {
        return getFixedResponseCodeFromObject(b.get(Constants.RESPONSE_CODE));
    }

    public int getResponseCodeFromIntent(Intent i) {
        return getFixedResponseCodeFromObject(i.getExtras().get(Constants.RESPONSE_CODE));
    }

    // Workaround to bug where sometimes response codes come as Long instead of Integer
    int getFixedResponseCodeFromObject(Object o) {
        if (o == null) {
            return Constants.BILLING_RESPONSE_RESULT.OK;
        } else if (o instanceof Integer) return ((Integer) o).intValue();
        else if (o instanceof Long) return (int) ((Long) o).longValue();
        else {
            throw new RuntimeException("Unexpected type for bundle response code: " + o.getClass().getName());
        }
    }
}
