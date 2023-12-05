/**
 * @author Mrack
 * @date 2022/2/15
 */

use jni::sys::{jint,jobjectArray, jstring, JavaVM, JNI_VERSION_1_6};
use jni::objects::{JClass,JObject,JValue};
use jni::{JNIEnv};
use libc::c_void;

//log
#[macro_use]
extern crate log;
extern crate android_logger;

use log::Level;
use android_logger::Config;

#[no_mangle]
#[allow(non_snake_case)]
unsafe fn JNI_OnLoad(jvm: JavaVM, _reserved: *mut c_void) -> jint {
    android_logger::init_once(
        Config::default()
            .with_min_level(Level::Info)
            .with_tag("ALOG"),
    );
    return JNI_VERSION_1_6;
}


#[no_mangle]
pub extern "C" fn Java_thouger_study_useRust_getAppSignature(
    env: JNIEnv,
    _: JClass,
    context: JObject
) -> jint {
    info!("Java_thouger_study_useRust_getAppSignature");

    let package_manager = env.call_method(*context, "getPackageManager", "()Landroid/content/pm/PackageManager;", &[]).unwrap().l().unwrap();
    
    let package_name = env.call_method(*context, "getPackageName", "()Ljava/lang/String;", &[]).unwrap();

    let args = [package_name, JValue::Int(64)];

    let result = env.call_method(package_manager,
         "getPackageInfo", "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;", &args).unwrap().l();

    let signatures = env.get_field(result.unwrap(), "signatures",  "[Landroid/content/pm/Signature;").unwrap().l().unwrap();

    let array = jobjectArray::from(*signatures);

    if env.get_array_length(array).unwrap() == 0 { return -1; }

    let signature = env.get_object_array_element(array, 0).unwrap();

    if signature.is_null() { return -1; }

    let hash = env.call_method(signature, "hashCode", "()I", &[]).unwrap().i().unwrap();

    return hash;
}
