//
// Created by alek on 07/10/2017.
//

/*
Copyright (c) 2003-2010, Mark Borgerding

All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    * Neither the author nor the names of any contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
#include <jni.h>
#include <string.h>
#include "../include/kiss_fft.h"
#include "../include/_kiss_fft_guts.h"
#include <stdbool.h>

/***********************************/
/*****  PULBLIC DECLARATIONS  *****/
/*********************************/
char * getName(ElRos_t Roso){
    return Roso.name;
}
char * getAge(ElRos_t Roso){
    char * age = (char*) Roso.age;
    return age;
}

int lengthOf(char * string){
    int len = 0;
    while(string[len] != '\0')
        len++;
    return len;
}

bool areEqual(char * str1, char * str2){
    bool ret = true;
    if(lengthOf(str1) == lengthOf(str2)){
        int i = 0;
        while((str1[i] != '\0') && ret == true){
            if(str1[i] == str2[i]){
                ret = true;
                i++;
            }
            else
                ret = false;
        }
    }
    else
        ret = false;
    return ret;
}

ElRos_t Ros = {"Alessio", 23};

/* The guts header contains all the multiplication and addition macros that are defined for
 fixed or floating point complex numbers.  It also declares the kf_ internal functions.
 */

//  FOR TESTING PURPOSE
// il nome del metodo deve essere fatto Java_(nome del packege del main in java con _ al posto di .)
// _MainActivity_nomeDelMetodo --> MainActivity perche' sara' li' ad essere richiamato il metodo
jstring Java_unipg_tlc_MainActivity_peppeName (
        JNIEnv * envi,
        jobject obj){
    jstring str = "Porco Peppe. Insert your name";
    return (*envi)->NewStringUTF(envi,str);
}

jint Java_unipg_tlc_MainActivity_dateOfBirth (
        JNIEnv * envi,
        jobject obj,
        jint age){
    jint yy = 2017;
    return (yy - age);
}

jstring Java_unipg_tlc_MainActivity_peppeAge(
        JNIEnv * envi,
        jobject obj){
    return (*envi)->NewStringUTF(envi, "Insert your age");
}

jstring Java_unipg_tlc_MainActivity_printStruct(
        JNIEnv * envi,
        jstring name, jint age){
    char * nameT = getName(Ros);
    char * ageT = getAge(Ros);
/*
 *      Some cast problem here. Couldn't find the solution yet.
    if(areEqual(nameT, (char *) name) && areEqual((char*)age, ageT))
        return (*envi)->NewStringUTF(envi, "Hey Ros, it's you!");
    else
        return (*envi)->NewStringUTF(envi, "Hey, I don't know you! I know only the Ros");
*/
    return (*envi)->NewStringUTF(envi,nameT);
}