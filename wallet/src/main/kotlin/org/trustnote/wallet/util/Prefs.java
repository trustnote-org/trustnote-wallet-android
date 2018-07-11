package org.trustnote.wallet.util;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.trustnote.wallet.TApp;
import org.trustnote.wallet.biz.me.AddressesBookDb;
import org.trustnote.wallet.biz.wallet.TProfile;
import org.trustnote.wallet.network.pojo.WalletNewVersion;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

public class Prefs {

    //TTT related logic ----------------------------------------------------------

    //Below are Biz related logic
    private static final String KEY_PROFILE = "TTTProfile";
    private static final File FILE_PROFILE = new File(TApp.context.getFilesDir(), "TTTProfile.json");
    private static final String KEY_HASH_PWD = "PWD_HASH";
    private static final String KEY_USER_AGREE = "USER_AGREE";
    private static final String KEY_DEVICE_NAME = "DEVICE_NAME";
    private static final String KEY_FINISH_CREATE_OR_RESTORE = "FINISH_CREATE_OR_RESTORE";
    private static final String KEY_ENABLE_PWD_FOR_STARTUP = "KEY_ENABLE_PWD_FOR_STARTUP";
    //private static final String KEY_MY_PAIRD_ID = "KEY_MY_PAIRD_ID";
    private static final String KEY_IS_USER_IN_FULL_RESTORE = "KEY_IS_USER_IN_FULL_RESTORE";
    private static final String KEY_LANGUAGE_DEFAULT = "KEY_LANGUAGE_DEFAULT";
    private static final String KEY_HUB_LATEST_VERSION_INFO = "KEY_HUB_LATEST_VERSION_INFO";

    private static final String KEY_TRANSFER_ADDRESSES = "TTTTransferAddresses";
    private static final File FILE_TRANSFER_ADDRESSES = new File(TApp.context.getFilesDir(), "TTTTransferAddresses.json");

    public static void writeTransferAddresses(AddressesBookDb db) {
        Utils.INSTANCE.writeJson2File(FILE_TRANSFER_ADDRESSES, db);
    }

    public static void writeUpgradeInfo(String s) {
        getInstance().write(KEY_HUB_LATEST_VERSION_INFO, s);
    }

    public static WalletNewVersion readUpgradeInfo() {
        Object res = getInstance().readObject(KEY_HUB_LATEST_VERSION_INFO, WalletNewVersion.class);
        if (res instanceof  WalletNewVersion) {
            return (WalletNewVersion)res;
        } else {
            return null;
        }
    }


    public static AddressesBookDb readTransferAddressesDb() {
        //Object res = getInstance().readObject(KEY_PROFILE, TProfile.class);
        return (AddressesBookDb) Utils.INSTANCE.readJsonFileAsObject(FILE_TRANSFER_ADDRESSES, AddressesBookDb.class);
    }

    public static boolean profileExist() {
        return FILE_PROFILE.exists() && FILE_PROFILE.length() > 13;
    }

    public static boolean removeProfile() {
        return FILE_PROFILE.exists() && FILE_PROFILE.delete();
    }

    public static void writeProfile(TProfile p) {
        //getInstance().writeObject(KEY_PROFILE, p);
        Utils.INSTANCE.writeJson2File(FILE_PROFILE, p);
    }

    public static TProfile readProfile() {
        //Object res = getInstance().readObject(KEY_PROFILE, TProfile.class);
        return (TProfile) Utils.INSTANCE.readJsonFileAsObject(FILE_PROFILE, TProfile.class);
    }

    public static void writePwdHash(String pwd) {
        getInstance().write(KEY_HASH_PWD, Utils.INSTANCE.hash(pwd));
    }

    public static Boolean isUserInFullRestore() {
        return getInstance().readBoolean(KEY_IS_USER_IN_FULL_RESTORE);
    }

    public static void saveUserInFullRestore(boolean isInFullRestore) {
        getInstance().writeBoolean(KEY_IS_USER_IN_FULL_RESTORE, isInFullRestore);
    }

    @NotNull
    public static String readPwdHash() {
        return getInstance().read(KEY_HASH_PWD);
    }

    public static Boolean pwdExist() {
        return getInstance().isExist(KEY_HASH_PWD);
    }

    public static void saveUserAgree() {
        getInstance().writeBoolean(KEY_USER_AGREE, true);
    }

    public static Boolean isUserAgree() {
        return getInstance().readBoolean(KEY_USER_AGREE, false);
    }


    public static void writeDeviceName(@NotNull String deviceName) {
        getInstance().write(KEY_DEVICE_NAME, deviceName);
    }

    public static String readDeviceName() {
        return getInstance().read(KEY_DEVICE_NAME);
    }

    public static void writeFinisheCreateOrRestore() {
        getInstance().writeBoolean(KEY_FINISH_CREATE_OR_RESTORE, true);
    }

    public static Boolean readIsFinisheCreateOrRestore() {
        return getInstance().readBoolean(KEY_FINISH_CREATE_OR_RESTORE);
    }

