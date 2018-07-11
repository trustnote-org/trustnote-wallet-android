package org.trustnote.wallet.network.pojo;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.trustnote.wallet.util.AndroidUtils;
import org.trustnote.wallet.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class WalletNewVersion {

    public String version = "";
    public boolean ignore = false;
    public JsonObject msg = Utils.INSTANCE.getEmptyJsonObject();

    public  List<String> getUpgradeItems(Context context)  {

        List<String> res = new ArrayList();

        if (msg != null) {

            JsonArray updateItems = msg.getAsJsonArray(AndroidUtils.isZh(context)? "cn" : "en");

            for(int i = 0 ; i < updateItems.size(); i++) {
                res.add((i+1) + ". " + updateItems.get(i).getAsString());
            }

        } else {

        }

        return res;
    }


}



