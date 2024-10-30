

进入ELFReader所在目录执行如下命令即可

```sh
./ELFReader -h filename
```

```sh
./ELFReader -S filename
```

```sh
./ELFReader -s filename
```

其中filename是被解析的elf文件所在路径。

示例：

目录结构如下，ELFReader是elf文件解析器，exponential和helloworld是待解析测试文件

```sh
├── bin
│   ├── ELFReader
│   ├── exponential
│   └── helloworld
```

进入bin目录并执行

```
cd bin/
./ELFReader -h helloworld
```

输出：

```sh
ELF Header:
  Magic:	7f 45 4c 46 02 01 01 00 00 00 00 00 00 00 00 00 
  Class:				ELF64
  Data:					2's complement, little endian
  Version:				1 (current)
  OS/ABI:				UNIX System V ABI
  ABI Version:				0
  Type:					DYN (Shared object file)
  Machine:				AMD x86-64 architecture
  Version:				1
  Entry point address:			0x00000000000010c0
  Start of program headers:		64 (bytes into file)
  Start of section headers:		36440 (bytes into file)
  Flags:				0x0
  Size of this header:			64 (bytes)
  Size of program headers:		56 (bytes)
  Number of program headers:		13
  Size of section headers:		64 (bytes)
  Number of section headers:		36
  Section header string table index:	35
```

执行-S命令

```sh
./ELFReader -S helloworld
```

输出：

```sh
There are 36 section headers, starting at offset 0x8e58

Section Headers:
  [Nr]	Name			Type		Addr		Offset		Size		EntSize		Flags	Link	Info	Align
  [ 0]	                        NULL            0x00000000	0x00000000	0x00000000	0x00000000	        0       0       0       
  [ 1]	.interp                 PROGBITS        0x00000318	0x00000318	0x0000001c	0x00000000	A       0       0       1       
  [ 2]	.note.gnu.property      NOTE            0x00000338	0x00000338	0x00000020	0x00000000	A       0       0       8       
  [ 3]	.note.gnu.build-id      NOTE            0x00000358	0x00000358	0x00000024	0x00000000	A       0       0       4       
  [ 4]	.note.ABI-tag           NOTE            0x0000037c	0x0000037c	0x00000020	0x00000000	A       0       0       4       
  [ 5]	.gnu.hash               GNU_HASH        0x000003a0	0x000003a0	0x00000028	0x00000000	A       6       0       8       
  [ 6]	.dynsym                 DYNSYM          0x000003c8	0x000003c8	0x00000138	0x00000018	A       7       1       8       
  [ 7]	.dynstr                 STRTAB          0x00000500	0x00000500	0x00000163	0x00000000	A       0       0       1       
  [ 8]	.gnu.version            VERSYM          0x00000664	0x00000664	0x0000001a	0x00000002	A       6       0       2       
  [ 9]	.gnu.version_r          VERNEED         0x00000680	0x00000680	0x00000040	0x00000000	A       7       2       8       
  [10]	.rela.dyn               RELA            0x000006c0	0x000006c0	0x00000120	0x00000018	A       6       0       8       
  [11]	.rela.plt               RELA            0x000007e0	0x000007e0	0x00000060	0x00000018	AI      6       24      8       
  [12]	.init                   PROGBITS        0x00001000	0x00001000	0x0000001b	0x00000000	AX      0       0       4       
  [13]	.plt                    PROGBITS        0x00001020	0x00001020	0x00000050	0x00000010	AX      0       0       16      
  [14]	.plt.got                PROGBITS        0x00001070	0x00001070	0x00000010	0x00000010	AX      0       0       16      
  [15]	.plt.sec                PROGBITS        0x00001080	0x00001080	0x00000040	0x00000010	AX      0       0       16      
  [16]	.text                   PROGBITS        0x000010c0	0x000010c0	0x00000205	0x00000000	AX      0       0       16      
  [17]	.fini                   PROGBITS        0x000012c8	0x000012c8	0x0000000d	0x00000000	AX      0       0       4       
  [18]	.rodata                 PROGBITS        0x00002000	0x00002000	0x00000013	0x00000000	A       0       0       4       
  [19]	.eh_frame_hdr           PROGBITS        0x00002014	0x00002014	0x00000054	0x00000000	A       0       0       4       
  [20]	.eh_frame               PROGBITS        0x00002068	0x00002068	0x00000148	0x00000000	A       0       0       8       
  [21]	.init_array             INIT_ARRAY      0x00003d78	0x00002d78	0x00000010	0x00000008	WA      0       0       8       
  [22]	.fini_array             FINI_ARRAY      0x00003d88	0x00002d88	0x00000008	0x00000008	WA      0       0       8       
  [23]	.dynamic                DYNAMIC         0x00003d90	0x00002d90	0x00000200	0x00000010	WA      7       0       8       
  [24]	.got                    PROGBITS        0x00003f90	0x00002f90	0x00000070	0x00000008	WA      0       0       8       
  [25]	.data                   PROGBITS        0x00004000	0x00003000	0x00000010	0x00000000	WA      0       0       8       
  [26]	.bss                    NOBITS          0x00004040	0x00003010	0x00000118	0x00000000	WA      0       0       64      
  [27]	.comment                PROGBITS        0x00000000	0x00003010	0x0000002a	0x00000001	MS      0       0       1       
  [28]	.debug_aranges          PROGBITS        0x00000000	0x0000303a	0x00000030	0x00000000	        0       0       1       
  [29]	.debug_info             PROGBITS        0x00000000	0x0000306a	0x00002c34	0x00000000	        0       0       1       
  [30]	.debug_abbrev           PROGBITS        0x00000000	0x00005c9e	0x00000649	0x00000000	        0       0       1       
  [31]	.debug_line             PROGBITS        0x00000000	0x000062e7	0x00000406	0x00000000	        0       0       1       
  [32]	.debug_str              PROGBITS        0x00000000	0x000066ed	0x00001b08	0x00000001	MS      0       0       1       
  [33]	.symtab                 SYMTAB          0x00000000	0x000081f8	0x00000780	0x00000018	        34      55      8       
  [34]	.strtab                 STRTAB          0x00000000	0x00008978	0x00000381	0x00000000	        0       0       1       
  [35]	.shstrtab               STRTAB          0x00000000	0x00008cf9	0x0000015a	0x00000000	        0       0       1       
Key to Flags:
	W (write), A (alloc), X (execute), M (merge), S (strings), I (info),
	L (link order), O (extra OS processing required), G (group), T (TLS),
	C (compressed), x (unknown), o (OS specific), E (exclude),
	l (large), p (processor specific)

```

