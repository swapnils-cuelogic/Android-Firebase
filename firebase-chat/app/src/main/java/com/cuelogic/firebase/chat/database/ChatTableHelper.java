package com.cuelogic.firebase.chat.database;

/**
 * @version 1.0.0
 * @Date 14/04/2017
 */

import com.cuelogic.firebase.chat.FirebaseChatMainApp;
import com.cuelogic.firebase.chat.database.model.ChatInfo;
import com.cuelogic.firebase.chat.database.model.ChatInfoDao;
import com.cuelogic.firebase.chat.database.model.ChatInfoMapper;
import com.cuelogic.firebase.chat.models.Chat;
import com.cuelogic.firebase.chat.utils.Logger;

import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0.0
 * @Date 15/02/2017
 */
public class ChatTableHelper {

    private static final String TAG = ChatTableHelper.class.getSimpleName();
    private static ChatTableHelper mInstance;
    private ChatInfoDao chatInfoDao;

    private ChatTableHelper() throws Exception {
        FirebaseChatMainApp application = (FirebaseChatMainApp) FirebaseChatMainApp.getAppContext();
        DatabaseHelper mDatabaseHelper = application.getDatabaseHelper();
        if (mDatabaseHelper == null) {
            throw new Exception("Database not initialized");
        } else {
            chatInfoDao = mDatabaseHelper.getChatInfoDao();
        }
    }

    private static ChatTableHelper getInstance() throws Exception {
        if (mInstance == null) {
            mInstance = new ChatTableHelper();
        }
        return mInstance;
    }

    public static List<Chat> getRecords() {
        List<Chat> messageRecords = new ArrayList<Chat>();
        try {
            ChatInfoDao snakeInfoDao = ChatTableHelper.getInstance().chatInfoDao;
            Query<ChatInfo> notesQuery = snakeInfoDao.queryBuilder()
                    //.where(Properties.FirstName.eq("Joe"))
                    .orderAsc(ChatInfoDao.Properties.Timestamp)
                    .build();
            List records = notesQuery.list();
            for (int i = 0; i < records.size(); i++) {
                ChatInfo message = (ChatInfo) records.get(i);
                Chat messageBean = ChatInfoMapper.getBean(message);
                messageRecords.add(messageBean);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return messageRecords;
    }

    public static List<Chat> getRecords(String senderUid, String receiverUid) {
        List<Chat> messageRecords = new ArrayList<Chat>();
        try {
            ChatInfoDao chatInfoDao = ChatTableHelper.getInstance().chatInfoDao;

            QueryBuilder<ChatInfo> queryBuilder = chatInfoDao.queryBuilder();
            queryBuilder.or(ChatInfoDao.Properties.SenderUid.eq(senderUid), ChatInfoDao.Properties.SenderUid.eq(receiverUid));
            queryBuilder.or(ChatInfoDao.Properties.ReceiverUid.eq(senderUid), ChatInfoDao.Properties.ReceiverUid.eq(receiverUid));
            queryBuilder.orderAsc(ChatInfoDao.Properties.Timestamp);

            List records = queryBuilder.list();
            for (int i = 0; i < records.size(); i++) {
                ChatInfo message = (ChatInfo) records.get(i);
                Chat messageBean = ChatInfoMapper.getBean(message);
                messageRecords.add(messageBean);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return messageRecords;
    }

    public static Chat getLastElement(String senderUid, String receiverUid) {
        List<Chat> messageRecords = new ArrayList<Chat>();
        try {
            ChatInfoDao chatInfoDao = ChatTableHelper.getInstance().chatInfoDao;

            QueryBuilder<ChatInfo> queryBuilder = chatInfoDao.queryBuilder();
            queryBuilder.or(ChatInfoDao.Properties.SenderUid.eq(senderUid), ChatInfoDao.Properties.SenderUid.eq(receiverUid));
            queryBuilder.or(ChatInfoDao.Properties.ReceiverUid.eq(senderUid), ChatInfoDao.Properties.ReceiverUid.eq(receiverUid));
            queryBuilder.orderDesc(ChatInfoDao.Properties.Timestamp);

            List records = queryBuilder.list();
            if (null != records && records.size() > 0) {
                ChatInfo chatInfo = (ChatInfo) records.get(0);
                return ChatInfoMapper.getBean(chatInfo);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    public static void insertRecords(List<Chat> chatRecords) {
        try {
            List<ChatInfo> messageRecords = new ArrayList<>();
            for (int i = 0; i < chatRecords.size(); i++) {
                Chat messageBean = chatRecords.get(i);
                ChatInfo message = ChatInfoMapper.getDbObject(messageBean);
                messageRecords.add(message);
            }
            ChatInfoDao snakeInfoDao = ChatTableHelper.getInstance().chatInfoDao;
            snakeInfoDao.insertOrReplaceInTx(messageRecords); //insertInTx
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static long addMessage(Chat messageBean) {
        long rowId = 0;
        ChatInfo message = ChatInfoMapper.getDbObject(messageBean);
        try {
            ChatInfoDao chatInfoDao = ChatTableHelper.getInstance().chatInfoDao;
            rowId = chatInfoDao.insertOrReplace(message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Logger.vLog(TAG, "addMessage: " + rowId);
        return rowId;
    }

//    public static SnakeBean getSnakeInfo(String snakeUnique) {
//        try {
//            SnakeInfoDao snakeInfoDao = SampleTableHelper.getInstance().snakeInfoDao;
//            Query<SnakeInfo> notesQuery = snakeInfoDao.queryBuilder()
//                    .where(SnakeInfoDao.Properties.Snake_unique.eq(snakeUnique))
//                    .build();
//            List<SnakeInfo> records = notesQuery.list();
//            if (null != records && records.size() > 0) {
//                return SnakeInfoMapper.getBeanFromDbObject(records.get(0));
//            }
//        } catch (Exception exception) {
//            exception.printStackTrace();
//        }
//        return null;
//    }

//    public static int getRecordCount() {
//        try {
//            SnakeInfoDao snakeInfoDao = SampleTableHelper.getInstance().snakeInfoDao;
//            Query<SnakeInfo> notesQuery = snakeInfoDao.queryBuilder().build();
//            List records = notesQuery.list();
//            return records.size();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return 0;
//    }

}
