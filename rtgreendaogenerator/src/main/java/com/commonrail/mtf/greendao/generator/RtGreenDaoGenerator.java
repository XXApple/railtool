package com.commonrail.mtf.greendao.generator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;


/**
 * for create db in pre runtime
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
//        files.addIdProperty().notNull().primaryKey().unique();
        files.addStringProperty("fileType").notNull();
        files.addStringProperty("fileUrl").notNull().unique();
        files.addStringProperty("fileLocalUrl").notNull();
        files.addStringProperty("fileLen").notNull();
        files.addIntProperty("fileStatus").notNull();//0 not downloaded,1download completed

        Entity homeItem = schema.addEntity("InjectorDb");
        homeItem.addStringProperty("injectorType").notNull().unique();
        homeItem.addIntProperty("orderNum").notNull();
        homeItem.addStringProperty("injectorName");
        homeItem.addStringProperty("iconUrl").notNull();
        
    }

//    private String injectorType = "";
//    private int orderNum = 0;
//    private String injectorName = "";
//    private String iconUrl = "";
}
