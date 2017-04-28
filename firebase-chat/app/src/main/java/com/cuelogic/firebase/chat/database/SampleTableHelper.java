//package com.cuelogic.firebase.chat.database;
//
///**
// * @version 1.0.0
// * @Date 14/04/2017
// */
//
//import com.cuelogic.firebase.chat.FirebaseChatMainApp;
//
///**
// * @version 1.0.0
// * @Date 15/02/2017
// */
//public class SampleTableHelper {
//
//    private static final String TAG = SampleTableHelper.class.getSimpleName();
//    private static SampleTableHelper mInstance;
//    private SampleDao sampleDao;
//
//    private SampleTableHelper() throws Exception {
//        FirebaseChatMainApp application = (FirebaseChatMainApp) FirebaseChatMainApp.getAppContext();
//        DatabaseHelper mDatabaseHelper = application.getDatabaseHelper();
//        if (mDatabaseHelper == null) {
//            throw new Exception("Database not initialized");
//        } else {
//            sampleDao = mDatabaseHelper.getSampleDao();
//        }
//    }
//
//    private static SampleTableHelper getInstance() throws Exception {
//        if (mInstance == null) {
//            mInstance = new SampleTableHelper();
//        }
//        return mInstance;
//    }
//
////    public static List<SnakeBean> getRecords() {
////        List<SnakeBean> messageRecords = new ArrayList<SnakeBean>();
////        try {
////            SnakeInfoDao snakeInfoDao = SampleTableHelper.getInstance().snakeInfoDao;
////            Query<SnakeInfo> notesQuery = snakeInfoDao.queryBuilder()
////                    //.where(Properties.FirstName.eq("Joe"))
////                    .orderAsc(SnakeInfoDao.Properties.Snake_name)
////                    .build();
////            List records = notesQuery.list();
////            for (int i = 0; i < records.size(); i++) {
////                SnakeInfo message = (SnakeInfo) records.get(i);
////                SnakeBean messageBean = SnakeInfoMapper.getBeanFromDbObject(message);
////                messageRecords.add(messageBean);
////            }
////        } catch (Exception ex) {
////            ex.printStackTrace();
////        }
////        return messageRecords;
////    }
//
////    public static void insertRecords(List<SnakeBean> chatRecords) {
////        try {
////            List<SnakeInfo> messageRecords = new ArrayList<SnakeInfo>();
////            for (int i = 0; i < chatRecords.size(); i++) {
////                SnakeBean messageBean = chatRecords.get(i);
////                SnakeInfo message = SnakeInfoMapper.getDbObjectFromBean(messageBean);
////                messageRecords.add(message);
////            }
////            SnakeInfoDao snakeInfoDao = SampleTableHelper.getInstance().snakeInfoDao;
////            snakeInfoDao.insertOrReplaceInTx(messageRecords); //insertInTx
////        } catch (Exception exception) {
////            exception.printStackTrace();
////        }
////    }
//
////    public static SnakeBean getSnakeInfo(String snakeUnique) {
////        try {
////            SnakeInfoDao snakeInfoDao = SampleTableHelper.getInstance().snakeInfoDao;
////            Query<SnakeInfo> notesQuery = snakeInfoDao.queryBuilder()
////                    .where(SnakeInfoDao.Properties.Snake_unique.eq(snakeUnique))
////                    .build();
////            List<SnakeInfo> records = notesQuery.list();
////            if (null != records && records.size() > 0) {
////                return SnakeInfoMapper.getBeanFromDbObject(records.get(0));
////            }
////        } catch (Exception exception) {
////            exception.printStackTrace();
////        }
////        return null;
////    }
//
////    public static int getRecordCount() {
////        try {
////            SnakeInfoDao snakeInfoDao = SampleTableHelper.getInstance().snakeInfoDao;
////            Query<SnakeInfo> notesQuery = snakeInfoDao.queryBuilder().build();
////            List records = notesQuery.list();
////            return records.size();
////        } catch (Exception ex) {
////            ex.printStackTrace();
////        }
////        return 0;
////    }
//
////    public static List<SnakePictureBean> getPictureRecords(String snakeUnique) {
////        List<SnakePictureBean> messageRecords = new ArrayList<SnakePictureBean>();
////        try {
////            SnakePictureInfoDao snakePictureInfoDao = SampleTableHelper.getInstance().snakePictureInfoDao;
////            Query<SnakePictureInfo> notesQuery = snakePictureInfoDao.queryBuilder()
////                    .where(SnakePictureInfoDao.Properties.Snake_unique.eq(snakeUnique))
////                    .orderAsc(SnakePictureInfoDao.Properties._id)
////                    .build();
////            List records = notesQuery.list();
////            for (int i = 0; i < records.size(); i++) {
////                SnakePictureInfo message = (SnakePictureInfo) records.get(i);
////                SnakePictureBean messageBean = SnakePictureMapper.getBeanFromDbObject(message);
////                messageRecords.add(messageBean);
////            }
////        } catch (Exception ex) {
////            ex.printStackTrace();
////        }
////        return messageRecords;
////    }
//
////    public static int getPictureRecordCount(String snakeUnique) {
////        try {
////            SnakePictureInfoDao snakePictureInfoDao = SampleTableHelper.getInstance().snakePictureInfoDao;
////            Query<SnakePictureInfo> notesQuery = snakePictureInfoDao.queryBuilder()
////                    .where(SnakePictureInfoDao.Properties.Snake_unique.eq(snakeUnique))
////                    .orderAsc(SnakePictureInfoDao.Properties._id)
////                    .build();
////            return notesQuery.list().size();
////        } catch (Exception ex) {
////            ex.printStackTrace();
////        }
////        return 0;
////    }
//
////    public static void insertPictureRecords(List<SnakePictureBean> chatRecords) {
////        try {
////            List<SnakePictureInfo> messageRecords = new ArrayList<SnakePictureInfo>();
////            for (int i = 0; i < chatRecords.size(); i++) {
////                SnakePictureBean messageBean = chatRecords.get(i);
////                SnakePictureInfo message = SnakePictureMapper.getDbObjectFromBean(messageBean);
////                messageRecords.add(message);
////            }
////            SnakePictureInfoDao snakeInfoDao = SampleTableHelper.getInstance().snakePictureInfoDao;
////            snakeInfoDao.insertOrReplaceInTx(messageRecords); //insertInTx
////        } catch (Exception exception) {
////            exception.printStackTrace();
////        }
////    }
//
//}
