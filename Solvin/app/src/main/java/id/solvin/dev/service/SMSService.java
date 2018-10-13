package id.solvin.dev.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import id.solvin.dev.model.basic.SMSBus;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Erick Sumargo on 3/13/2017.
 */

public class SMSService extends BroadcastReceiver {
    private Object[] pdusObj;

    private SmsMessage smsMessage;
    private String message, messageResult;
    private String[] messagePart, subMessagePart;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            messageResult = getSMSMessage(intent);
            if (messageResult != null) {
                if (messageResult.split("\\.").length != 2) {
                    return;
                } else {
                    messagePart = message.split("\\.");
                    subMessagePart = messagePart[1].split(" ");
                    if (messagePart[0].equals("Welcome to Solvin")) {
                        if (TextUtils.isDigitsOnly(subMessagePart[3])) {
                            EventBus.getDefault().post(
                                    new SMSBus(subMessagePart[3]));
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                }
            } else {
                return;
            }
        }
    }

    private String getSMSMessage(Intent intent) {
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                pdusObj = (Object[]) bundle.get("pdus");
                if (pdusObj != null) {
                    for (int i = 0; i < pdusObj.length; i++) {
                        smsMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        message = smsMessage.getDisplayMessageBody();
                    }
                }
            }
        } catch (Exception e) {
        }
        return message;
    }
}