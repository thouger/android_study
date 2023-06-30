//
// Created by zhenxi on 2022/2/6.
//

#include "invokePrintf.h"
#include "ZhenxiLogging.h"
#include "libpath.h"
#include "HookUtils.h"


static std::ofstream *invokeOs;
static bool isSave = false;

std::string (*invokePrintf_org_PrettyMethodSym)(void *thiz, bool b) = nullptr;

void *(*org_Invoke)(void *thiz, void *self, uint32_t *args, uint32_t args_size, void *result,const char *shorty) = nullptr;
void *new_Invoke(void *thiz, void *self, uint32_t *args, uint32_t args_size, void *result,
                 const char *shorty) {

    string basicString = invokePrintf_org_PrettyMethodSym(thiz, true);

    LOG(INFO) << "invoke method info -> "<<basicString;

    if(isSave){
        *invokeOs << basicString.append("\n");
    }
    return org_Invoke(thiz, self, args, args_size, result, shorty);
}




void invokePrintf::HookJNIInvoke(std::ofstream *os) {
    if(os!= nullptr){
        invokeOs = os;
        isSave = true;
    }
    if (invokePrintf_org_PrettyMethodSym == nullptr) {
        void *PrettyMethodSym = fake_dlsym(
                fake_dlopen(getlibArtPath(), RTLD_NOW),"_ZN3art9ArtMethod12PrettyMethodEb");
        invokePrintf_org_PrettyMethodSym = reinterpret_cast<std::string(*)(void *, bool)>(PrettyMethodSym);
    }
    void *symInvoke = getSymCompat(getlibArtPath(),"_ZN3art9ArtMethod6InvokeEPNS_6ThreadEPjjPNS_6JValueEPKc");
    HookUtils::Hooker(symInvoke,(void *) new_Invoke,(void **) &org_Invoke);
}