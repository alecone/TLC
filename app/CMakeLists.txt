cmake_minimum_required(VERSION 3.4.1)
project(TLC)

add_library( # Sets the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/native-lib.cpp )

add_library(kiss_fft
            SHARED
            src/main/cpp/kiss_fft.c)

add_library(provaMain
            SHARED
            src/main/cpp/provaMain.c)


include_directories(src/main/include/)


find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )


target_link_libraries( # Specifies the target library.
                       native-lib

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )

target_link_libraries( # Specifies the target library.
                        kiss_fft

                       # Links the target library to the log library

                       # included in the NDK.
                       ${log-lib} )

target_link_libraries( # Specifies the target library.
                        provaMain

                       # Links the target library to the log library

                       # included in the NDK.
                       ${log-lib} )

#project (TLC)
#add_executable(TLC provaMain.c)