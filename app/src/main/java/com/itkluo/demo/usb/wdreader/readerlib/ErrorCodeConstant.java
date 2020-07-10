package com.itkluo.demo.usb.wdreader.readerlib;

public class ErrorCodeConstant {

    public static final int RSP_OK = 0;  //正确执行
    public static final int ERR_WRONG_TYPE = -1;  //卡类型不对
    public static final int ERR_NO_CARD = -2;  //无卡
    public static final int ERR_NO_POWER = -3;  //有卡未上电
    public static final int ERR_NO_RESPONSE = -4;  //卡无应答
    public static final int ERR_LOAD_LIB = -5;  //加载动态库错
    public static final int ERR_NO_READER = -11;  //读卡器连接错
    public static final int ERR_DISCONNECTION = -12;  //未建立连接
    public static final int ERR_UNSUPPORT = -13;  //（动态库）不支持该命令
    public static final int ERR_PARAMENTS = -14;  //（发给动态库的）命令参数错
    public static final int ERR_CRC = -15;  //信息校验和出错
    public static final int ERR_ID_CODE = -20;  //卡识别码格式错或规范格式版本错
    public static final int ERR_INTER_VERIFY = -21;  //内部认证失败（用户卡不合法）
    public static final int ERR_INPUT_UNMATCH = -22;  //传入数据与卡内不符
    public static final int ERR_INPUT_ILLEGAL = -23;  //传入数据不合法
    public static final int ERR_PSAM_KEY_LEVEL = -24;  //PSAM卡密钥级别不够
    public static final int ERR_NOT_FIND_DATA = -25;     //此卡文件结构中无传入的文件名和数据项
    public static final int ERR_USER_CANCLE = -31;  //用户取消密码输入
    public static final int ERR_INPUT_TIMEOUT = -32;  //密码输入操作超时
    public static final int ERR_INPUT_LENGTH = -33;  //输入密码长度错
    public static final int ERR_PWD_UNMATCH = -34;  //两次输入密码不一致
    public static final int ERR_INIT_PWD = -35;  //初始密码不能交易
    public static final int ERR_CHANGE_INIT_PWD = -36;  //不能改为初始密码
    public static final int ERR_INVALID_CHR = -41;  //运算数据含非法字符
    public static final int ERR_INVALID_LEN = -42;  //运算数据长度错
    public static final int ERR_PIN_VERIFY = -51;  //PIN校验失败，剩余次数N次（根据卡返回信息）
    public static final int ERR_PIN_LOCK = -52;  //PIN锁定
    public static final int ERR_NO_PSAM = -2201;  //无PSAM卡
    public static final int ERR_PSAM_ALGO = -2202;  //PSAM卡算法不支持（即PSAM卡内没有SSF33算法或SM4算法）
    public static final int ERR_PSAM_NO_KEY = -2203; //PSAM卡内没有RKSSSE密钥（3.0卡读个人基本信息需要RKSSSE密钥外部认证）
    public static final int ERR_ENCRYPT_VERIFY = -2204;  //不需要加密机认证
    public static final int ERR_EXT_VERIFY_2 = -25536;  //外部认证失败，剩余可尝试次数2次
    public static final int ERR_EXT_VERIFY_1 = -25537;  //外部认证失败，剩余可尝试次数1次
    public static final int ERR_EXT_VERIFY_0 = -25538;  //外部认证失败，剩余可尝试次数0次
    public static final int ERR_LC_LE = -26368;  //Lc/Le不正确
    public static final int ERR_INVALID_STATUS = -26881;  //命令不接受（无效状态）
    public static final int ERR_FILE_WRONG = -27009;  //命令与文件结构不相符、当前文件非所需文件
    public static final int ERR_UNSAFE = -27010;  //不满足安全条件
    public static final int ERR_KEY_LOCK = -27011;  //密钥锁定（算法锁定）鉴别方法锁定
    public static final int ERR_INVALID_RND = -27012;  //引用数据无效、随机数无效
    public static final int ERR_INVALID_COND = -27013;  //不满足使用条件、应用被锁定、应用未选择、余额上溢
    public static final int ERR_WRONG_MAC = -27016;  //安全报文数据项不正确、MAC不正确
    public static final int ERR_WRONG_PARA = -27264;  //数据域参数不正确
    public static final int ERR_NO_MF = -27265;  //不支持该功能、卡中无MF、卡被锁定、应用锁定
    public static final int ERR_NO_FILE = -27266;  //未找到文件、文件标
    public static final int ERR_NO_RECORD = -27267; //未找到记录
    public static final int ERR_NO_KEY = -27272; //未找到引用数据、未找到密钥
    public static final int ERR_INVALID_MAC = -37634; //MAC无效
    public static final int ERR_CARD_LOCK = -37635; //应用已被永久锁定、卡片锁定
    public static final int ERR_PSAM_TRANS = -37891;  //PSAM卡不支持消费交易
    public static final int ERR_NO_MAC = -37894;  //所需MAC（或/和TAC）不可用
    public static final int ERR_SAOMA_TIMEOUT = -111;        //扫码超时
    public static final int ERR_NO_MAGNETIC_TRIP = -112;  //不支持设置磁道信息
}
