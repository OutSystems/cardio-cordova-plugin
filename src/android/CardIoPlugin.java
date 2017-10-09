package com.os.mobile.cardioplugin;

import android.Manifest;
import android.content.Intent;

import android.content.pm.PackageManager;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

/**
 * Created by João Gonçalves on 23/11/15.
 */
public class CardIoPlugin extends CordovaPlugin {


    private static final int ERROR_INVALID_ARGS = 0;
    private static final int ERROR_USER_CANCEL = 1;
    private static final int ERROR_CANT_READ_CARD = 2;

    private static final int REQUEST_PERMISSION = 100;

    private static final int CARDIO_PLUGIN_SCAN_CARD_REQUEST_CODE = 0xff0;

    /* Cordova Plugin - Action to Scan Card */
    public static final String ACTION_SCAN_CARD = "scanCard";

    private CallbackContext callbackContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        if (ACTION_SCAN_CARD.equals(action)) {
            return scanCard(callbackContext, args);
        }
        return false;
    }

    /**
     * Method to launch Card Scanner
     *
     * @param callbackContext
     * @param args
     * @throws JSONException
     */
    private boolean scanCard(CallbackContext callbackContext, final JSONArray args) throws JSONException {

        if (!CardIOActivity.canReadCardWithCamera()) {
            this.cordova.requestPermission(new CordovaPlugin(){
                @Override
                public boolean onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
                    super.onRequestPermissionResult(requestCode, permissions, grantResults);

                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        return scanCardLogic(args);

                    return false;
                }
            }, REQUEST_PERMISSION, Manifest.permission.CAMERA);
        } else
            return scanCardLogic(args);

        return false;

    }

    private boolean scanCardLogic(JSONArray args) {
        if (CardIOActivity.canReadCardWithCamera()) {
            try {
                boolean requireExpiry = args.length() >= 1 ? args.getBoolean(0) : false;
                boolean requireCvv = args.length() >= 2 ? args.getBoolean(1) : false;
                boolean requirePostalCode = args.length() >= 3 ? args.getBoolean(2) : false;

                Intent scanIntent = new Intent(cordova.getActivity(), CardIOActivity.class);
                // customize these values to suit your needs.
                scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, requireExpiry); // default: false
                scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, requireCvv); // default: false
                scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, requirePostalCode); // default: false
                // scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true);
                // scanIntent.putExtra(CardIOActivity.EXTRA_KEEP_APPLICATION_THEME, true);
                this.cordova.startActivityForResult(this, scanIntent, CARDIO_PLUGIN_SCAN_CARD_REQUEST_CODE);
                return true;
            } catch (JSONException e) {
                this.respondError(ERROR_INVALID_ARGS, "Invalid args");
                return true;
            }
        } else {
            this.respondError(ERROR_CANT_READ_CARD, "Can't scan with camera.");
            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == CARDIO_PLUGIN_SCAN_CARD_REQUEST_CODE) {
            if(resultCode == CardIOActivity.RESULT_ENTRY_CANCELED || resultCode == CardIOActivity.RESULT_CANCELED) {
                respondError(ERROR_USER_CANCEL, "Cancelled by user.");
            }
            if (resultCode == CardIOActivity.RESULT_CARD_INFO && intent != null && intent.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                handleScanResult(intent);
            }
        }
    }

    /**
     *
     * @param intent
     */
    private void handleScanResult(Intent intent) {
        CreditCard scanResult = intent.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
        try {
            JSONObject jsonObject = null;
            jsonObject = new JSONObject();
            jsonObject.put("rawCardNumber", scanResult.cardNumber);
            jsonObject.put("cardType", scanResult.getCardType());
            jsonObject.put("redactedCardNumber", scanResult.getRedactedCardNumber());

            if (scanResult.isExpiryValid()) {
                jsonObject.put("expiryYear", scanResult.expiryYear);
                jsonObject.put("expiryMonth", scanResult.expiryMonth);
            }

            if (scanResult.cvv != null) {
                jsonObject.put("cvv", scanResult.cvv);
            } else {
                jsonObject.put("ccv", "");
            }

            if (scanResult.postalCode != null) {
                jsonObject.put("postalCode", scanResult.postalCode);
            } else {
                jsonObject.put("postalCode", "");
            }

            respondSuccess(jsonObject);

        } catch (JSONException e) {

        }
    }


    /**
     *
     * @param jsonObject
     */
    private void respondSuccess(JSONObject jsonObject) {
        this.callbackContext.success(jsonObject.toString());
    }

    /**
     *
     * @param errorCode
     * @param msg
     */
    private void respondError(int errorCode, String msg) {
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("error_code", errorCode);
            jsonObject.put("error_message", msg);
            this.callbackContext.error(jsonObject.toString());
        } catch (JSONException e) {

        }
    }
}