    public static void writeEnablepwdForStartup(boolean enabled) {
        getInstance().writeBoolean(KEY_ENABLE_PWD_FOR_STARTUP, enabled);
    }

    public static Boolean readEnablepwdForStartup() {
        return getInstance().readBoolean(KEY_ENABLE_PWD_FOR_STARTUP, true);
    }

    public static String readDefaultLanguage() {
        return getInstance().read(KEY_LANGUAGE_DEFAULT, "");
    }

    public static void writeDefaultLanguage(String language) {
        getInstance().write(KEY_LANGUAGE_DEFAULT, language);
    }

    //    public static void writeMyPairId(String myPairId) {
    //        getInstance().write(KEY_MY_PAIRD_ID, myPairId);
    //    }
    //
    //    public static String readMyPairId() {
    //        return getInstance().read(KEY_MY_PAIRD_ID);
    //    }


    //------------------------------------------------------------------
    private static final String LENGTH = "_length";
    private static final String DEFAULT_STRING_VALUE = "";
    private static final int DEFAULT_INT_VALUE = -1;
    private static final double DEFAULT_DOUBLE_VALUE = -1d;
    private static final float DEFAULT_FLOAT_VALUE = -1f;
    private static final long DEFAULT_LONG_VALUE = -1L;
    private static final boolean DEFAULT_BOOLEAN_VALUE = false;

    private static SharedPreferences sharedPreferences;
    private static Prefs prefsInstance;

    private Prefs(@NonNull Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(
                context.getPackageName() + "_preferences",
                Context.MODE_PRIVATE
        );
    }

