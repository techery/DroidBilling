package com.techery.droid.billings.models;

import com.techery.droid.billings.Constants;
import com.techery.droid.billings.utils.ResponseHelper;

public class IabResult {
    int mResponse;
    String mMessage;

    public IabResult(int response, String message) {
        mResponse = response;
        if (message == null || message.trim().length() == 0) {
            mMessage = ResponseHelper.getResponseDesc(response);
        }
        else {
            mMessage = message + " (response: " + ResponseHelper.getResponseDesc(response) + ")";
        }
    }

    public int getResponse() { return mResponse; }
    public String getMessage() { return mMessage; }
    public boolean isSuccess() { return mResponse == Constants.BILLING_RESPONSE_RESULT.OK; }
    public boolean isFailure() { return !isSuccess(); }
    public String toString() { return "IabResult: " + getMessage(); }
}

