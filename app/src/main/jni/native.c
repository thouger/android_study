#include <stdio.h>
#include <stdlib.h>

void helloWorld() {
    printf("Hello, World!\n");
    system("setprop aaa bbb");
}
// 定义构造函数
__attribute__((constructor))
void myLibraryInit() {
    // 在这里执行初始化操作
    helloWorld();
}