    private Prefs(@NonNull Context context, @NonNull String preferencesName) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(
                preferencesName,
                Context.MODE_PRIVATE
        );
    }


    /**
     * @return Returns a 'Prefs' inst
     */
    public static Prefs getInstance() {
        if (prefsInstance == null) {
            throw new IllegalStateException("prefsInstance has not initialized");
        }
        return prefsInstance;
    }

    /**
     * @param context
     * @return Returns a 'Prefs' inst
     */
    public static Prefs with(@NonNull Context context) {
        if (prefsInstance == null) {
            prefsInstance = new Prefs(context);
        }
        return prefsInstance;
    }


    /**
     * @param context
     * @param forceInstantiation
     * @return Returns a 'Prefs' inst
     */
    public static Prefs with(@NonNull Context context, boolean forceInstantiation) {
        if (forceInstantiation) {
            prefsInstance = new Prefs(context);
        }
        return prefsInstance;
    }

    /**
     * @param context
     * @param preferencesName
     * @return Returns a 'Prefs' inst
     */
    public static Prefs with(@NonNull Context context, @NonNull String preferencesName) {
        if (prefsInstance == null) {
            prefsInstance = new Prefs(context, preferencesName);
        }
        return prefsInstance;
    }

    /**
     * @param context
     * @param preferencesName
     * @param forceInstantiation
     * @return Returns a 'Prefs' inst
     */
    public static Prefs with(@NonNull Context context, @NonNull String preferencesName,
                             boolean forceInstantiation) {
        if (forceInstantiation) {
            prefsInstance = new Prefs(context, preferencesName);
        }
        return prefsInstance;
    }

    // String related methods

    /**
     * @param what
     * @return Returns the stored value of 'what'
     */
    public String read(String what) {
        return sharedPreferences.getString(what, DEFAULT_STRING_VALUE);
    }

    /**
     * @param what
     * @param defaultString
     * @return Returns the stored value of 'what'
     */
    public String read(String what, String defaultString) {
        return sharedPreferences.getString(what, defaultString);
    }

    /**
     * @param where
     * @param what
     */
    public void write(String where, String what) {
        sharedPreferences.edit().putString(where, what).apply();
    }

    // int related methods

    /**
     * @param what
     * @return Returns the stored value of 'what'
     */
    public int readInt(String what) {
        return sharedPreferences.getInt(what, DEFAULT_INT_VALUE);
    }

    /**
     * @param what
     * @param defaultInt
     * @return Returns the stored value of 'what'
     */
    public int readInt(String what, int defaultInt) {
        return sharedPreferences.getInt(what, defaultInt);
    }

    /**
     * @param where
     * @param what
     */
    public void writeInt(String where, int what) {
        sharedPreferences.edit().putInt(where, what).apply();
    }

    // double related methods

    /**
     * @param what
     * @return Returns the stored value of 'what'
     */
    public double readDouble(String what) {
        if (!contains(what))
            return DEFAULT_DOUBLE_VALUE;
        return Double.longBitsToDouble(readLong(what));
    }

    /**
     * @param what
     * @param defaultDouble
     * @return Returns the stored value of 'what'
     */
    public double readDouble(String what, double defaultDouble) {
        if (!contains(what))
            return defaultDouble;
        return Double.longBitsToDouble(readLong(what));
    }

    /**
     * @param where
     * @param what
     */
    public void writeDouble(String where, double what) {
        writeLong(where, Double.doubleToRawLongBits(what));
    }

    // float related methods

    /**
     * @param what
     * @return Returns the stored value of 'what'
     */
    public float readFloat(String what) {
        return sharedPreferences.getFloat(what, DEFAULT_FLOAT_VALUE);
    }

    /**
     * @param what
     * @param defaultFloat
     * @return Returns the stored value of 'what'
     */
    public float readFloat(String what, float defaultFloat) {
        return sharedPreferences.getFloat(what, defaultFloat);
    }

    /**
     * @param where
     * @param what
     */
    public void writeFloat(String where, float what) {
        sharedPreferences.edit().putFloat(where, what).apply();
    }

    // long related methods

    /**
     * @param what
     * @return Returns the stored value of 'what'
     */
    public long readLong(String what) {
        return sharedPreferences.getLong(what, DEFAULT_LONG_VALUE);
    }

    /**
     * @param what
     * @param defaultLong
     * @return Returns the stored value of 'what'
     */
    public long readLong(String what, long defaultLong) {
        return sharedPreferences.getLong(what, defaultLong);
    }

    /**
     * @param where
     * @param what
     */
    public void writeLong(String where, long what) {
        sharedPreferences.edit().putLong(where, what).apply();
    }

    // boolean related methods

    /**
     * @param what
     * @return Returns the stored value of 'what'
     */
    public boolean readBoolean(String what) {
        return sharedPreferences.getBoolean(what, DEFAULT_BOOLEAN_VALUE);
    }

    /**
     * @param what
     * @param defaultBoolean
     * @return Returns the stored value of 'what'
     */
    public boolean readBoolean(String what, boolean defaultBoolean) {
        return sharedPreferences.getBoolean(what, defaultBoolean);
    }

    /**
     * @param where
     * @param what
     */
    public void writeBoolean(String where, boolean what) {
        sharedPreferences.edit().putBoolean(where, what).apply();
    }

    // String set methods

    /**
     * @param key
     * @param value
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void putStringSet(final String key, final Set<String> value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            sharedPreferences.edit().putStringSet(key, value).apply();
        } else {
            // Workaround for pre-HC's lack of StringSets
            putOrderedStringSet(key, value);
        }
    }

    /**
     * @param key
     * @param value
     */
    public void putOrderedStringSet(String key, Set<String> value) {
        int stringSetLength = 0;
        if (sharedPreferences.contains(key + LENGTH)) {
            // First read what the value was
            stringSetLength = readInt(key + LENGTH);
        }
        writeInt(key + LENGTH, value.size());
        int i = 0;
        for (String aValue : value) {
            write(key + "[" + i + "]", aValue);
            i++;
        }
        for (; i < stringSetLength; i++) {
            // Remove any remaining values
            remove(key + "[" + i + "]");
        }
    }

    /**
     * @param key
     * @param defValue
     * @return Returns the String Set with HoneyComb compatibility
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public Set<String> getStringSet(final String key, final Set<String> defValue) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return sharedPreferences.getStringSet(key, defValue);
        } else {
            // Workaround for pre-HC's missing getStringSet
            return getOrderedStringSet(key, defValue);
        }
    }

    /**
     * @param key
     * @param defValue
     * @return Returns the ordered String Set
     */
    public Set<String> getOrderedStringSet(String key, final Set<String> defValue) {
        if (contains(key + LENGTH)) {
            LinkedHashSet<String> set = new LinkedHashSet<>();
            int stringSetLength = readInt(key + LENGTH);
            if (stringSetLength >= 0) {
                for (int i = 0; i < stringSetLength; i++) {
                    set.add(read(key + "[" + i + "]"));
                }
            }
            return set;
        }
        return defValue;
    }

    // end related methods

    /**
     * @param key
     */
    public void remove(final String key) {
        if (contains(key + LENGTH)) {
            // Workaround for pre-HC's lack of StringSets
            int stringSetLength = readInt(key + LENGTH);
            if (stringSetLength >= 0) {
                sharedPreferences.edit().remove(key + LENGTH).apply();
                for (int i = 0; i < stringSetLength; i++) {
                    sharedPreferences.edit().remove(key + "[" + i + "]").apply();
                }
            }
        }
        sharedPreferences.edit().remove(key).apply();
    }

    /**
     * @param key
     * @return Returns if that key exists
     */
    public boolean contains(final String key) {
        return sharedPreferences.contains(key);
    }

    /**
     * Clear all the preferences
     */
    public void clear() {
        sharedPreferences.edit().clear().apply();
    }

    public void writeObject(String where, Object o) {
        write(where, Utils.INSTANCE.toGsonString(o));
    }

    public Object readObject(String what, Class cls) {
        String json = read(what);
        if (json == null || json.isEmpty()) {
            return new Object();
        }
        return Utils.INSTANCE.getGson().fromJson(json, cls);
    }

    public boolean isExist(String what) {
        String json = read(what);
        return json != null && !json.isEmpty();
    }


}