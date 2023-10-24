#include <iostream>
#include <cstdio>
#include <cstring>
#include <cstdlib>
#include "_elf.h"
#ifndef ELFREADER_HEADER_H
#define ELFREADER_HEADER_H
using namespace std;
void readelf_h(const char* filename);

void readelf_S(const char* filename);

void readelf_s(const char* filename);
#endif //ELFREADER_HEADER_H
