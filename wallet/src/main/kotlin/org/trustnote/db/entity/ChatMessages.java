package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import org.trustnote.wallet.util.Utils;

import java.lang.Integer;
import java.lang.String;

@Entity(
        tableName = "chat_messages"
)
public class ChatMessages extends TBaseEntity {
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

    @Ignore
    public String correspondentName;

    @Ignore
    public int unReadMsgsNumber;


    //TODO:
    @Ignore
    public int msgUiType = Utils.getRandom().nextInt(2);
}

enum ChatMessageType {
    msg_from_me,
    msg_from_you
}