package com.commonrail.mtf.greendao.generator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;


/**
 * 此类用于生产数据库和表结构,编译时提前产生,非运行时
 */
public class RtGreenDaoGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "com.commonrail.mtf.db");
        initFilesEntuty(schema);

        new DaoGenerator().generateAll(schema, args[0]);
    }

    //生成购物车表
    private static void initFilesEntuty(Schema schema) {
        Entity files = schema.addEntity("Files");
        files.addIdProperty().notNull().primaryKey().unique();
        files.addStringProperty("fileType").notNull();
        files.addStringProperty("fileUrl").notNull();
        files.addStringProperty("fileLocalUrl").notNull();
        files.addStringProperty("fileLen").notNull();
        files.addIntProperty("fileStatus").notNull();//0 未下载,1已下载

        Entity homeItem = schema.addEntity("InjectorDb");
        homeItem.addIdProperty().notNull().primaryKey().unique();
        homeItem.addStringProperty("injectorType").notNull();
        homeItem.addIntProperty("orderNum").notNull();
        homeItem.addStringProperty("injectorName").notNull();
        homeItem.addStringProperty("iconUrl").notNull();
        
    }

//    private String injectorType = "";
//    private int orderNum = 0;
//    private String injectorName = "";
//    private String iconUrl = "";
}
