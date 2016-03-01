package com.commonrail.mtf.mvp.model.entity;

import java.io.Serializable;

/**
 * 项目名称：railtool
 * 类描述：
 * <p/>
 * stepId:"1", //步骤ID
 * stepOrder:1, //步骤顺序
 * stepName:"装入阀组件，旋紧紧固螺丝", //步骤名称
 * dispStepName:"装入阀组件，旋紧紧固螺丝", //步骤显示名称
 * measToolNum:"G01", //选用量具编号
 * measToolPic:"/images/g01.jpg", //选用量具编号图片URL
 * picUrl:"/images/crin2/pic_4.jpg", //测量示意图地址
 * videoPicUrl:"/images/img03.jpg", //测量方法演示封面图片地址
 * videoUrl:"/video/1.mp4",  //测量方法演示的视频地址
 * testSpec:"步骤：<p>❶ 将喷油器体夹持在专用台虎钳上；</p><p>❷ 依次将阀半球和衔铁插入座孔；</p>" //测量步骤及要点
 * measDisp:"h1测量值[mm]", //测量值显示
 * suggestDisp:"建议衔铁升程垫片厚度为[mm]", //建议显示
 * angle:"13", //角度
 * mkz:"23", //门槛值
 * ljfw:"60 - 85", //力矩范围
 * lj:"70 - 75", //力矩
 * suggestCalcFun:"crin1Formula3", //计算方法
 * pageType:"1" //页面类型 1,2
 * <p/>
 * <p/>
 * 创建人：wengyiming
 * 创建时间：16/1/11 下午11:44
 * 修改人：wengyiming
 * 修改时间：16/1/11 下午11:44
 * 修改备注：
 */
public class Step implements Serializable {
    private String stepId = "1";
    private String stepOrder = "1";
    private String stepName = "";
    private String dispStepName = "";
    private String measToolNum = "";

    private String showPicUrl = "";

    private String measToolPic = "";
    private String picUrl = "";
    private String videoUrl = "";
    private String testSpec = "";
    private String measDisp = "";


    private String suggestDisp = "";
    private String angle = "";
    private String mkz = "";
    private String ljfw = "";
    private String lj = "";
    private String suggestCalcFun = "";
    private String pageType = "";

    private String measKey = "";
    private String measRange = "";
    private String sgstRange = "";

    public String getMeasKey() {
        return measKey;
    }

    public void setMeasKey(final String mMeasKey) {
        measKey = mMeasKey;
    }

    public String getMeasRange() {
        return measRange;
    }

    public void setMeasRange(final String mMeasRange) {
        measRange = mMeasRange;
    }

    public String getSgstRange() {
        return sgstRange;
    }

    public void setSgstRange(final String mSgstRange) {
        sgstRange = mSgstRange;
    }

    @Override
    public String toString() {
        return "Step{" +
                "stepId='" + stepId + '\'' +
                ", stepOrder='" + stepOrder + '\'' +
                ", stepName='" + stepName + '\'' +
                ", dispStepName='" + dispStepName + '\'' +
                ", measToolNum='" + measToolNum + '\'' +
                ", showPicUrl='" + showPicUrl + '\'' +
                ", measToolPic='" + measToolPic + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", testSpec='" + testSpec + '\'' +
                ", measDisp='" + measDisp + '\'' +
                ", suggestDisp='" + suggestDisp + '\'' +
                ", angle='" + angle + '\'' +
                ", mkz='" + mkz + '\'' +
                ", ljfw='" + ljfw + '\'' +
                ", lj='" + lj + '\'' +
                ", suggestCalcFun='" + suggestCalcFun + '\'' +
                ", pageType='" + pageType + '\'' +
                '}';
    }

    public String getStepId() {
        return stepId;
    }

    public void setStepId(final String mStepId) {
        stepId = mStepId;
    }

    public String getStepOrder() {
        return stepOrder;
    }

    public void setStepOrder(final String mStepOrder) {
        stepOrder = mStepOrder;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(final String mStepName) {
        stepName = mStepName;
    }

    public String getDispStepName() {
        return dispStepName;
    }

    public void setDispStepName(final String mDispStepName) {
        dispStepName = mDispStepName;
    }

    public String getMeasToolNum() {
        return measToolNum;
    }

    public void setMeasToolNum(final String mMeasToolNum) {
        measToolNum = mMeasToolNum;
    }

    public String getMeasToolPic() {
        return measToolPic;
    }

    public void setMeasToolPic(final String mMeasToolPic) {
        measToolPic = mMeasToolPic;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(final String mPicUrl) {
        picUrl = mPicUrl;
    }


    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(final String mVideoUrl) {
        videoUrl = mVideoUrl;
    }

    public String getTestSpec() {
        return testSpec;
    }

    public void setTestSpec(final String mTestSpec) {
        testSpec = mTestSpec;
    }

    public String getMeasDisp() {
        return measDisp;
    }

    public void setMeasDisp(final String mMeasDisp) {
        measDisp = mMeasDisp;
    }

    public String getSuggestDisp() {
        return suggestDisp;
    }

    public void setSuggestDisp(final String mSuggestDisp) {
        suggestDisp = mSuggestDisp;
    }

    public String getAngle() {
        return angle;
    }

    public void setAngle(final String mAngle) {
        angle = mAngle;
    }

    public String getMkz() {
        return mkz;
    }

    public void setMkz(final String mMkz) {
        mkz = mMkz;
    }

    public String getLjfw() {
        return ljfw;
    }

    public void setLjfw(final String mLjfw) {
        ljfw = mLjfw;
    }

    public String getLj() {
        return lj;
    }

    public void setLj(final String mLj) {
        lj = mLj;
    }

    public String getSuggestCalcFun() {
        return suggestCalcFun;
    }

    public void setSuggestCalcFun(final String mSuggestCalcFun) {
        suggestCalcFun = mSuggestCalcFun;
    }

    public String getPageType() {
        return pageType;
    }

    public void setPageType(final String mPageType) {
        pageType = mPageType;
    }

    public String getShowPicUrl() {
        return showPicUrl;
    }

    public void setShowPicUrl(String mShowPicUrl) {
        showPicUrl = mShowPicUrl;
    }
}
