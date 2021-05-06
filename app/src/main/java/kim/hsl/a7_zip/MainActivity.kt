package kim.hsl.a7_zip

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.io.*

class MainActivity : AppCompatActivity() {
    companion object {
        val TAG = "MainActivity"

        init {
            System.loadLibrary("native-lib")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        copy7zr()

        compress7z()

        uncompress7z()

        executeCmd("7z")
    }

    /**
     * 将 7zr 文件拷贝到应用私有目录
     */
    fun copy7zr() {
        Log.i(TAG, "开始拷贝 7zr 文件")

        // /data/user/0/kim.hsl.a7_zip/files/7zr
        var exeFile = File(filesDir, "7zr")
        Log.i(TAG, "filesDir = ${filesDir.absolutePath} , exeFile = ${exeFile.absolutePath}")

        // 查看该文件是否存在, 如果存在设置该文件可执行
        // 如果不存在 , 拷贝文件
        if (exeFile.exists()) {
            exeFile.setExecutable(true)
            Log.i(TAG, "内置存储空间存在该 /data/user/0/kim.hsl.a7_zip/files/7zr 文件")
            return
        } else {
            Log.i(TAG, "内置存储空间不存在 7zr 可执行文件 , 开始拷贝文件")
        }

        // 如果不存在 , 拷贝文件
        var inputStream: InputStream = assets.open("libs/arm64-v8a/7zr")
        // /data/user/0/kim.hsl.a7_zip/files/7zr
        var fileOutputStream: FileOutputStream = FileOutputStream(exeFile)

        Log.i(TAG, "Build.CPU_ABI = ${Build.CPU_ABI}")

        // 不同 CPU 架构拷贝不同的可执行程序
        if (Build.CPU_ABI.startsWith("armeabi-v7a")) {
            inputStream = assets.open("libs/armeabi-v7a/7zr")

        } else if (Build.CPU_ABI.startsWith("arm64-v8a")) {
            inputStream = assets.open("libs/arm64-v8a/7zr")

        } else if (Build.CPU_ABI.startsWith("x86")) {
            inputStream = assets.open("libs/x86/7zr")

        } else if (Build.CPU_ABI.startsWith("x86_64")) {
            inputStream = assets.open("libs/x86_64/7zr")
        }

        // 拷贝文件
        var buffer: ByteArray = ByteArray(1024)
        var readCount = inputStream.read(buffer);
        while (readCount != -1) {
            fileOutputStream.write(buffer)
            readCount = inputStream.read(buffer);
        }
        fileOutputStream.flush()
        fileOutputStream.close()

        Log.i(TAG, "拷贝 7zr 文件结束")
    }

    /**
     * 使用 7zr 进行压缩
     */
    fun compress7z() {
        // /data/user/0/kim.hsl.a7_zip/files/7zr
        var exeFile = File(filesDir, "7zr")
        // 执行前赋予可执行权限
        exeFile.setExecutable(true)

        var file_7z = File("${filesDir.absolutePath}/files.7z")
        if(file_7z.exists()){
            file_7z.delete()
        }

        var cmd = "${exeFile.absolutePath} a ${filesDir.absolutePath}/files.7z ${filesDir.absolutePath} -mx=9 -t7z"
        Log.i(TAG, "压缩命令 : $cmd")

        var process: Process = Runtime.getRuntime().exec(cmd)

        // 读取命令执行过程数据
        var reader = BufferedReader(InputStreamReader(process.inputStream))
        while (true) {
            val line = reader.readLine()
            if (line != null) {
                Log.i(TAG, "$line")
            }else{
                break
            }
        }

        val exitValue = process.exitValue()
        Log.i(TAG, "压缩文件 , 执行完毕 , exitValue = $exitValue")
    }

    /**
     * 判定命令是否执行完毕
     * 调用 process.exitValue 方法 , 如果没有执行完毕 , 会抛异常,
     * 如果执行完毕会返回一个确定的值
     */
    fun isComplete(process: Process): Boolean {
        try {
            // 已经执行完毕
            process.exitValue()
            return true
        } catch (e: IllegalThreadStateException) {
            // 未执行完毕
            return false
        }
    }

    /**
     * 使用 7zr 进行解压缩
     */
    fun uncompress7z() {
        // /data/user/0/kim.hsl.a7_zip/files/7zr
        var exeFile = File(filesDir, "7zr")
        // 执行前赋予可执行权限
        exeFile.setExecutable(true)

        // 删除解压目录
        var unzip_file = File("${filesDir.absolutePath}/unzip_file")
        if(unzip_file.exists()){
            recursionDeleteFile(unzip_file)
        }

        var cmd = "${exeFile.absolutePath} x ${filesDir.absolutePath}/files.7z -o${filesDir.absolutePath}/unzip_file"
        Log.i(TAG, "解压缩命令 : $cmd")

        var process: Process = Runtime.getRuntime().exec(cmd)

        // 读取命令执行过程数据
        var reader = BufferedReader(InputStreamReader(process.inputStream))
        while (true) {
            val line = reader.readLine()
            if (line != null) {
                Log.i(TAG, "$line")
            }else{
                break
            }
        }

        val exitValue = process.exitValue()
        Log.i(TAG, "解压缩文件 , 执行完毕 , exitValue = $exitValue")
    }

    /**
     * 递归删除文件
     */
    fun recursionDeleteFile(file: File) {
        if (file.isDirectory) {
            // 如果是目录 , 则递归删除
            file.listFiles().forEach {
                // ForEach 循环删除目录
                recursionDeleteFile(it)
            }
        } else {
            // 如果是文件直接删除
            file.delete()
        }
    }

    external fun executeCmd(cmd: String): Unit
}