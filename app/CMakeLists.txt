cmake_minimum_required(VERSION 3.4.1)
project(TLC)

###############################
##  IMPORTING COSA_REC LIBS  ##
###############################
set(COSA_REC_DIR ${CMAKE_SOURCE_DIR}/src/main/cpp/android)
include_directories(${COSA_REC_DIR})
set(COSA_REC_LIBS
    mysqlclient
    rtlsdr0.5
    usb1.0)
foreach(libname ${COSA_REC_LIBS})
        message(add lib ${libname})
        add_library(${libname} SHARED IMPORTED)
        set_target_properties(${libname} PROPERTIES IMPORTED_LOCATION
                              ${COSA_REC_DIR}/libs/${ANDROID_ABI}/lib${libname}.so)
endforeach()

set(CMAKE_C_FLAGS "${CMAKE_CXX_FLAGS} -mfloat-abi=softfp -mfpu=neon -g -O0")

#ADD LIB COSA_REC THAT WILL BE MODIFIED BY US
add_library(cosa_rec SHARED src/main/cpp/cosa_rec/cosa_rec.c)


add_library( # Sets the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/native-lib.cpp )

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

target_link_libraries(cosa_rec ${COSA_REC_LIBS} ${log-lib})

target_link_libraries( # Specifies the target library.
                       native-lib

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )

#add_executable(TLC provaMain.c)