package com.commonrail.mtf.greendao.generator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class RtGreenDaoGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "com.commonrail.mtf.db");
        initFilesEntuty(schema);

        new DaoGenerator().generateAll(schema, args[0]);
    }

    //生成购物车表
    private static void initFilesEntuty(Schema schema) {
        Entity trip = schema.addEntity("Files");
        trip.addIdProperty().notNull().primaryKey().unique();
        trip.addStringProperty("fileType").notNull();
        trip.addStringProperty("fileUrl").notNull();
        trip.addStringProperty("fileLocalUrl").notNull();
        trip.addStringProperty("fileLen").notNull();
        trip.addIntProperty("fileStatus").notNull();//0 未下载,1已下载
    }
}
