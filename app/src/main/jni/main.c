#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <fcntl.h>

#define PORT 11111
#define SERVER_IP "127.0.0.1"

#define COMMAND_MAX_LENGTH 1024

void setSocketNonBlocking(int sockfd) {
    fcntl(sockfd, F_SETFL, O_NONBLOCK);
}

char* magiskSu(const char* cmd) {

    char* retString = NULL;
    int mSocket = 0;

    // 创建socket
    mSocket = socket(AF_INET, SOCK_STREAM, 0);
    if (mSocket == -1) {
        perror("Socket creation failed");
        exit(EXIT_FAILURE);
    }

    struct sockaddr_in serverAddress;
    serverAddress.sin_family = AF_INET;
    serverAddress.sin_port = htons(PORT);
    inet_pton(AF_INET, SERVER_IP, &serverAddress.sin_addr);

    // 连接到服务器
    printf("Connecting to socket server on port %d\n", PORT);
    if (connect(mSocket, (struct sockaddr*)&serverAddress, sizeof(serverAddress)) == -1) {
        perror("Connection to server failed");
        exit(EXIT_FAILURE);
    }

    setSocketNonBlocking(mSocket);

    fd_set readSet;
    FD_ZERO(&readSet);
    FD_SET(mSocket, &readSet);

    struct timeval timeout;
    timeout.tv_sec = 3;  // 设置为等待10秒
    timeout.tv_usec = 0;

    char myCmd[256];
    snprintf(myCmd, sizeof(myCmd), "do_cmd|%s", cmd);

    // 发送命令到服务器
    printf("Command to server: %s\n", myCmd);
    send(mSocket, myCmd, strlen(myCmd), 0);

    // 接收服务器响应
    char buffer[1024] = {0};
    printf("Waiting for server response...\n");

    // 然后在读取数据前可以使用如下方式判断是否有数据可读
    if (select(mSocket + 1, &readSet, NULL, NULL, &timeout) > 0) {
        char buffer[1024] = {0};
        ssize_t bytesRead = read(mSocket, buffer, sizeof(buffer));
        if (bytesRead == 0) {
            printf("Server closed the connection.\n");
        } else if (bytesRead > 0) {
            printf("Server response: %s\n", buffer);
            // 处理读取的数据
        } else {
            perror("Error reading from server");
        }
    } else {
        printf("Timeout or error in select.\n");
    }

    // 关闭socket连接
    close(mSocket);
    return retString;
}

int main() {
    char buffer[1024];
    // 从标准输入流中读取参数
    if (fgets(buffer, sizeof(buffer), stdin) != NULL) {
        // 在这里处理读取到的参数
        printf("Received argument: %s", buffer);
    }
    magiskSu(buffer);
    return 0;
}