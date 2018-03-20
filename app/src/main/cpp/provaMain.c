//
// Created by alek on 10/10/2017.
//

#include <jni.h>
#include <string.h>
#include "../include/kiss_fft.h"
#include "../include/_kiss_fft_guts.h"
#include <stdbool.h>
#include <android/log.h>

int main(void){
    char output[40];
    int i = 60;
    __android_log_print(ANDROID_LOG_INFO, "Tag", "\r\n\r\n Prova di main \r\n\r\n");
    return 0;
}
/*
jint Java_unipg_tlc_MainActivity_main (
        JNIEnv * envi,
        jobject obj){
    jint i;
    for(i=0;i<10;i++)
        sprintf("\r\n Ciao numero %d !\r\n",i);
}
*/
/*
jint  Java_unipg_tlc_MainActivity_getMain(
        JNIEnv *env,
        jobject obj){
    int ret = main();
    jint jret = ret;
    return jret;
}
*/