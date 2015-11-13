package com.barry.outside.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import com.barry.outside.R;

/**
 * Created by Owner on 2015/11/13.
 */
public class AccountUtil {

    final static String DUMMY_ACCOUNT = "dummy";

    public static void createDummyAccountIfNotExist(Context context) {
        if (null == getAccount(context)) {
            addAccount(context);
        }
    }

    public static Account getAccount(Context context) {
        Account[] accounts = AccountManager.get(context).getAccountsByType(context.getString(R.string.account_type));
        if (accounts.length > 0) {
            return accounts[0];
        }
        return null;
    }

    public static void addAccount(Context context) {
        final String accountType = context.getString(R.string.account_type);
        Account account = new Account(DUMMY_ACCOUNT, accountType);
        AccountManager.get(context).addAccountExplicitly(account, "", null);
    }
}
