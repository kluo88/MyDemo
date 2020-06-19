package com.itkluo.demo.tts2;

import android.support.annotation.RawRes;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 组合音频 实体类
 * 开头 + 金额 + 单位 + 结尾
 *
 * @author luobingyong
 * @date 2020/6/18
 */
public class VoiceBuilder {
    //开头音频
    private @RawRes Integer[] start;
    //播报金额
    private String number;
    //单位
    private @RawRes Integer unit;
    //是否转成全数字,照着数字一个一个播 默认人民币 千百十
    private boolean isOriginNum;
    //结尾音频
    private @RawRes Integer[] end;

    public @RawRes Integer[] getStart() {
        return start;
    }

    public String getNumber() {
        return number;
    }

    public @RawRes Integer getUnit() {
        return unit;
    }

    public boolean isOriginNum() {
        return isOriginNum;
    }

    public @RawRes Integer[] getEnd() {
        return end;
    }

    public static class Builder {
        private @RawRes Integer[] start;
        private String number;
        private @RawRes Integer unit;
        private boolean isOriginNum;
        private @RawRes Integer[] end;

        public Builder start(@RawRes Integer... start) {
            this.start = start;
            return this;
        }

        public Builder number(String number) {
            this.number = getNumber(filterEmoji(number, " "));
            return this;
        }

        public Builder unit(@RawRes Integer unit) {
            this.unit = unit;
            return this;
        }

        public Builder isOriginNum(boolean isOriginNum) {
            this.isOriginNum = isOriginNum;
            return this;
        }

        public Builder end(@RawRes Integer... end) {
            this.end = end;
            return this;
        }

        public VoiceBuilder builder() {
            return new VoiceBuilder(this);
        }

        private String filterEmoji(String source, String slipStr) {
            return !TextUtils.isEmpty(source) ? source.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", slipStr) : source;
        }
    }

    public VoiceBuilder(Builder builder) {
        this.start = builder.start;
        this.number = builder.number;
        this.unit = builder.unit;
        this.isOriginNum = builder.isOriginNum;
        this.end = builder.end;
    }

    /**
     * 提取字符串中的 数字 带小数点 ，没有就返回""
     *
     * @param number
     * @return
     */
    public static String getNumber(String number) {
        Pattern pattern = Pattern.compile("(\\d+\\.\\d+)");
        Matcher m = pattern.matcher(number);
        if (m.find()) {
            number = m.group(1) == null ? "" : m.group(1);
        } else {
            pattern = Pattern.compile("(\\d+)");
            m = pattern.matcher(number);
            if (m.find()) {
                number = m.group(1) == null ? "" : m.group(1);
            } else {
                number = "";
            }
        }

        return number;
    }

}
