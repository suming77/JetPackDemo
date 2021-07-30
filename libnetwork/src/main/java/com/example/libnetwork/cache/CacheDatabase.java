package com.example.libnetwork.cache;

import com.example.libcommon.AppGlobals;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/06/24 18:27
 * @类描述 ${TODO}这里声明为抽象的，在使用的时候被注解生成IMPL，就不需要实现里面的方法了
 */
//entities数据库当中有哪些表
//必须指定版本
//exportSchema:默认是TRUE，它会导出一个json文件，包含了创建到执行的所有操作，和所有字段和描述，需要在gardle文件当中配置导出文件的位置
@Database(entities = {Cache.class}, version = 1)
//数据读取、存储时数据转换器,比如将写入时将Date转换成Long存储，读取时把Long转换Date返回
//@TypeConverters(DateConverter.class)
public abstract class CacheDatabase extends RoomDatabase {
    private static final CacheDatabase sDatabase;

    static {
        //创建内存数据库
        //但是这种数据只存在于内存中，进程被杀死后数据就会丢失
//        Room.inMemoryDatabaseBuilder()
        sDatabase = Room.databaseBuilder(AppGlobals.getApplication(), CacheDatabase.class, "jetpack_cache")
                //是否允许在主线程查询，默认是false
                .allowMainThreadQueries()
                //数据库被创建或者被打开时的回调
//                .addCallback()
                //设置查询的线程池，有默认值，通常不需要进一步设置
//        .setQueryExecutor()
                //设置数据库工厂
//        .openHelperFactory()
                //设置数据库的日志模式
//        .setJournalMode()
                //数据库升级异常之后的回滚
//.fallbackToDestructiveMigration()
                //数据库升级异常后根据指定版本回滚
//        .fallbackToDestructiveMigrationFrom()
//                .addMigrations(CacheDatabase.sMigration)
                .build();
    }


    //    static Migration sMigration = new Migration(1, 3) {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            database.execSQL("alter table teacher rename to student");
//            database.execSQL("alter table teacher add column teacher_age INTEGER NOT NULL default 0 ");
//        }
//    };


    public static CacheDatabase get() {
        return sDatabase;
    }

    public abstract CacheDao getCacheDao();
}
