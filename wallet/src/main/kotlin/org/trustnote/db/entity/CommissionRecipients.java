package org.trustnote.db.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommissionRecipients {

    @Expose
    @SerializedName("address")
    public String address;

    @Expose
    @SerializedName("earned_headers_commission_share")
    public int share = 100;

}
