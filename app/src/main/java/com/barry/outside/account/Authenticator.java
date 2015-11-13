package com.barry.outside.account;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.barry.outside.R;

public class Authenticator extends AbstractAccountAuthenticator {

    static final String TAG = "Authenticator";
    public static final String KEY_CLIENT_ID = "com.trubuzz.peppa.oauth.clientid";
    public static final String KEY_SECRET = "com.trubuzz.peppa.oauth.secret";

    private Context m_context;

    public Authenticator(Context context) {
        super(context);
        m_context = context;
    }

    // Editing properties is not supported
    @Override
    public Bundle editProperties(AccountAuthenticatorResponse r, String s) {
        Log.d(TAG, "editProperties");
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType,
                             String[] requiredFeatures, Bundle options) throws NetworkErrorException {
    /*    Log.d(TAG, "addAccount");
        final Intent intent = new Intent(m_context, AuthenticatorActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent); */
        return null;
    }

    // Ignore attempts to confirm credentials
    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse r, Account account, Bundle bundle)
            throws NetworkErrorException {
        Log.d(TAG, "confirmCredentials");
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType,
                               Bundle options) throws NetworkErrorException {
      /*  Log.d(TAG, "getAuthToken " + authTokenType);

        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        final AccountManager am = AccountManager.get(m_context);

        String authToken = am.peekAuthToken(account, authTokenType);

        // Lets give another try to authenticate the user
        String errorMsg = null;
        if (TextUtils.isEmpty(authToken)) {
            final String password = am.getPassword(account);
            if (password != null) {
                try {
                    authToken = fetchToken(account.name, password, authTokenType);
                    Log.d(TAG, "fetch new token " + account + " -- " + authToken);
                } catch (Exception e) {
                    errorMsg = e.getMessage();
                }
            }
        }

        // If we get an authToken - we return it
        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }

        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity.
        final Intent intent = new Intent(m_context, AuthenticatorActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        if (errorMsg != null)
            intent.putExtra(AuthenticatorActivity.ARG_MESSAGE, errorMsg);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    protected String fetchToken(String name, String passwd, String authTokenType) throws Exception {
        ApplicationInfo ai;
        try {
            ai = m_context.getPackageManager().getApplicationInfo(m_context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }

        Bundle bundle = ai.metaData;
        ContentValues cv = new ContentValues();
        cv.put("grant_type", "password");
        cv.put("client_id", bundle.getString(KEY_CLIENT_ID));
        cv.put("client_secret", bundle.getString(KEY_SECRET));
        cv.put("redirect_uri", m_context.getString(R.string.oauth_redirect_url));
        cv.put("scope", "basic");
        cv.put("username", name);
        cv.put("password", passwd);

        URL url;
        try {
            url = new URL(Uri.parse(m_context.getString(R.string.oauth_server_url)).buildUpon()
                    .appendPath("oauth").appendPath("access-token").toString());
        } catch (MalformedURLException e) {
            return null;
        }

        ConnectBuilder b = new ConnectBuilder(m_context);
        b.setUrl(url.toString());
        b.setMethod("POST");
        b.setBody(cv);
        b.open();

        Bundle resp = b.getResponse();
        if (resp != null) {
            if (resp.containsKey(ConnectBuilder.RESPONSE_ERROR))
                throw new Exception(resp.getString(ConnectBuilder.RESPONSE_ERROR));
            if (resp.containsKey(ConnectBuilder.RESPONSE_UNAUTHORIZED))
                throw new Exception(ConnectBuilder.RESPONSE_UNAUTHORIZED);
            if (resp.containsKey(ConnectBuilder.RESPONSE_SUCCESS)) {
                JSONObject jo;
                try {
                    jo = new JSONObject(resp.getString(ConnectBuilder.RESPONSE_SUCCESS));
                    return jo.optString("access_token");
                } catch (JSONException e) {
                    return null;
                }
            }
        } */
        return null;
    }

    @Override
    public String getAuthTokenLabel(String s) {
        Log.d(TAG, "getAuthTokenLabel");
        return m_context.getString(R.string.app_name);
    }

    // Updating user credentials is not supported
    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse r, Account account, String s, Bundle bundle)
            throws NetworkErrorException {
        Log.d(TAG, "updateCredentials");
        throw new UnsupportedOperationException();
    }

    // Checking features for the account is not supported
    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse r, Account account, String[] strings)
            throws NetworkErrorException {
        Log.d(TAG, "hasFeatures");
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle getAccountRemovalAllowed(AccountAuthenticatorResponse response, Account account) throws NetworkErrorException {
        Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, true);
        return result;
    }
}