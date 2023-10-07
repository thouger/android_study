//
// Created by zhenxi on 2021/12/12.
//

//#include <VAJni.h>

#include <utility>

#include "includes/appUtils.h"
#include "includes/HookUtils.h"
#include "includes/libpath.h"

using namespace StringUtils;


void inline findSymBySo(size_t address, mapAddresInfo* info) {
//    SandHook::ElfImg elfImg(info->fname);
//    elfImg.getAddressSym(address, info);
}


/**
 * 根据一个地址获取符号信息
 *
 * **需要free**
 */
ZHENXI_RUNTIME_MAPINFO::mapAddresInfo* getSymbolforAddress(size_t address,pid_t pid) {

    if (address == 0) {
        _LOGE("getSymbolforAddress  address == null ")
        return nullptr;
    }
    auto* info = (ZHENXI_RUNTIME_MAPINFO::mapAddresInfo*)alloca(sizeof (ZHENXI_RUNTIME_MAPINFO::mapAddresInfo));

    FILE *fp = fopen(string("/proc/").append(
            to_string(pid)).append("/maps").c_str(),"r");

    char *line = nullptr;
    char perm[12] = {'\0'}, dev[12] = {'\0'},mapname[PATH_MAX] = {'\0'};
    size_t begin, end, inode, foo;
    size_t len = 0;
    while (getline(&line, &len, fp) != -1) {
        sscanf(line, "%lx-%lx %s %lx %s %ld %s", &begin, &end, perm, &foo, dev, &inode, mapname);
        if (address >= begin && address <= end) {
            info->fname = mapname;
            info->fbase = begin;
            _LOGI("getSymbolforAddress  find so path %s ,start -> %zu end-> %zu  need -> %zu",info->fname,begin,end,address)
            //获取函数的相对偏移
            info->offset = address-begin;
            //findSymBySo(address,info);
            syscall(__NR_close, fp);
            return info;
        }
    }

    _LOGI("getSymbolforAddress not found address in maps   0x%zx ", address)
    //找不到也需要关闭
    syscall(__NR_close, fp);
    return info;
}



//将内存指定地址设置可读,可执行,
//但是不可写
bool MPROTECT(size_t addr,size_t size,int __prot){
    //计算扇叶
    auto alignment = (unsigned) ((unsigned long long) addr % sysconf(_SC_PAGESIZE));
    //保证开始地址 减去扇叶大小
    int i = mprotect((void *) (addr - alignment), (size_t) (alignment + size),__prot);
    if (i == 0) {
        return true;
    }
    _LOGE("Helper mprotect error   %s ", strerror(errno))
    return false;
}
#define BUF_SIZE 1024

void  getNameByPid(pid_t pid, char *task_name) {
    char proc_pid_path[BUF_SIZE];
    char buf[BUF_SIZE];

    sprintf(proc_pid_path, "/proc/%d/status", pid);
    FILE *fp = fopen(proc_pid_path, "r");
    if (nullptr != fp) {
        if (fgets(buf, BUF_SIZE - 1, fp) == nullptr) {
            fclose(fp);
        }
        fclose(fp);
        sscanf(buf, "%*s %s", task_name);
    }
}

ZHENXI_RUNTIME_MAPINFO::mapItemInfo getSoBaseAddress(const char *name) {
    ZHENXI_RUNTIME_MAPINFO::mapItemInfo info{0};
    if(name== nullptr){
        return info;
    }
    size_t start = 0 ;
    size_t end = 0 ;
    bool isFirst = true;
    size_t len = 0;
    char buffer[PATH_MAX];
    memset(buffer, 0, PATH_MAX);
    //读取的Maps不进行IO重定向
    FILE *fp =fopen("/proc/self/abcd", "r");
    if(fp == nullptr){
        //找不到用原始文件
        fp = fopen("/proc/self/maps", "r");
        if(fp== nullptr){
            return info;
        }
    }
    char *line = nullptr;
    while (getline(&line, &len, fp) != -1) {
        if (line!= nullptr&&strstr(line, name)) {
            sscanf(line, "%lx-%lx",&start, &end);
            //start 只有第一次赋值
            if(isFirst){
                info.start = start;
                isFirst= false;
            }
        }
    }
    info.end = end;
    syscall(__NR_close, fp);
    //_LOGE("get maps info start -> 0x%zx  end -> 0x%zx ",info.start,info.end);
    return info;
}


void *(*org_RegisterNative)(void *thiz, void *native_method) = nullptr;

std::string (*org_PrettyMethodSym)(void *thiz, bool b) = nullptr;

void *My_RegisterNative(void *thiz, void *native_method) {
    const string &basicString = org_PrettyMethodSym(thiz, true);
    LOG(ERROR) << ">>>>>>>>>>>>>> native register " << basicString.c_str() << "  " << native_method;
//    if (strstr(basicString.c_str(), "getHWProperty")) {
//        _LOGE("注册函数打印 %s %p", basicString.c_str(), native_method);
//    }
    return org_RegisterNative(thiz, native_method);
}

void HookRegiest() {
    void *art = fake_dlopen(getlibArtPath(), RTLD_NOW);
    void *regiestSym = fake_dlsym(art, "_ZN3art9ArtMethod14RegisterNativeEPKv");
    if(org_PrettyMethodSym== nullptr) {
        void *PrettyMethodSym = fake_dlsym(fake_dlopen(getlibArtPath(), RTLD_NOW),
                                           "_ZN3art9ArtMethod12PrettyMethodEb");
        org_PrettyMethodSym = reinterpret_cast<std::string(*)(void *, bool)>(PrettyMethodSym);
    }
    if (HookUtils::Hooker(regiestSym,
                          (void *) My_RegisterNative,
                          (void **) &org_RegisterNative)) {
        LOG(ERROR) << ">>>>>>>> hook  RegisterNative sucess ! ";
    } else {
        LOG(ERROR) << ">>>>>>>> hook  RegisterNative fail ! ";
    }

    fake_dlclose(art);
}


void *(*org_append)(const char *__str) = nullptr;
void *new_append(const char *_str) {
    LOG(INFO) << ">>>>>>>>>  hook string append :  " << _str;
    return org_append(_str);
}

void hookStringAppend() {
    //todo 实现这个函数
}

bool getpData(char temp[],const void *p, size_t size) {
    memset(temp,0,strlen(temp));
    int i;
    int len = 0;
    for (i = 0; i < size; i++) {
        len += sprintf(temp + len, "%02X", ((char *) p)[i]);
    }
    return true;
}

bool mProtect(size_t addr, size_t len) {
    auto alignment = (unsigned) ((unsigned long long) addr % sysconf(_SC_PAGESIZE));
    int i = mprotect((void *) (addr - alignment), (size_t) (alignment + len),
                     PROT_READ | PROT_WRITE | PROT_EXEC);
    if (i == -1) {
        return false;
    }
    return true;
}