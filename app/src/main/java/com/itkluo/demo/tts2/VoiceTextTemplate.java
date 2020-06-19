package com.itkluo.demo.tts2;

import android.text.TextUtils;

import com.itkluo.demo.tts.AudioCode;
import com.itkluo.demo.tts2.util.MoneyUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 音频组合
 *
 * @author luobingyong
 * @date 2020/6/18
 */
public class VoiceTextTemplate {
    /**
     * 音频组合
     *
     * @param voiceBean
     * @return
     */
    public static List<Integer> genVoiceList(VoiceBuilder voiceBean) {
        List<Integer> result = new ArrayList<>();
        Integer[] start = voiceBean.getStart();
        String number = voiceBean.getNumber();
        Integer unit = voiceBean.getUnit();
        boolean isOriginNum = voiceBean.isOriginNum();
        Integer[] end = voiceBean.getEnd();

        if (start != null && start.length > 0) {
            List<Integer> resultList = new ArrayList<Integer>(start.length);
            Collections.addAll(resultList, start);
            result.addAll(resultList);
        }

        if (!TextUtils.isEmpty(number)) {
            if (isOriginNum) {
                result.addAll(createReadableNumList(number));
            } else {
                result.addAll(genReadableMoney(number));
            }
        }

        if (unit != null && unit > 0) {
            result.add(unit);
        }

        if (end != null && end.length > 0) {
            List<Integer> resultList = new ArrayList<Integer>(end.length);
            Collections.addAll(resultList, end);
            result.addAll(resultList);
        }

        return result;
    }


    /**
     * 全转成 中文 RMB
     *
     * @param numString
     * @return
     */
    private static List<Integer> genReadableMoney(String numString) {
        List<Integer> result = new ArrayList<>();
        if (!TextUtils.isEmpty(numString)) {
            if (numString.contains(".")) {
                String integerPart = numString.split("\\.")[0];
                String decimalPart = numString.split("\\.")[1];
                List<Integer> intList = readIntPart(integerPart);
                List<Integer> decimalList = readDecimalPart(decimalPart);
                result.addAll(intList);
                if (!decimalList.isEmpty()) {
                    result.add(AudioCode.audio_key_dot);
                    result.addAll(decimalList);
                }
            } else {
                result.addAll(readIntPart(numString));
            }
        }
        return result;
    }

    private static List<Integer> readDecimalPart(String decimalPart) {
        List<Integer> result = new ArrayList<>();
        if (!"00".equals(decimalPart)) {
            char[] chars = decimalPart.toCharArray();
            for (char ch : chars) {
                String numStr = String.valueOf(ch);
                int rawId = getNum2RawId(Integer.parseInt(numStr));
                if (rawId > 0) {
                    result.add(rawId);
                }
            }
        }
        return result;
    }


    /**
     * 全转成数字
     *
     * @param numString
     * @return
     */
    private static List<Integer> createReadableNumList(String numString) {
        List<Integer> result = new ArrayList<>();
        if (!TextUtils.isEmpty(numString)) {
            int len = numString.length();
            for (int i = 0; i < len; i++) {
                if ('.' == numString.charAt(i)) {
                    result.add(AudioCode.audio_key_dot);
                } else {
                    String numStr = String.valueOf(numString.charAt(i));
                    int rawId = getNum2RawId(Integer.parseInt(numStr));
                    if (rawId > 0) {
                        result.add(rawId);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 返回数字对应的音频
     *
     * @param integerPart
     * @return
     */
    private static List<Integer> readIntPart(String integerPart) {
        List<Integer> result = new ArrayList<>();
        String intString = MoneyUtils.readInt(Integer.parseInt(integerPart));
        int len = intString.length();
        for (int i = 0; i < len; i++) {
            char current = intString.charAt(i);
            if (current == '拾') {
                result.add(AudioCode.audio_unit_shi);
            } else if (current == '佰') {
                result.add(AudioCode.audio_unit_bai);
            } else if (current == '仟') {
                result.add(AudioCode.audio_unit_qian);
            } else if (current == '万') {
//                result.add(VoiceConstants.TEN_THOUSAND);
                result.add(AudioCode.audio_unit_wan);
            } else if (current == '亿') {
//                result.add(VoiceConstants.TEN_MILLION);
            } else {
                String numStr = String.valueOf(current);
                int rawId = getNum2RawId(Integer.parseInt(numStr));
                if (rawId > 0) {
                    result.add(rawId);
                }

            }
        }
        return result;
    }

    private static int getNum2RawId(int num) {
        int rawId = -1;
        switch (num) {
            case 0:
                rawId = AudioCode.audio_key_0;
                break;
            case 1:
                rawId = AudioCode.audio_key_1;
                break;
            case 2:
                rawId = AudioCode.audio_key_2;
                break;
            case 3:
                rawId = AudioCode.audio_key_3;
                break;
            case 4:
                rawId = AudioCode.audio_key_4;
                break;
            case 5:
                rawId = AudioCode.audio_key_5;
                break;
            case 6:
                rawId = AudioCode.audio_key_6;
                break;
            case 7:
                rawId = AudioCode.audio_key_7;
                break;
            case 8:
                rawId = AudioCode.audio_key_8;
                break;
            case 9:
                rawId = AudioCode.audio_key_9;
                break;
            default:
                break;
        }
        return rawId;
    }


}
