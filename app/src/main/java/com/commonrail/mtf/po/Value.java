package com.commonrail.mtf.po;

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
    private String stepId ;
    private String stepNum;
    private double measResult;
    private double calcResult;

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public String getStepNum() {
        return stepNum;
    }

    public void setStepNum(String stepNum) {
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
