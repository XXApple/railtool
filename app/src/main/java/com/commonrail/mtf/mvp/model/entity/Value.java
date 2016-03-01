package com.commonrail.mtf.mvp.model.entity;

import java.io.Serializable;

/**
 * Created by wengyiming on 2016/1/26.
 * <p/>
 * stepId:3,//步骤ID
 * stepNum:1,//步骤顺序
 * measResult:0.43,//测量工具结果
 * calcResult:0.67 //建议测量工具结果
 */
public class Value implements Serializable {
    private int stepId ;
    private int stepNum;
    private double measResult;
    private double calcResult;

    public int getStepId() {
        return stepId;
    }

    public void setStepId(int stepId) {
        this.stepId = stepId;
    }

    public int getStepNum() {
        return stepNum;
    }

    public void setStepNum(int stepNum) {
        this.stepNum = stepNum;
    }

    public double getMeasResult() {
        return measResult;
    }

    public void setMeasResult(double measResult) {
        this.measResult = measResult;
    }

    public double getCalcResult() {
        return calcResult;
    }

    public void setCalcResult(double calcResult) {
        this.calcResult = calcResult;
    }


    @Override
    public String toString() {
        return "Value{" +
                "stepId='" + stepId + '\'' +
                ", stepNum='" + stepNum + '\'' +
                ", measResult=" + measResult +
                ", calcResult=" + calcResult +
                '}';
    }
}
