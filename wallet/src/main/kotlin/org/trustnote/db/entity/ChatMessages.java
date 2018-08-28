package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import org.trustnote.superwallet.R;
import org.trustnote.superwallet.TApp;
import org.trustnote.superwallet.biz.msgs.MessageModel;
import org.trustnote.superwallet.biz.msgs.TMessageType;

import java.lang.Integer;
import java.lang.String;

@Entity(
        tableName = "chat_messages"
)
public class ChatMessages extends TBaseEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(
            name = "id"
    )
    public Integer id;

    @ColumnInfo(
            name = "correspondent_address"
    )
    public String correspondentAddress;

    @ColumnInfo(
            name = "message"
    )
    public String message;

    @ColumnInfo(
            name = "creation_date"
    )
    public long creationDate;

    @ColumnInfo(
            name = "is_incoming"
    )
    public int isIncoming;

    @ColumnInfo(
            name = "type"
    )
    public String type;

    @ColumnInfo(
            name = "is_read"
    )
    public int isRead;

    @Ignore
    public String correspondentName;

    @Ignore
    public int unReadMsgsNumber;


    //TODO:
    @Ignore
    public int msgUiType = 0;

    @Ignore
    public boolean showTimeOrDate = false;

    @Ignore
    public boolean alreadyComputeForShow = false;


    public static ChatMessages createIncomingMessage(String message, String from) {
        ChatMessages res = new ChatMessages();
        res.creationDate = System.currentTimeMillis() / 1000;
        res.correspondentAddress = from;
        res.message = message;
        res.type = TMessageType.text.name();
        res.isIncoming = 1;
        return res;
    }

    public static ChatMessages createOutMessage(String message, String from) {
        ChatMessages res = new ChatMessages();
        res.creationDate = System.currentTimeMillis() / 1000;
        res.correspondentAddress = from;
        res.message = message;
        res.type = TMessageType.text.name();
        res.isIncoming = 0;
        return res;
    }


    public static ChatMessages createSystemMessage(String message, String from) {
        ChatMessages res = new ChatMessages();
        res.creationDate = System.currentTimeMillis() / 1000;
        res.correspondentAddress = from;
        res.message = message;
        res.type = TMessageType.system.name();
        res.isIncoming = 0;
        return res;
    }

}
