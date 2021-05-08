#include <jni.h>
#include <string>
#include <7zTypes.h>
#include "logging_macros.h"

// 表示该函数在其它代码中实现
// 这是 CPP\7zip\UI\Console\MainAr.cpp 中的 main 函数
extern int MY_CDECL main
        (
#ifndef _WIN32
        int numArgs, char *args[]
#endif
);

extern "C"
JNIEXPORT void JNICALL
Java_kim_hsl_a7_1zip_MainActivity_executeCmd(JNIEnv* env, jobject thiz, jstring cmd) {
    // 将 Java 字符串转为 C 字符串
    const char *cmd_java = env->GetStringUTFChars(cmd, 0);

    LOGI("jni 中处理压缩文件命令 : %s", cmd_java);

    // 命令示例 : 7zr a files.7z files -mx=9 -t7z
    // 参数个数
    int argCount = 0;
    // 存放多个字符串, 最多 20 个字符串 , 每个最多 1024 个字符
    char argArray[20][1024] = {0};

    //分割字符串 将值填入变量

    // 获取  cmd_java 字符串长度
    int cmd_size = strlen(cmd_java);
    // 二维数组 循环控制变量, 第一个是字符串数组 , 第二个是字符串中的字符数组
    int str_index = 0, char_index = 0;

    // 标记字符是否是非空字符, tab, 如果是则设置 1 , 如果不是设置 0
    // 开始时默认 0, 不是空格
    int isChar = 0;

    // 逐个字节遍历 字符
    for(int i = 0; i < cmd_size; i ++){
        // 获取一个字符
        char c = cmd_java[i];

        //LOGI("遍历 %d . %c , cmd_size = %d", i, c, cmd_size);

        switch (c) {
            case ' ': // 判断是否是空格
            case '\t': // 判断是否是 TAB 空格
                if(isChar){
                    // 如果上一个字符不是空格 , 则需要结束当前的字符串
                    argArray[str_index][char_index++] = '\0';
                    // 字符串索引自增 1
                    str_index ++;
                    // 字符串内字符索引归零
                    char_index = 0;
                    // 设置当前的字符为空格标志位 1
                    isChar = 0;
                }else{
                    // 如果之前是空格, 那么现在也是空格 ,
                    // 说明命令中有多个空格 , 此处不做任何处理
                }
                break;
            default:
                isChar = 1;
                // 将当前字符放入数组中
                argArray[str_index][char_index++] = c;
                break;
        }
    }

    // 如果最后一位不是空格 , 则需要手动将最后一个字符串写入到数组中
    if (cmd_java[cmd_size - 1] != ' ' && cmd_java[cmd_size - 1] != '\t') {
        // 如果上一个字符不是空格 , 则需要结束当前的字符串
        argArray[str_index][char_index++] = '\0';
        // 字符串索引自增 1
        str_index ++;
    }

    // 统计字符串个数
    argCount = str_index;

    // 拼装字符串数组
    char *args[] = {0};
    for (int i = 0; i < argCount; ++i) {
        args[i] = argArray[i];
        // 打印字符串数组
        LOGI("%d . %s", i, args[i]);
    }

    // 量参数传入 main 函数
    int result = main(argCount, args);

    // 释放 Java 字符串以及 C 字符串
    env->ReleaseStringUTFChars(cmd, cmd_java);

    LOGI("7zr 命令执行完毕 result = %d !", result);
}