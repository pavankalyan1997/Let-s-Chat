package com.pk.letschat.DBCalls;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.widget.Toast;

import com.pk.letschat.ContactsTab;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class LetsChatDataBase extends SQLiteOpenHelper {
    SQLiteDatabase sqLiteDatabase;
    private static final String DB_NAME="letsChat.db";

    private static final int DATABASE_VERSION=5;

    //Contact Table
    private final String CREATE_CONTACT_TABLE="CREATE TABLE IF NOT EXISTS Contacts(UID  VARCHAR,NAME VARCHAR, PHONENUMBER VARCHAR)";
    private final String DROP_CONTACT_TABLE="Drop table if exists Contacts";
    private final String INSERT_CONTACT_QUERY="insert into Contacts(UID,NAME,PHONENUMBER) values(?,?,?)";
    private final String RETRIEVE_CONTACTS="select * from Contacts";
    private final String CONTACT_FROM_UID="select * from Contacts where UID=?";

    // Messages Table
    private final String CREATE_MESSAGES_TABLE="create table if not exists Messages_INDIVIDUAL(ACCOUNTID VARCHAR,TOKEN VARCHAR,TIMESTAMP VARCHAR,MESSAGE VARCHAR,TIME VARCHAR,SENDER VARCHAR,RECEIVER VARCHAR,MESSAGE_STATUS VARCHAR,MESSAGE_TYPE VARCHAR)";
    private final String DROP_MESSAGES_TABLE="Drop table if exists Messages_INDIVIDUAL";
    private final String INSERT_MESSAGE_QUERY="insert into Messages_INDIVIDUAL(ACCOUNTID,TOKEN,TIMESTAMP,MESSAGE,TIME,SENDER,RECEIVER,MESSAGE_STATUS,MESSAGE_TYPE) values (?,?,?,?,?,?,?,?,?)";
    private final String MESSAGE_FROM_TOKEN="select * from Messages_INDIVIDUAL where TOKEN=? and ACCOUNTID=?";
    private final String CHECK_MESSAGE_EXISTS="select count(*) from Messages_INDIVIDUAL where TOKEN=? and TIMESTAMP=?";

    public LetsChatDataBase(Context context){
        super(context,DB_NAME,null,DATABASE_VERSION);
        sqLiteDatabase=this.getWritableDatabase();

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CONTACT_TABLE);
        db.execSQL(CREATE_MESSAGES_TABLE);
        sqLiteDatabase=db;

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_CONTACT_TABLE);
        db.execSQL(DROP_MESSAGES_TABLE);
        onCreate(db);

    }


    public void insertDataToContacts(String uid,String name,String phoneNumber){
        SQLiteStatement prepStatement=null;
        try{
            ContactsTable contactsTable=getContactFromUID(uid);
            if(contactsTable==null){
                prepStatement=sqLiteDatabase.compileStatement(INSERT_CONTACT_QUERY);
                prepStatement.bindString(1,uid);
                prepStatement.bindString(2,name);
                prepStatement.bindString(3,phoneNumber);
                prepStatement.executeInsert();
                sqLiteDatabase.setTransactionSuccessful();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(prepStatement!=null){
                try{
                    prepStatement.close();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

        }

    }
    public ArrayList<ContactsTable> getContactsTable(){
        ArrayList<ContactsTable> contactsList=new ArrayList<>();
        Cursor cursor=null;
        try {
            cursor=sqLiteDatabase.rawQuery(RETRIEVE_CONTACTS,null);
            if(cursor.moveToFirst()){
                do{
                    contactsList.add(new ContactsTable(cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2)));

                }while (cursor.moveToNext());
            }



        }catch (Exception e){
            e.printStackTrace();
        }finally {
            cursor.close();

        }
        return contactsList;

    }
    public ContactsTable getContactFromUID(String uid){
        ContactsTable contact=null;
        Cursor cursor=null;
        try {
            cursor=sqLiteDatabase.rawQuery(CONTACT_FROM_UID,new String[]{uid});
            if(cursor.moveToFirst()){
                do{
                    contact=new ContactsTable();
                    contact.setUid(cursor.getString(0));
                    contact.setName(cursor.getString(1));
                    contact.setPhoneNumber(cursor.getString(2));
                }while (cursor.moveToNext());
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            cursor.close();
        }

        return contact;
    }
    public boolean checkMessage(String token,String timeStamp){
        int count=0;
        Cursor cursor=null;
        try{
            cursor=sqLiteDatabase.rawQuery(CHECK_MESSAGE_EXISTS,new String[]{token,timeStamp});
            if(cursor.moveToNext()){
                do{
                    count=cursor.getInt(0);

                }while (cursor.moveToNext());
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            cursor.close();
        }
        if(count==0){
            return false;
        }
        else return true;
    }

    public ArrayList<MessagesTable> getMessagesOfToken(String token,String accountID){
        ArrayList<MessagesTable> messageList=new ArrayList<>();
        MessagesTable table=null;
        Cursor cursor=null;
        try{
            cursor=sqLiteDatabase.rawQuery(MESSAGE_FROM_TOKEN,new String[]{token,accountID});
            if(cursor.moveToFirst()){
                table=new MessagesTable();
                do{
                    table.setAccountID(cursor.getString(0));
                    table.setToken(cursor.getString(1));
                    table.setTimeStamp(cursor.getString(2));
                    table.setMessage(cursor.getString(3));
                    table.setTIME(cursor.getString(4));
                    table.setSENDER(cursor.getString(5));
                    table.setRECEIVER(cursor.getString(6));
                    table.setMESSAGE_STATUS(cursor.getString(7));
                    table.setMESSAGE_TYPE(cursor.getString(8));
                    messageList.add(table);

                }while (cursor.moveToNext());
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor!=null){
                try{
                    cursor.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return messageList;
    }
    public void insertMessages(MessagesTable messagesTable){
        SQLiteStatement prepStatement=null;
        try{
            //MessagesTable existingRecord=getContactFromUID(messagesTable.getToken());
            prepStatement=sqLiteDatabase.compileStatement(INSERT_MESSAGE_QUERY);
            prepStatement.bindString(1,messagesTable.getAccountID());
            prepStatement.bindString(2,messagesTable.getToken());
            prepStatement.bindString(3,messagesTable.getTimeStamp());
            prepStatement.bindString(4,messagesTable.getMessage());
            prepStatement.bindString(5,messagesTable.getTIME());
            prepStatement.bindString(6,messagesTable.getSENDER());
            prepStatement.bindString(7,messagesTable.getRECEIVER());
            prepStatement.bindString(8,messagesTable.getMESSAGE_STATUS());
            prepStatement.bindString(9,messagesTable.getMESSAGE_TYPE());
            prepStatement.executeInsert();
            sqLiteDatabase.setTransactionSuccessful();
            Log.i("Message Insertion","Success");

        }catch (Exception e){
            e.printStackTrace();
            Log.i("Message Insertion","failed");
        }finally {
            if(prepStatement!=null){
                try{
                    prepStatement.close();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

        }




    }


}
