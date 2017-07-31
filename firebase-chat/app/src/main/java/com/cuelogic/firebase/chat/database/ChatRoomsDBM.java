package com.cuelogic.firebase.chat.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.cuelogic.firebase.chat.models.RoomDetails;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Harshal Vibhandik on 12/05/17.
 */

public class ChatRoomsDBM extends DatabaseManager {
    private static String TIME_FORMAT = "h:mm a";
    private SimpleDateFormat sdfTime = new SimpleDateFormat(TIME_FORMAT);

    private static ChatRoomsDBM sInstance;

    private ChatRoomsDBM(Context context) {
        this.mContext = context;
    }
    public static ChatRoomsDBM getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ChatRoomsDBM(context.getApplicationContext());
        }
        return sInstance;
    }

    public class _ChatRooms {
        public static final String TABLE = "ChatsRooms";

        public static final String ROOM_ID = "roomId";
        public static final String UNREAD_COUNT = "unread_count";
        public static final String LAST_MESSAGE = "last_message";
        public static final String LAST_MESSAGE_TIMESTAMP = "last_message_timestamp";
        public static final String IS_MUTED = "is_muted";

        public static final String CREATE_TABLE = "create table " + TABLE
                + " (" + ROOM_ID + " TEXT PRIMARY KEY NOT NULL, "
                + UNREAD_COUNT + " INTEGER DEFAULT 0, "
                + LAST_MESSAGE + " TEXT, "
                + LAST_MESSAGE_TIMESTAMP + " INTEGER DEFAULT 0, "
                + IS_MUTED + " INTEGER DEFAULT 0);";
    }

    public void muteRooms(List<String> roomIds) {
        updateMute(roomIds, true);
    }

    public void unmuteRooms(List<String> roomIds) {
        updateMute(roomIds, false);
    }

    private void updateMute(List<String> roomIds, boolean isMuted) {
        try {
            SQLiteDatabase db = getWritableDatabase();

            for (String roomId:
                roomIds) {
                ContentValues values = new ContentValues();
                values.put(_ChatRooms.ROOM_ID, roomId);
                values.put(_ChatRooms.IS_MUTED, isMuted ? 1 : 0);

                Cursor cur = db.query(_ChatRooms.TABLE, null,
                        _ChatRooms.ROOM_ID + " = ?", new String[] { roomId },
                        null, null, null);
                if (cur.moveToFirst()) {
                    db.update(_ChatRooms.TABLE, values, _ChatRooms.ROOM_ID + " = ?", new String[] { roomId });
                } else {
                    db.insert(_ChatRooms.TABLE, null, values);
                }
                cur.close();
            }
            db.close();
        } catch(SQLException se){
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void muteRoom(String roomId) {
        updateMute(roomId, true);
    }

    public void unmuteRoom(String roomId) {
        updateMute(roomId, false);
    }

    private void updateMute(String roomId, boolean isMuted) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(_ChatRooms.ROOM_ID, roomId);
            values.put(_ChatRooms.IS_MUTED, isMuted ? 1 : 0);

            Cursor cur = db.query(_ChatRooms.TABLE, null,
                    _ChatRooms.ROOM_ID + " = ?", new String[] { roomId },
                    null, null, null);
            if (cur.moveToFirst()) {
                db.update(_ChatRooms.TABLE, values, _ChatRooms.ROOM_ID + " = ?", new String[] { roomId });
            } else {
                db.insert(_ChatRooms.TABLE, null, values);
            }
            cur.close();
            db.close();
        } catch(SQLException se){
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isMuted(String roomId) {
        boolean isMuted = false;
        try {
            SQLiteDatabase db = getWritableDatabase();

            Cursor cur = db.query(_ChatRooms.TABLE, null,
                    _ChatRooms.ROOM_ID + " = ?", new String[] { roomId },
                    null, null, null);
            if (cur.moveToFirst()) {
                isMuted = cur.getInt(cur.getColumnIndex(_ChatRooms.IS_MUTED)) == 1;
            }
            cur.close();
            db.close();
        } catch(SQLException se){
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isMuted;
    }

    public void clearCount(String roomId) {
        try {
            SQLiteDatabase db = getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(_ChatRooms.ROOM_ID, roomId);
            values.put(_ChatRooms.UNREAD_COUNT, 0);

            Cursor cur = db.query(_ChatRooms.TABLE, null,
                    _ChatRooms.ROOM_ID + " = ?", new String[] { roomId },
                    null, null, null);

            if (cur.moveToFirst()) {
                db.update(_ChatRooms.TABLE, values, _ChatRooms.ROOM_ID + " = ?", new String[] { roomId });
            } else {
                db.insert(_ChatRooms.TABLE, null, values);
            }
            cur.close();
            db.close();
        } catch(SQLException se){
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addMessage(String roomId, String message, long timestamp) {
        try {
            SQLiteDatabase db = getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(_ChatRooms.ROOM_ID, roomId);
            values.put(_ChatRooms.LAST_MESSAGE, message);
            values.put(_ChatRooms.LAST_MESSAGE_TIMESTAMP, timestamp);

            Cursor cur = db.query(_ChatRooms.TABLE, null,
                    _ChatRooms.ROOM_ID + " = ?", new String[] { roomId },
                    null, null, null);
            int count = 0;
            if (cur.moveToFirst()) {
                count = cur.getInt(cur.getColumnIndex(_ChatRooms.UNREAD_COUNT));
                count = count + 1;
                values.put(_ChatRooms.UNREAD_COUNT, count);
                db.update(_ChatRooms.TABLE, values, _ChatRooms.ROOM_ID + " = ?", new String[] { roomId });
            } else {
                count = count + 1;
                values.put(_ChatRooms.UNREAD_COUNT, count);
                db.insert(_ChatRooms.TABLE, null, values);
            }
            cur.close();
            db.close();
        } catch(SQLException se){
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateLastMessage(String roomId, String message, long timestamp) {
        try {
            SQLiteDatabase db = getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(_ChatRooms.ROOM_ID, roomId);
            values.put(_ChatRooms.LAST_MESSAGE, message);
            values.put(_ChatRooms.LAST_MESSAGE_TIMESTAMP, timestamp);

            Cursor cur = db.query(_ChatRooms.TABLE, null,
                    _ChatRooms.ROOM_ID + " = ?", new String[] { roomId },
                    null, null, null);

            if (cur.moveToFirst()) {
                db.update(_ChatRooms.TABLE, values, _ChatRooms.ROOM_ID + " = ?", new String[] { roomId });
            } else {
                db.insert(_ChatRooms.TABLE, null, values);
            }
            cur.close();
            db.close();
        } catch(SQLException se){
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RoomDetails getRoomDetails(String roomId) {
        RoomDetails roomDetails = new RoomDetails();
        try {
            SQLiteDatabase db = getWritableDatabase();

            Cursor cur = db.query(_ChatRooms.TABLE, null,
                    _ChatRooms.ROOM_ID + " = ?", new String[] { roomId },
                    null, null, null);
            if (cur.moveToFirst()) {
                roomDetails.unreadCount = cur.getInt(cur.getColumnIndex(_ChatRooms.UNREAD_COUNT));
                long time = cur.getLong(cur.getColumnIndex(_ChatRooms.LAST_MESSAGE_TIMESTAMP));
                if(time != 0) {
                    roomDetails.lastMessage = cur.getString(cur.getColumnIndex(_ChatRooms.LAST_MESSAGE));
                    roomDetails.lastMessageTime = sdfTime.format(new Date(time));
                }
                roomDetails.isMuted = cur.getInt(cur.getColumnIndex(_ChatRooms.IS_MUTED)) == 1;
            }
            db.close();
        } catch(SQLException se){
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roomDetails;
    }

    public void clear() {
        try {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(_ChatRooms.TABLE, null, null);
            db.close();
        } catch(SQLException se){
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public List<Service> getAll(long parentServiceId) {
        List<Service> services = new ArrayList<Service>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cur = db.query(_ChatRooms.TABLE, null,
                    _ChatRooms.UNREAD_COUNT + " = ? AND "+_ChatRooms.IS_MUTED +" = ?",
                    new String[] { ""+parentServiceId, "0"},
                    null, null, _ChatRooms.NAME);
            while (cur.moveToNext()) {
                services.add(get(cur));
            }
            cur.close();
            db.close();
        } catch(SQLException se){
            se.printStackTrace();
        } catch(Exception e){
            e.printStackTrace();
        }
        return services;
    }

    public Service get(long serviceId) {
        Service service = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cur = db.query(_ChatRooms.TABLE, null,
                    _ChatRooms.LAST_MESSAGE_TIMESTAMP + " = ?", new String[] { ""+serviceId },
                    null, null, null);
            if (cur.moveToFirst()) {
                service =  get(cur);
            }
            cur.close();
            db.close();
        } catch(SQLException se){
            se.printStackTrace();
        } catch(Exception e){
            e.printStackTrace();
        }
        return service;
    }

    private Service get(Cursor cur) {
        return new Service(cur.getString(cur.getColumnIndex(_ChatRooms.ROOM_ID)),
                cur.getLong(cur.getColumnIndex(_ChatRooms.UNREAD_COUNT)),
                cur.getLong(cur.getColumnIndex(_ChatRooms.LAST_MESSAGE_TIMESTAMP)),
                cur.getString(cur.getColumnIndex(_ChatRooms.NAME)),
                cur.getString(cur.getColumnIndex(_ChatRooms.DESCRIPTION)),
                cur.getInt(cur.getColumnIndex(_ChatRooms.TIME)),
                cur.getFloat(cur.getColumnIndex(_ChatRooms.PRICE)),
                cur.getString(cur.getColumnIndex(_ChatRooms.BENEFITS)),
                cur.getString(cur.getColumnIndex(_ChatRooms.FREQUENCY_OF_SERVICE)),
                cur.getLong(cur.getColumnIndex(_ChatRooms.CREATED_ON)),
                cur.getLong(cur.getColumnIndex(_ChatRooms.LAST_UPDATED)),
                cur.getInt(cur.getColumnIndex(_ChatRooms.IS_MUTED))==1 ? true : false);
    }

    public void saveAll(List<Service> services) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            for (Service service:
                    services) {
                checkAndSaveService(db, service);
            }
            db.close();
        } catch(SQLException se){
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(Service service) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            checkAndSaveService(db, service);
            db.close();
        } catch(SQLException se){
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkAndSaveService(SQLiteDatabase db, Service service) {
        ContentValues values = makeContentValues(service);
        Cursor cur = db.query(_ChatRooms.TABLE, null,
                _ChatRooms.LAST_MESSAGE_TIMESTAMP + " = ?", new String[] { ""+service.getServiceId() },
                null, null, null);
        if (cur.moveToFirst()) {
            db.update(_ChatRooms.TABLE, values, _ChatRooms.LAST_MESSAGE_TIMESTAMP + "=?", new String[] { ""+service.getServiceId() });
        } else {
            db.insert(_ChatRooms.TABLE, null, values);
        }
        cur.close();
    }

    private ContentValues makeContentValues(Service service) {
        ContentValues values = new ContentValues();
        values.put(_ChatRooms.ROOM_ID, service.getSalonId());
        values.put(_ChatRooms.LAST_MESSAGE_TIMESTAMP, service.getServiceId());
        values.put(_ChatRooms.UNREAD_COUNT, service.getParentServiceId());
        values.put(_ChatRooms.NAME, service.getServiceName());
        values.put(_ChatRooms.DESCRIPTION, service.getServiceDesc());
        values.put(_ChatRooms.PRICE, service.getPrice());
        values.put(_ChatRooms.TIME, service.getTimeInMin());
        values.put(_ChatRooms.BENEFITS, service.getBenefits());
        values.put(_ChatRooms.FREQUENCY_OF_SERVICE, service.getFrequencyOfService());
        values.put(_ChatRooms.CREATED_ON, service.getCreatedOn());
        values.put(_ChatRooms.LAST_UPDATED, service.getLastUpdated());
        values.put(_ChatRooms.IS_MUTED, service.isDeleted() ? 1 : 0);
        return values;
    }*/
}