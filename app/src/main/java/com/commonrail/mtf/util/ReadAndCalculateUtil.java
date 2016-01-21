package com.commonrail.mtf.util;

import android.text.TextUtils;

import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;


public class ReadAndCalculateUtil {

    protected static Map<String, String> DATA_MAP = new HashMap<String, String>();

    public static String READ_KEY = "readKey";

    public static String READ_VALUE = "readValue";

    public static String CALC_KEY = "calcKey";

    public static String CALC_VALUE = "calcValue";

    public static final String INIT_VALUE = "0";

    public static void init() {
        DATA_MAP.clear();
        DATA_MAP.put(READ_KEY, null);
        DATA_MAP.put(CALC_KEY, null);
    }

    public static String setReadKey(String readKey) {
        DATA_MAP.put(READ_KEY, readKey);
        if (null != DATA_MAP.get(readKey)) {
            return DATA_MAP.get(readKey);
        } else {
            return INIT_VALUE;
        }
    }

    public static String setCalcKey(String calcKey) {
        DATA_MAP.put(CALC_KEY, calcKey);
        if (null != DATA_MAP.get(calcKey)) {
            return DATA_MAP.get(calcKey);
        } else {
            return INIT_VALUE;
        }
    }

    public static Map<String, Object> handleReadValue(String readValue) {
        try {
            if (!TextUtils.isEmpty(readValue)) {
                String measuredValue = readValue.trim().replace(" ", "").replace("&", "").replace("mm", "").replace("-", "");
                measuredValue = format(measuredValue);

                String readKey = DATA_MAP.get(READ_KEY);
                DATA_MAP.put(readKey, measuredValue);

                Map<String, String> map = new HashMap<String, String>();
                map.put(READ_VALUE, measuredValue);//先将测量值保存到内存，才能计算
                String calcKey = DATA_MAP.get(CALC_KEY);
                if (null != calcKey) {
//                    Map<String, Object> mStringObjectMap = calc(calcKey);//计算建议值的计算结果
                    return calc(calcKey);//计算建议值的计算结果
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected static String format(String value) {
        Double myNumber = Double.valueOf(value);
        NumberFormat format = NumberFormat.getInstance();
        format.setMinimumFractionDigits(3);
        format.setMaximumFractionDigits(3);
        format.setGroupingUsed(false);
        return format.format(myNumber);
    }

    protected static Map<String, Object> calc(String method) throws Exception {
        Method m = ReadAndCalculateUtil.class.getMethod(method);
        ReadAndCalculateUtil methobj = new ReadAndCalculateUtil();
        Object obj = m.invoke(methobj);
        Double value = (Double) obj;
        String calcValue = format(value.toString());
        DATA_MAP.put(method, calcValue);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(CALC_VALUE, calcValue);//得到建议值的计算结果
        return map;

//        ReadDataWebsocket.sendMsg(JSON.toJSONString(map));
    }

    //CRIN2
    static final double CRIN2_AH = 0.052;
    static final double CRIN2_OL = 0.040;
    static final double CRIN2_RAG = 0.050;
    static final double CRIN2_VF = 67.0;
    static final double CRIN2_NF = 34.0;
    static final double CRIN2_NL = 0.250;
    //CRIN1
    static final double CRIN1_AH = 0.060;
    static final double CRIN1_OL = 0.040;
    static final double CRIN1_RAG = 0.050;
    static final double CRIN1_VF = 58.0;
    static final double CRIN1_NF = 50.0;
    static final double CRIN1_NL = 0.255;
    //CRIN1.6
    static final double CRIN1_6_AH = 0.050;
    static final double CRIN1_6_OL = 0.040;
    static final double CRIN1_6_RAG = 0.050;
    static final double CRIN1_6_VF = 67.0;
    static final double CRIN1_6_NF = 34.0;
    static final double CRIN1_6_NL = 0.200;
    //CRIN2-L
    static final double CRIN2_L_AH = 0.050;
    static final double CRIN2_L_OL = 0.020;
    static final double CRIN2_L_RAG = 0.050;
    static final double CRIN2_L_VF = 78.0;
    static final double CRIN2_L_NF = 33.0;
    static final double CRIN2_L_NL = 0.200;
    //CRI2-994
    static final double CRI2_994_AH = 0.040;
    static final double CRI2_994_OL = 0.020;
    static final double CRI2_994_RAG = 0.055;
    static final double CRI2_994_VF = 85.0;
    static final double CRI2_994_NF = 34.0;
    static final double CRI2_994_NL = 0.850;
    //CRI1-998
    static final double CRI1_998_AH = 0.050;
    static final double CRI1_998_OL = 0.100;
    static final double CRI1_998_RAG = 0.065;
    static final double CRI1_998_VF = 68.0;
    static final double CRI1_998_NF = 34.0;
    static final double CRI1_998_NL = 0.200;
    //CRI1-999
    static final double CRI1_999_AH = 0.040;
    static final double CRI1_999_OL = 0.020;
    static final double CRI1_999_RAG = 0.075;
    static final double CRI1_999_VF = 67.0;
    static final double CRI1_999_NF = 34.0;
    static final double CRI1_999_NL = 0.425;
    //CRI2-926
    static final double CRI2_996_AH = 0.053;
    static final double CRI2_996_OL = 0.020;
    static final double CRI2_996_RAG = 0.055;
    static final double CRI2_996_VF = 80.0;
    static final double CRI2_996_NF = 34.0;
    static final double CRI2_996_NL = 0.200;
    //DENSO
    static final double DENSO_AH = 0.050;
    static final double DENSO_RAG = 0.050;
    static final double DENSO_VF = 67.0;
    static final double DENSO_NF = 34.0;
    //DELPHI
    static final double DELPHI_AH = 0.030;
    static final double DELPHI_RAG = 0.020;
    static final double DELPHI_VF = 70.0;
    //CATERPILLAR
    static final double CATERPILLAR_AH = 0.040;
    static final double CATERPILLAR_RAG = 0.060;
    static final double CATERPILLAR_VF = 65.0;
    static final double CATERPILLAR_NF = 33.0;
    static final double CATERPILLAR_NL = 0.300;

    public static double crin2Formula1() {
        double rs = 0;

        double h1 = Double.valueOf(DATA_MAP.get("h1"));
        double h2 = Double.valueOf(DATA_MAP.get("h2"));
        double h3 = Double.valueOf(DATA_MAP.get("h3"));

        rs = h1 - (h2 - CRIN2_NF / 35.8) - h3;

        return rs;
    }

    public static double crin2Formula2() {
        double rs = 0;

        double h4 = Double.valueOf(DATA_MAP.get("h4"));
        double h5 = Double.valueOf(DATA_MAP.get("h5"));
        double h6 = Double.valueOf(DATA_MAP.get("h6"));

        rs = h5 + h6 - h4 + CRIN2_AH;

        return rs;
    }

    public static double crin2Formula3() {
        double rs = 0;

        double h6 = Double.valueOf(DATA_MAP.get("h6"));
        double h7 = Double.valueOf(DATA_MAP.get("h7"));

        rs = h6 - h7 - CRIN2_OL;

        return rs;
    }

    public static double crin2Formula4() {
        double rs = 0;

        double h8 = Double.valueOf(DATA_MAP.get("h8"));
        double h9 = Double.valueOf(DATA_MAP.get("h9"));
        double h10 = Double.valueOf(DATA_MAP.get("h10"));

        rs = h8 - h9 - (h10 - CRIN2_VF / 43) + CRIN2_RAG + CRIN2_AH;

        return rs;
    }

    public static double crin2Formula5() {
        double rs = 0;

        double h11 = Double.valueOf(DATA_MAP.get("h11"));

        rs = h11 - CRIN2_NL;

        return rs;
    }

    public static double crin1Formula1() {
        double rs = 0;

        double h2 = Double.valueOf(DATA_MAP.get("h2"));
        double h3 = Double.valueOf(DATA_MAP.get("h3"));

        rs = h2 - h3 + CRIN1_AH;

        return rs;
    }

    public static double crin1Formula2() {
        double rs = 0;

        double h4 = Double.valueOf(DATA_MAP.get("h4"));
        double h5 = Double.valueOf(DATA_MAP.get("h5"));

        rs = h4 - h5 + CRIN1_RAG;

        return rs;
    }

    public static double crin1Formula3() {
        double rs = 0;

        double h6 = Double.valueOf(DATA_MAP.get("h6"));
        double h7 = Double.valueOf(DATA_MAP.get("h7"));
        double h8 = Double.valueOf(DATA_MAP.get("h8"));

        rs = h6 - h7 - (h8 - CRIN1_VF / 50) + CRIN1_AH;

        return rs;
    }

    public static double crin1Formula4() {
        double rs = 0;

        double h9 = Double.valueOf(DATA_MAP.get("h9"));

        rs = h9 - CRIN1_NL;

        return rs;
    }

    public static double crin1Formula5() {
        double rs = 0;

        double h1 = Double.valueOf(DATA_MAP.get("h1"));
        double h10 = Double.valueOf(DATA_MAP.get("h10"));

        rs = h1 - (h10 - CRIN1_NF / 35.9);

        return rs;
    }

    public static double crin16Formula1() {
        double rs = 0;

        double h1 = Double.valueOf(DATA_MAP.get("h1"));
        double h2 = Double.valueOf(DATA_MAP.get("h2"));
        double h3 = Double.valueOf(DATA_MAP.get("h3"));

        rs = h1 - (h2 - CRIN1_6_NF / 35.8) - h3;

        return rs;
    }

    public static double crin16Formula2() {
        double rs = 0;

        double h4 = Double.valueOf(DATA_MAP.get("h4"));
        double h5 = Double.valueOf(DATA_MAP.get("h5"));

        rs = h4 - h5 + CRIN1_6_AH;

        return rs;
    }

    public static double crin16Formula3() {
        double rs = 0;

        double h6 = Double.valueOf(DATA_MAP.get("h6"));
        double h7 = Double.valueOf(DATA_MAP.get("h7"));

        rs = h6 - h7 + CRIN1_6_RAG;

        return rs;
    }

    public static double crin16Formula4() {
        double rs = 0;

        double h8 = Double.valueOf(DATA_MAP.get("h8"));
        double h9 = Double.valueOf(DATA_MAP.get("h9"));
        double h10 = Double.valueOf(DATA_MAP.get("h10"));

        rs = h8 - h9 - (h10 - CRIN1_6_VF / 50) + CRIN1_6_AH;

        return rs;
    }

    public static double crin16Formula5() {
        double rs = 0;

        double h9 = Double.valueOf(DATA_MAP.get("h9"));

        rs = h9 - CRIN1_6_NL;

        return rs;
    }

    public static double crin2LFormula1() {
        double rs = 0;

        double h1 = Double.valueOf(DATA_MAP.get("h1"));
        double h2 = Double.valueOf(DATA_MAP.get("h2"));
        double h3 = Double.valueOf(DATA_MAP.get("h3"));

        rs = h1 - (h2 - CRIN2_L_NF / 35.8) - h3;

        return rs;
    }

    public static double crin2LFormula2() {
        double rs = 0;

        double h4 = Double.valueOf(DATA_MAP.get("h4"));
        double h5 = Double.valueOf(DATA_MAP.get("h5"));

        rs = h5 - h4 + CRIN2_L_AH;

        return rs;
    }

    public static double crin2LFormula3() {
        double rs = 0;

        double h4 = Double.valueOf(DATA_MAP.get("h4"));
        double h6 = Double.valueOf(DATA_MAP.get("h6"));

        rs = h4 - h6 - CRIN2_L_OL - CRIN2_L_AH;

        return rs;
    }

    public static double crin2LFormula4() {
        double rs = 0;

        double h7 = Double.valueOf(DATA_MAP.get("h7"));
        double h8 = Double.valueOf(DATA_MAP.get("h8"));
        double h9 = Double.valueOf(DATA_MAP.get("h9"));

        rs = h8 - h7 - (h9 - CRIN2_L_VF / 50);

        return rs;
    }

    public static double crin2LFormula5() {
        double rs = 0;

        double h10 = Double.valueOf(DATA_MAP.get("h10"));

        rs = h10 - CRIN2_L_NL;

        return rs;
    }

    public static double cri2994Formula1() {
        double rs = 0;

        double h1 = Double.valueOf(DATA_MAP.get("h1"));
        double h2 = Double.valueOf(DATA_MAP.get("h2"));
        double h3 = Double.valueOf(DATA_MAP.get("h3"));

        rs = h1 - (h2 - CRI2_994_NF / 35.8) - h3;

        return rs;
    }

    public static double cri2994Formula2() {
        double rs = 0;

        double h4 = Double.valueOf(DATA_MAP.get("h4"));
        double h5 = Double.valueOf(DATA_MAP.get("h5"));

        rs = h4 - h5 + CRI2_994_AH;

        return rs;
    }

    public static double cri2994Formula3() {
        double rs = 0;

        double h6 = Double.valueOf(DATA_MAP.get("h6"));
        double h7 = Double.valueOf(DATA_MAP.get("h7"));

        rs = h7 - h6 - CRI2_994_AH;

        return rs;
    }

    public static double cri2994Formula4() {
        double rs = 0;

        double h8 = Double.valueOf(DATA_MAP.get("h8"));
        double h9 = Double.valueOf(DATA_MAP.get("h9"));

        rs = h8 - h9 + CRI2_994_RAG;

        return rs;
    }

    public static double cri2994Formula5() {
        double rs = 0;

        double h10 = Double.valueOf(DATA_MAP.get("h10"));
        double h11 = Double.valueOf(DATA_MAP.get("h11"));
        double h12 = Double.valueOf(DATA_MAP.get("h12"));

        rs = h11 - h10 - (h12 - CRI2_994_VF / 50) + CRI2_994_AH;

        return rs;
    }

    public static double cri1998Formula1() {
        double rs = 0;

        double h2 = Double.valueOf(DATA_MAP.get("h2"));
        double h3 = Double.valueOf(DATA_MAP.get("h3"));

        rs = h2 - h3 - +CRI1_998_AH;

        return rs;
    }

    public static double cri1998Formula2() {
        double rs = 0;

        double h4 = Double.valueOf(DATA_MAP.get("h4"));
        double h5 = Double.valueOf(DATA_MAP.get("h5"));

        rs = h4 - h5 + CRI1_998_RAG;

        return rs;
    }

    public static double cri1998Formula3() {
        double rs = 0;

        double h6 = Double.valueOf(DATA_MAP.get("h6"));
        double h7 = Double.valueOf(DATA_MAP.get("h7"));
        double h8 = Double.valueOf(DATA_MAP.get("h8"));

        rs = h7 - h6 - (h8 - CRI1_998_VF / 50.3) + CRI1_998_AH;

        return rs;
    }

    public static double cri1998Formula4() {
        double rs = 0;

        double h9 = Double.valueOf(DATA_MAP.get("h9"));

        rs = h9 - CRI1_998_NL;

        return rs;
    }

    public static double cri1998Formula5() {
        double rs = 0;

        double h1 = Double.valueOf(DATA_MAP.get("h1"));
        double h10 = Double.valueOf(DATA_MAP.get("h10"));

        rs = h1 - (h10 - CRI1_998_NF / 35.9);

        return rs;
    }

    public static double cri1999Formula1() {
        double rs = 0;

        double h1 = Double.valueOf(DATA_MAP.get("h1"));
        double h2 = Double.valueOf(DATA_MAP.get("h2"));
        double h3 = Double.valueOf(DATA_MAP.get("h3"));

        rs = h1 - (h2 - CRI1_999_NF / 35.8) - h3;

        return rs;
    }

    public static double cri1999Formula2() {
        double rs = 0;

        double h4 = Double.valueOf(DATA_MAP.get("h4"));
        double h5 = Double.valueOf(DATA_MAP.get("h5"));

        rs = h4 - h5 + CRI1_999_AH;

        return rs;
    }

    public static double cri1999Formula3() {
        double rs = 0;

        double h6 = Double.valueOf(DATA_MAP.get("h6"));
        double h7 = Double.valueOf(DATA_MAP.get("h7"));

        rs = h7 - h6 - CRI1_999_AH;

        return rs;
    }

    public static double cri1999Formula4() {
        double rs = 0;

        double h8 = Double.valueOf(DATA_MAP.get("h8"));
        double h9 = Double.valueOf(DATA_MAP.get("h9"));

        rs = h8 - h9 + CRI1_999_RAG;

        return rs;
    }

    public static double cri1999Formula5() {
        double rs = 0;

        double h10 = Double.valueOf(DATA_MAP.get("h10"));
        double h11 = Double.valueOf(DATA_MAP.get("h11"));
        double h12 = Double.valueOf(DATA_MAP.get("h12"));

        rs = h11 - h10 - (h12 - CRI1_999_VF / 50) + CRI1_999_AH;

        return rs;
    }

    public static double cri1999Formula6() {
        double rs = 0;

        double h13 = Double.valueOf(DATA_MAP.get("h13"));

        rs = h13 - CRI1_999_NL;

        return rs;
    }

    public static double cri2996Formula1() {
        double rs = 0;

        double h1 = Double.valueOf(DATA_MAP.get("h1"));
        double h2 = Double.valueOf(DATA_MAP.get("h2"));
        double h3 = Double.valueOf(DATA_MAP.get("h3"));

        rs = h1 - (h2 - CRI2_996_NF / 35.8) - h3;

        return rs;
    }

    public static double cri2996Formula2() {
        double rs = 0;

        double h4 = Double.valueOf(DATA_MAP.get("h4"));
        double h5 = Double.valueOf(DATA_MAP.get("h5"));

        rs = h5 - h4 + CRI2_996_AH;

        return rs;
    }

    public static double cri2996Formula3() {
        double rs = 0;

        double h6 = Double.valueOf(DATA_MAP.get("h6"));
        double h7 = Double.valueOf(DATA_MAP.get("h7"));
        double h8 = Double.valueOf(DATA_MAP.get("h8"));

        rs = h7 - h6 - (h8 - CRI2_996_VF / 50);

        return rs;
    }

    public static double cri2996Formula4() {
        double rs = 0;

        double h9 = Double.valueOf(DATA_MAP.get("h9"));

        rs = h9 - CRI2_996_NL;

        return rs;
    }

    public static double densoFormula1() {
        double rs = 0;

        double h1 = Double.valueOf(DATA_MAP.get("h1"));
        double h2 = Double.valueOf(DATA_MAP.get("h2"));

        rs = h1 - h2 + DENSO_AH;

        return rs;
    }

    public static double densoFormula2() {
        double rs = 0;

        double h3 = Double.valueOf(DATA_MAP.get("h3"));
        double h4 = Double.valueOf(DATA_MAP.get("h4"));
        double h5 = Double.valueOf(DATA_MAP.get("h5"));

        rs = h4 + h3 - (h5 - DENSO_VF / 43) + DENSO_AH + DENSO_RAG;

        return rs;
    }

    public static double densoFormula3() {
        double rs = 0;

        double h1 = Double.valueOf(DATA_MAP.get("h1"));
        double h2 = Double.valueOf(DATA_MAP.get("h2"));
        double h3 = Double.valueOf(DATA_MAP.get("h3"));

        rs = h1 - (h2 - DENSO_NF / 35.8) - h3;

        return rs;
    }

    public static double delphiFormula1() {
        double rs = 0;

        double h1 = Double.valueOf(DATA_MAP.get("h1"));
        double h2 = Double.valueOf(DATA_MAP.get("h2"));
        double h3 = Double.valueOf(DATA_MAP.get("h3"));

        rs = h2 - (h3 - DELPHI_VF / 43) + h1;

        return rs;
    }

    public static double caterpillarFormula1() {
        double rs = 0;

        double h2 = Double.valueOf(DATA_MAP.get("h2"));

        rs = h2 + 0.100;

        return rs;
    }

    public static double caterpillarFormula2() {
        double rs = 0;

        double h3 = Double.valueOf(DATA_MAP.get("h3"));
        double h4 = Double.valueOf(DATA_MAP.get("h4"));

        rs = h3 - (h4 - CATERPILLAR_VF / 43) + 0.100;

        return rs;
    }

    public static double caterpillarFormula3() {
        double rs = 0;

        double h5 = Double.valueOf(DATA_MAP.get("h5"));
        double h6 = Double.valueOf(DATA_MAP.get("h6"));

        rs = h5 - (h6 - CATERPILLAR_NF / 35.8);

        return rs;
    }

    public static double caterpillarFormula4() {
        double rs = 0;

        double h7 = Double.valueOf(DATA_MAP.get("h7"));
        double h8 = Double.valueOf(DATA_MAP.get("h8"));

        rs = h7 - h8 + CATERPILLAR_NL;

        return rs;
    }
}