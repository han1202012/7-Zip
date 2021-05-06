
# 指定 CMake 最低版本
cmake_minimum_required(VERSION 3.4.1)

# 设置函数库编译
add_library( # 参数 1 : 设置生成的动态库名称
        native-lib

        # 参数 2 : 设置生成的函数库类型 : ① 静态库 STATIC ② 动态库 SHARED
        SHARED

        # 参数 3 : 配置要编译的源文件
        native-lib.cpp)


# 使用下面的方式引入动态库会出现于 Android.mk 配置一样的问题 , 6.0 以上的 Android 系统在运行时出现找不到路径的问题

# 引入动态库
#add_library(add SHARED IMPORTED)
# 设置函数库的导入路径
#set_target_properties(add PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/../jniLibs/armeabi-v7a/libadd.so)


# 打印日志信息
#       ${ANDROID_ABI} 的作用是获取当前的 CPU 指令集架构
#           当本次编译 armeabi-v7a CPU 架构时 , ${ANDROID_ABI} 值为 armeabi-v7a
#           当本次编译 x86 CPU 架构时 , ${ANDROID_ABI} 值为 x86
message("CMAKE_SOURCE_DIR : ${CMAKE_SOURCE_DIR}, ANDROID_ABI : ${ANDROID_ABI}")


# 到预设的目录查找 log 库 , 将找到的路径赋值给 log-lib
#       这个路径是 NDK 的 ndk-bundle\platforms\android-29\arch-arm\usr\lib\liblog.so
#       不同的 Android 版本号 和 CPU 架构 需要到对应的目录中查找 , 此处是 29 版本 32 位 ARM 架构的日志库
#
# 可以不配置 :
#       可以不进行该配置, 直接在后面的 target_link_libraries 中链接 log 也不会出错
find_library(
        log-lib

        log)

# 打印日志库位置
message(${log-lib})


# 设置变量
# CMAKE_CXX_FLAGS 表示会将 C++ 的参数传给编译器
# CMAKE_C_FLAGS 表示会将 C 参数传给编译器

# 参数设置 : 传递 CMAKE_CXX_FLAGS C+= 参数给编译器时 , 在 该参数后面指定库的路径
#   CMAKE_SOURCE_DIR 指的是当前的文件地址
#   -L 参数指定动态库的查找路径
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}")

# 设置头文件查找路径
include_directories(
        lib7zr/CPP/7zip/Archive
        lib7zr/CPP/7zip/Archive/7z
        lib7zr/CPP/7zip/Archive/BZip2
        lib7zr/CPP/7zip/Archive/Common
        lib7zr/CPP/7zip/Archive/GZip
        lib7zr/CPP/7zip/Archive/Cab
        lib7zr/CPP/7zip/Archive/Lzma
        lib7zr/CPP/7zip/Archive/Tar
        lib7zr/CPP/7zip/Archive/Zip
        lib7zr/CPP/7zip/Archive/Split
        lib7zr/CPP/7zip/Archive/Z
        lib7zr/CPP/7zip/Compress
        lib7zr/CPP/7zip/Crypto
        lib7zr/CPP/7zip/UI/Console
        lib7zr/CPP/7zip/UI/Common
        lib7zr/CPP/Windows
        lib7zr/CPP/Common
        lib7zr/CPP/7zip/Common
        lib7zr/C
        lib7zr/CPP/myWindows
        lib7zr/CPP
        lib7zr/CPP/include_windows)


# 链接函数库
#       参数 1 : 本构建脚本要生成的动态库目标
#       参数 2 ~ ... : 后面是之前预编译的动态库或静态库 , 或引入的动态库
target_link_libraries(
        native-lib

        # 表示 编译 native-lib 模块, 要链接 lib7zr.so 模块
        7zr

        ${log-lib})
