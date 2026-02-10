#include <stdio.h>  // fopen(),fclose(), fprintf(), printf()
#include <errno.h> // errno
#include <string.h> // strerror()

int main()
{
    FILE *log_fp = NULL;
    // 在on sys.boot_completed=1 时操作sdcard失败，建议操作/data/local/tmp/
    log_fp = fopen("/data/local/tmp/ademo_log", "w+");
    if (log_fp != NULL)
    {
        fprintf(log_fp, "open /data/local/tmp/ademo_log success\n");
        fprintf(log_fp, "----------------log by ademo\n");
        fclose(log_fp);
    }
    else
    {
        int errNum = errno; // 防止 printf 异常
        printf("open /data/local/tmp/ademo_log failed, errno:%d, error:%s\n", errNum, strerror(errNum));
    }
    return 0;
}
