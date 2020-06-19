package com.itkluo.demo.tts;

import com.itkluo.demo.R;

/**
 * 音频的ID
 */
public interface AudioCode {
    int audio_key_0 = R.raw.mp3_key_code_0;
    int audio_key_1 = R.raw.mp3_key_code_1;
    int audio_key_2 = R.raw.mp3_key_code_2;
    int audio_key_3 = R.raw.mp3_key_code_3;
    int audio_key_4 = R.raw.mp3_key_code_4;
    int audio_key_5 = R.raw.mp3_key_code_5;
    int audio_key_6 = R.raw.mp3_key_code_6;
    int audio_key_7 = R.raw.mp3_key_code_7;
    int audio_key_8 = R.raw.mp3_key_code_8;
    int audio_key_9 = R.raw.mp3_key_code_9;
    int audio_key_dot = R.raw.mp3_key_code_dot; //点
    int audio_key_jia = R.raw.mp3_key_code_jia; //加
    int audio_key_jian = R.raw.mp3_key_code_jian; //减
    int audio_key_cheng = R.raw.mp3_key_code_cheng; //乘
    int audio_key_chu = R.raw.mp3_key_code_chu; //除
    int audio_key_equal = R.raw.mp3_key_code_equal; //等于
    int audio_key_esc = R.raw.mp3_key_code_esc; //返回
    int audio_key_back = R.raw.mp3_key_code_back; //退格
    int audio_key_pay = R.raw.mp3_key_code_pay; //收款
    int audio_key_setting = R.raw.mp3_key_code_setting; //设置
    int audio_key_payment = R.raw.mp3_key_code_payment;//支付

    int audio_unit_shi = R.raw.mp3_unit_shi;//十
    int audio_unit_bai = R.raw.mp3_unit_bai;//百
    int audio_unit_qian = R.raw.mp3_unit_qian;//千
    int audio_unit_wan = R.raw.mp3_unit_wan;//万
    int audio_unit_yuan = R.raw.mp3_unit_yuan;//元
    int audio_unit_jiao = R.raw.mp3_unit_jiao;//角
    int audio_unit_fen = R.raw.mp3_unit_fen;//分
    int audio_unit_du = R.raw.mp3_unit_du;//度

    int audio_pay_facepay = R.raw.mp3_pay_facepay;//请刷脸或出示付款码
    int audio_pay_jh_qrpay = R.raw.mp3_pay_jh_qrpay;//请出示付款码或点击扫码支付
    int audio_pay_wx = R.raw.mp3_pay_wx;//微信支付收款
    int audio_pay_zfb = R.raw.mp3_pay_zfb;//支付宝收款
    int audio_pay_result_succeed = R.raw.mp3_pay_result_succeed;//支付成功
    int audio_pay_result_error = R.raw.mp3_pay_result_error;//支付失败
    int audio_pay_enter_password = R.raw.mp3_pay_enter_password;//请输入支付密码
    int audio_pay_refund_succeed = R.raw.mp3_pay_refund_succeed;//退款申请成功

    int audio_idcheck_main = R.raw.mp3_idcheck_main;//人证核验
    int audio_idcheck_have_papers = R.raw.mp3_idcheck_have_papers;//有证核验
    int audio_idcheck_have_papers_hint = R.raw.mp3_idcheck_have_papers_hint;//请在读卡区放入身份证
    int audio_idcheck_not_papers = R.raw.mp3_idcheck_not_papers;//无证核验
    int audio_idcheck_not_papers_hint = R.raw.mp3_idcheck_not_papers_hint;//请输入姓名和身份证号码
    int audio_idcheck_temp_hint = R.raw.mp3_idcheck_temp_hint;//当前体温为
    int audio_idcheck_temp_overtop = R.raw.mp3_idcheck_temp_overtop;//体温过高
    int audio_idcheck_temp_low = R.raw.mp3_idcheck_temp_low;//体温过低，需重新测温
    int audio_idcheck_temp_top_exception = R.raw.mp3_idcheck_temp_top_exception;//异常高温
    int audio_idcheck_pass = R.raw.mp3_idcheck_pass;//人证核验通过
    int audio_idcheck_not_pass = R.raw.mp3_idcheck_not_pass;//人证核验不通过
    int audio_idcheck_cloud_pass = R.raw.mp3_idcheck_cloud_pass;//人证云核验通过
    int audio_idcheck_cloud_not_pass = R.raw.mp3_idcheck_cloud_not_pass;//人证云核验不通过
    int audio_idcheck_timeout = R.raw.mp3_idcheck_imeout;//人脸识别超时
    int audio_idcheck_scan_face_failure = R.raw.mp3_idcheck_scan_face_failure;//人脸采集失败，请重试

    int audio_setting_enter_password = R.raw.mp3_setting_enter_password;//请输入密码
    int audio_setting_wifi_connect = R.raw.mp3_setting_wifi_connect;//无线网络已连接
    int audio_setting_ethernet_connect = R.raw.mp3_setting_ethernet_connect;//有线网络已连接
    int audio_setting_network_disconnect = R.raw.mp3_setting_network_disconnect;//网络已断开

    int audio_signin_network_error = R.raw.mp3_signin_network_error; //网络错误，请重试
    int audio_signin_succeed = R.raw.mp3_signin_succeed;//签到成功，欢迎光临
    int audio_signin_unsuccessful = R.raw.mp3_signin_unsuccessful;//签到失败，请联系管理员
}
