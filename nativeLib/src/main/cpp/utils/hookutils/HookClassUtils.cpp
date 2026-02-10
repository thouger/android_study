
#include "dlfcn_compat.h"
#include "HookUtils.h"
#include "libpath.h"
#include "ZhenxiLogging.h"
#include "libpath.h"

//void ClassLinker::LoadClass(Thread* self,
//                            const DexFile& dex_file,
//                            const DexFile::ClassDef& dex_class_def,
//                            Handle<mirror::Class> klass)
HOOK_DEF(void*, LoadClass, void *LinkerThis, void *self,
         const void *dex_file,
         const void *dex_class_def,
         void *klass) {
    return orig_LoadClass(LinkerThis, self, dex_file, dex_class_def, klass);
}


void into_classLinker_loadClass(char *artPth) {
    void *classLinker_loadClassSym = getSymCompat(artPth,
                                                  "_ZN3art11ClassLinker9LoadClassEPNS_6ThreadERKNS_7DexFileERKNS_3dex8ClassDefENS_6HandleINS_6mirror5ClassEEE");
    if (classLinker_loadClassSym == nullptr) {
        classLinker_loadClassSym = getSymCompat(artPth,
                                                "_ZN3art11ClassLinker9LoadClassEPNS_6ThreadERKNS_7DexFileERKNS3_8ClassDefENS_6HandleINS_6mirror5ClassEEE");
    }

    if (classLinker_loadClassSym != nullptr) {
        if (HookUtils::Hooker((void *) classLinker_loadClassSym,
                              (void *) new_LoadClass,
                              (void **) &orig_LoadClass)) {
            LOG(ERROR) << " into_classLinker_loadClass sucess";
            return;
        }
    } else {
        LOG(ERROR) << " classLinker_loadClassSym not find loadClassSym ";
    }
    LOG(ERROR) << "DobbyHook into_classLinker_loadClass fail";
}

//mirror::Class* ClassLinker::FindClass(Thread* self,
//                                      const char* descriptor,
//                                      Handle<mirror::ClassLoader> class_loader)
HOOK_DEF(void*, FindClass, void *LinkerThis, void *self, const char *descriptor,
         void *class_loader) {
    LOG(ERROR) << "  My_FindClass 被执行 " << descriptor;

    return orig_FindClass(LinkerThis, self, descriptor, class_loader);
}


void into_classlinker_findclass(char *art) {


    void *classLinker_findclass = getSymCompat(art,
                                               "_ZN3art11ClassLinker9FindClassEPNS_6ThreadEPKcNS_6HandleINS_6mirror11ClassLoaderEEE");


    if (classLinker_findclass != nullptr) {
        if (HookUtils::Hooker((void *) classLinker_findclass,
                              (void *) new_FindClass,
                              (void **) &orig_FindClass)) {
            LOG(ERROR) << " into_classLinker_FindClass sucess";
            return;
        }
    } else {
        LOG(ERROR) << " into_classlinker_findclass 没找到函数地址";

    }
    LOG(ERROR) << "DobbyHook into_classLinker_FindClass fail";
}


//mirror::Class* ClassLinker::DefineClass(Thread* self,
//                                        const char* descriptor,
//                                        size_t hash,
//                                        Handle<mirror::ClassLoader> class_loader,
//                                        const DexFile& dex_file,
//                                        const DexFile::ClassDef& dex_class_def) {
HOOK_DEF(void*, DefineClass, void *classlinker_this,
         void *self,
         const char *descriptor,
         size_t hash,
         void *class_loader,
         const void *dex_file,
         const void *dex_class_def) {

    void *kclass = orig_DefineClass(classlinker_this, self, descriptor, hash, class_loader,
                                    dex_file,
                                    dex_class_def);
    //LOG(ERROR) << "LVmp  My_DefineClass被执行"<<dex_file.GetClassDescriptor(dex_class_def);

    return kclass;
}


void into_classlinker_DefineClass(char *art) {
    void *classLinker_DefineClass = getSymCompat(art,
                                                 "_ZN3art11ClassLinker11DefineClassEPNS_6ThreadEPKcjNS_6HandleINS_6mirror11ClassLoaderEEERKNS_7DexFileERKNS9_8ClassDefE");

    if (classLinker_DefineClass == nullptr) {
        classLinker_DefineClass = getSymCompat(art,
                                               "_ZN3art11ClassLinker11DefineClassEPNS_6ThreadEPKcjNS_6HandleINS_6mirror11ClassLoaderEEERKNS_7DexFileERKNS_3dex8ClassDefE");
    }
    if (classLinker_DefineClass != nullptr) {
        if (HookUtils::Hooker((void *) classLinker_DefineClass,
                              (void *) new_DefineClass,
                              (void **) &orig_DefineClass)) {
            LOG(ERROR) << " into_classLinker_DefineClass sucess";
            return;
        }
    } else {
        LOG(ERROR) << " into_classlinker_DefineClass 没找到函数地址";

    }
    LOG(ERROR) << "DobbyHook into_classLinker_FindClass fail";
}


//void ClassLinker::SetupClass(const DexFile& dex_file,
//                             const DexFile::ClassDef& dex_class_def,
//                             Handle<mirror::Class> klass,
//                             mirror::ClassLoader* class_loader) {
HOOK_DEF(void*, SetupClass,
         void *classlinker_this,
         const void *dex_file,
         const void *dex_class_def,
         void *klass,
         void *class_loader) {

    return orig_SetupClass(classlinker_this, dex_file, dex_class_def, klass, class_loader);
}


void into_classlinker_SetupClass(char *art) {
    void *classLinker_SetupClass = getSymCompat(art,
                                                "_ZN3art11ClassLinker10SetupClassERKNS_7DexFileERKNS1_8ClassDefENS_6HandleINS_6mirror5ClassEEEPNS8_11ClassLoaderE");
    if (classLinker_SetupClass == nullptr) {
        classLinker_SetupClass = getSymCompat(art,
                                              "_ZN3art11ClassLinker10SetupClassERKNS_7DexFileERKNS_3dex8ClassDefENS_6HandleINS_6mirror5ClassEEENS_6ObjPtrINS9_11ClassLoaderEEE");
    }

    if (classLinker_SetupClass != nullptr) {
        if (HookUtils::Hooker((void *) classLinker_SetupClass,
                              (void *) new_SetupClass,
                              (void **) &orig_SetupClass)) {
            LOG(ERROR) << " into_classLinker_SetupClass sucess";
            return;
        }
    } else {
        LOG(ERROR) << " into_classlinker_SetupClass not find sym";
    }
    LOG(ERROR) << "DobbyHook into_classlinker_SetupClass fail";
}

void HookClassloadAll() {
    char *art = getlibcPath();
    into_classLinker_loadClass(art);
    into_classlinker_findclass(art);

//    into_classlinker_DefineClass(art);
//    into_classlinker_SetupClass(art);


}