执行-s命令

```sh
./ELFReader -s helloworld
```

输出:

```sh
Symbol table '.dynsym' contains 13 entries
  Num:		Value		Size	Type	Bind	Vis	Ndx		Name
    0:	0x0000000000000000:	   0	NOTYPE	LOCAL	DEFAULT	UNDEF	
    1:	0x0000000000000000:	   0	FUNC	GLOBAL	DEFAULT	UNDEF	_ZSt4endlIcSt11char_traitsIcEERSt13basic_ostreamIT_T0_ES6_
    2:	0x0000000000000000:	   0	FUNC	GLOBAL	DEFAULT	UNDEF	__cxa_atexit
    3:	0x0000000000000000:	   0	FUNC	GLOBAL	DEFAULT	UNDEF	_ZStlsISt11char_traitsIcEERSt13basic_ostreamIcT_ES5_PKc
    4:	0x0000000000000000:	   0	FUNC	GLOBAL	DEFAULT	UNDEF	_ZNSolsEPFRSoS_E
    5:	0x0000000000000000:	   0	FUNC	GLOBAL	DEFAULT	UNDEF	_ZNSt8ios_base4InitC1Ev
    6:	0x0000000000000000:	   0	NOTYPE	WEAK	DEFAULT	UNDEF	_ITM_deregisterTMCloneTable
    7:	0x0000000000000000:	   0	FUNC	GLOBAL	DEFAULT	UNDEF	__libc_start_main
    8:	0x0000000000000000:	   0	NOTYPE	WEAK	DEFAULT	UNDEF	__gmon_start__
    9:	0x0000000000000000:	   0	NOTYPE	WEAK	DEFAULT	UNDEF	_ITM_registerTMCloneTable
   10:	0x0000000000000000:	   0	FUNC	GLOBAL	DEFAULT	UNDEF	_ZNSt8ios_base4InitD1Ev
   11:	0x0000000000000000:	   0	FUNC	WEAK	DEFAULT	UNDEF	__cxa_finalize
   12:	0x0000000000004040:	 272	OBJECT	GLOBAL	DEFAULT	  26	_ZSt4cout

Symbol table '.symtab' contains 80 entries
  Num:		Value		Size	Type	Bind	Vis	Ndx		Name
    0:	0x0000000000000000:	   0	NOTYPE	LOCAL	DEFAULT	UNDEF	
    1:	0x0000000000000318:	   0	SECTION	LOCAL	DEFAULT	   1	
    2:	0x0000000000000338:	   0	SECTION	LOCAL	DEFAULT	   2	
    3:	0x0000000000000358:	   0	SECTION	LOCAL	DEFAULT	   3	
    4:	0x000000000000037c:	   0	SECTION	LOCAL	DEFAULT	   4	
    5:	0x00000000000003a0:	   0	SECTION	LOCAL	DEFAULT	   5	
    6:	0x00000000000003c8:	   0	SECTION	LOCAL	DEFAULT	   6	
    7:	0x0000000000000500:	   0	SECTION	LOCAL	DEFAULT	   7	
    8:	0x0000000000000664:	   0	SECTION	LOCAL	DEFAULT	   8	
    9:	0x0000000000000680:	   0	SECTION	LOCAL	DEFAULT	   9	
   10:	0x00000000000006c0:	   0	SECTION	LOCAL	DEFAULT	  10	
   11:	0x00000000000007e0:	   0	SECTION	LOCAL	DEFAULT	  11	
   12:	0x0000000000001000:	   0	SECTION	LOCAL	DEFAULT	  12	
   13:	0x0000000000001020:	   0	SECTION	LOCAL	DEFAULT	  13	
   14:	0x0000000000001070:	   0	SECTION	LOCAL	DEFAULT	  14	
   15:	0x0000000000001080:	   0	SECTION	LOCAL	DEFAULT	  15	
   16:	0x00000000000010c0:	   0	SECTION	LOCAL	DEFAULT	  16	
   17:	0x00000000000012c8:	   0	SECTION	LOCAL	DEFAULT	  17	
   18:	0x0000000000002000:	   0	SECTION	LOCAL	DEFAULT	  18	
   19:	0x0000000000002014:	   0	SECTION	LOCAL	DEFAULT	  19	
   20:	0x0000000000002068:	   0	SECTION	LOCAL	DEFAULT	  20	
   21:	0x0000000000003d78:	   0	SECTION	LOCAL	DEFAULT	  21	
   22:	0x0000000000003d88:	   0	SECTION	LOCAL	DEFAULT	  22	
   23:	0x0000000000003d90:	   0	SECTION	LOCAL	DEFAULT	  23	
   24:	0x0000000000003f90:	   0	SECTION	LOCAL	DEFAULT	  24	
   25:	0x0000000000004000:	   0	SECTION	LOCAL	DEFAULT	  25	
   26:	0x0000000000004040:	   0	SECTION	LOCAL	DEFAULT	  26	
   27:	0x0000000000000000:	   0	SECTION	LOCAL	DEFAULT	  27	
   28:	0x0000000000000000:	   0	SECTION	LOCAL	DEFAULT	  28	
   29:	0x0000000000000000:	   0	SECTION	LOCAL	DEFAULT	  29	
   30:	0x0000000000000000:	   0	SECTION	LOCAL	DEFAULT	  30	
   31:	0x0000000000000000:	   0	SECTION	LOCAL	DEFAULT	  31	
   32:	0x0000000000000000:	   0	SECTION	LOCAL	DEFAULT	  32	
   33:	0x0000000000000000:	   0	FILE	LOCAL	DEFAULT	 ABS	crtstuff.c
   34:	0x00000000000010f0:	   0	FUNC	LOCAL	DEFAULT	  16	deregister_tm_clones
   35:	0x0000000000001120:	   0	FUNC	LOCAL	DEFAULT	  16	register_tm_clones
   36:	0x0000000000001160:	   0	FUNC	LOCAL	DEFAULT	  16	__do_global_dtors_aux
   37:	0x0000000000004150:	   1	OBJECT	LOCAL	DEFAULT	  26	completed.8060
   38:	0x0000000000003d88:	   0	OBJECT	LOCAL	DEFAULT	  22	__do_global_dtors_aux_fini_array_entry
   39:	0x00000000000011a0:	   0	FUNC	LOCAL	DEFAULT	  16	frame_dummy
   40:	0x0000000000003d78:	   0	OBJECT	LOCAL	DEFAULT	  21	__frame_dummy_init_array_entry
   41:	0x0000000000000000:	   0	FILE	LOCAL	DEFAULT	 ABS	main.cpp
   42:	0x0000000000002004:	   1	OBJECT	LOCAL	DEFAULT	  18	_ZStL19piecewise_construct
   43:	0x0000000000004151:	   1	OBJECT	LOCAL	DEFAULT	  26	_ZStL8__ioinit
   44:	0x00000000000011e0:	  77	FUNC	LOCAL	DEFAULT	  16	_Z41__static_initialization_and_destruction_0ii
   45:	0x000000000000122d:	  25	FUNC	LOCAL	DEFAULT	  16	_GLOBAL__sub_I_main
   46:	0x0000000000000000:	   0	FILE	LOCAL	DEFAULT	 ABS	crtstuff.c
   47:	0x00000000000021ac:	   0	OBJECT	LOCAL	DEFAULT	  20	__FRAME_END__
   48:	0x0000000000000000:	   0	FILE	LOCAL	DEFAULT	 ABS	
   49:	0x0000000000002014:	   0	NOTYPE	LOCAL	DEFAULT	  19	__GNU_EH_FRAME_HDR
   50:	0x0000000000001000:	   0	FUNC	LOCAL	DEFAULT	  12	_init
   51:	0x0000000000003d90:	   0	OBJECT	LOCAL	DEFAULT	  23	_DYNAMIC
   52:	0x0000000000003d88:	   0	NOTYPE	LOCAL	DEFAULT	  21	__init_array_end
   53:	0x0000000000003d78:	   0	NOTYPE	LOCAL	DEFAULT	  21	__init_array_start
   54:	0x0000000000003f90:	   0	OBJECT	LOCAL	DEFAULT	  24	_GLOBAL_OFFSET_TABLE_
   55:	0x0000000000004010:	   0	NOTYPE	GLOBAL	DEFAULT	  25	_edata
   56:	0x0000000000004000:	   0	NOTYPE	WEAK	DEFAULT	  25	data_start
   57:	0x0000000000002000:	   4	OBJECT	GLOBAL	DEFAULT	  18	_IO_stdin_used
   58:	0x0000000000000000:	   0	FUNC	WEAK	DEFAULT	UNDEF	__cxa_finalize@@GLIBC_2.2.5
   59:	0x00000000000011a9:	  55	FUNC	GLOBAL	DEFAULT	  16	main
   60:	0x0000000000000000:	   0	FUNC	GLOBAL	DEFAULT	UNDEF	_ZSt4endlIcSt11char_traitsIcEERSt13basic_ostreamIT_T0_ES6_@@GLIBCXX_3.4
   61:	0x0000000000004008:	   0	OBJECT	GLOBAL	DEFAULT	  25	__dso_handle
   62:	0x00000000000012c8:	   0	FUNC	GLOBAL	DEFAULT	  17	_fini
   63:	0x0000000000000000:	   0	FUNC	GLOBAL	DEFAULT	UNDEF	__cxa_atexit@@GLIBC_2.2.5
   64:	0x00000000000010c0:	  47	FUNC	GLOBAL	DEFAULT	  16	_start
   65:	0x0000000000000000:	   0	FUNC	GLOBAL	DEFAULT	UNDEF	_ZStlsISt11char_traitsIcEERSt13basic_ostreamIcT_ES5_PKc@@GLIBCXX_3.4
   66:	0x0000000000000000:	   0	FUNC	GLOBAL	DEFAULT	UNDEF	_ZNSolsEPFRSoS_E@@GLIBCXX_3.4
   67:	0x0000000000004010:	   0	OBJECT	GLOBAL	DEFAULT	  25	__TMC_END__
   68:	0x0000000000004040:	 272	OBJECT	GLOBAL	DEFAULT	  26	_ZSt4cout@@GLIBCXX_3.4
   69:	0x0000000000004000:	   0	NOTYPE	GLOBAL	DEFAULT	  25	__data_start
   70:	0x0000000000004158:	   0	NOTYPE	GLOBAL	DEFAULT	  26	_end
   71:	0x0000000000004010:	   0	NOTYPE	GLOBAL	DEFAULT	  26	__bss_start
   72:	0x0000000000000000:	   0	FUNC	GLOBAL	DEFAULT	UNDEF	_ZNSt8ios_base4InitC1Ev@@GLIBCXX_3.4
   73:	0x0000000000001250:	 101	FUNC	GLOBAL	DEFAULT	  16	__libc_csu_init
   74:	0x0000000000000000:	   0	NOTYPE	WEAK	DEFAULT	UNDEF	_ITM_deregisterTMCloneTable
   75:	0x00000000000012c0:	   5	FUNC	GLOBAL	DEFAULT	  16	__libc_csu_fini
   76:	0x0000000000000000:	   0	FUNC	GLOBAL	DEFAULT	UNDEF	__libc_start_main@@GLIBC_2.2.5
   77:	0x0000000000000000:	   0	NOTYPE	WEAK	DEFAULT	UNDEF	__gmon_start__
   78:	0x0000000000000000:	   0	NOTYPE	WEAK	DEFAULT	UNDEF	_ITM_registerTMCloneTable
   79:	0x0000000000000000:	   0	FUNC	GLOBAL	DEFAULT	UNDEF	_ZNSt8ios_base4InitD1Ev@@GLIBCXX_3.4

```

