#include <cerrno>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <cstdint>

/** e_ident identification indexes */
#define EI_MAG0		0		/* File identification byte 0 index */
#define ELFMAG0		0x7f		/* Magic number byte 0 */

#define EI_MAG1		1		/* File identification byte 1 index */
#define ELFMAG1		'E'		/* Magic number byte 1 */

#define EI_MAG2		2		/* File identification byte 2 index */
#define ELFMAG2		'L'		/* Magic number byte 2 */

#define EI_MAG3		3		/* File identification byte 3 index */
#define ELFMAG3		'F'		/* Magic number byte 3 */

/* Conglomeration of the identification bytes, for easy testing as a word.  */
#define	ELFMAG		"\177ELF"
#define	SELFMAG		4

#define EI_CLASS	4		/* File class byte index */
#define ELFCLASSNONE	0		/* Invalid class */
#define ELFCLASS32	1		/* 32-bit objects */
#define ELFCLASS64	2		/* 64-bit objects */
#define ELFCLASSNUM	3

#define EI_DATA		5		/* Data encoding byte index */
#define ELFDATANONE	0		/* Invalid data encoding */
#define ELFDATA2LSB	1		/* 2's complement, little endian */
#define ELFDATA2MSB	2		/* 2's complement, big endian */
#define ELFDATANUM	3

#define EI_VERSION	6		/* File version byte index */
/* Value must be EV_CURRENT */


#define EI_OSABI	7		/* OS ABI identification */
#define ELFOSABI_NONE		0	/* UNIX System V ABI */
#define ELFOSABI_SYSV		0	/* Alias.  */
#define ELFOSABI_HPUX		1	/* HP-UX */
#define ELFOSABI_NETBSD		2	/* NetBSD.  */
#define ELFOSABI_GNU		3	/* Object uses GNU ELF extensions.  */
#define ELFOSABI_LINUX		ELFOSABI_GNU /* Compatibility alias.  */
#define ELFOSABI_SOLARIS	6	/* Sun Solaris.  */
#define ELFOSABI_AIX		7	/* IBM AIX.  */
#define ELFOSABI_IRIX		8	/* SGI Irix.  */
#define ELFOSABI_FREEBSD	9	/* FreeBSD.  */
#define ELFOSABI_TRU64		10	/* Compaq TRU64 UNIX.  */
#define ELFOSABI_MODESTO	11	/* Novell Modesto.  */
#define ELFOSABI_OPENBSD	12	/* OpenBSD.  */
#define ELFOSABI_ARM_AEABI	64	/* ARM EABI */
#define ELFOSABI_ARM		97	/* ARM */
#define ELFOSABI_STANDALONE	255	/* Standalone (embedded) application */

#define EI_ABIVERSION	8		/* ABI version */
#define EI_PAD		9		/* Byte index of padding bytes */

/** EI_VERSION */
#define EV_NONE			0		//Invalid version
#define EV_CURRENT		1		//Current version

/** e_type */
#define ET_NONE		0		/* No file type */
#define ET_REL		1		/* Relocatable file */
#define ET_EXEC		2		/* Executable file */
#define ET_DYN		3		/* Shared object file */
#define ET_CORE		4		/* Core file */
#define	ET_NUM		5		/* Number of defined types */
#define ET_LOOS		0xfe00		/* OS-specific range start */
#define ET_HIOS		0xfeff		/* OS-specific range end */
#define ET_LOPROC	0xff00		/* Processor-specific range start */
#define ET_HIPROC	0xffff		/* Processor-specific range end */

/** e_machine */
#define EM_NONE		 0	/* No machine */
#define EM_M32		 1	/* AT&T WE 32100 */
#define EM_SPARC	 2	/* SUN SPARC */
#define EM_386		 3	/* Intel 80386 */
#define EM_68K		 4	/* Motorola m68k family */
#define EM_88K		 5	/* Motorola m88k family */
#define EM_IAMCU	 6	/* Intel MCU */
#define EM_860		 7	/* Intel 80860 */
#define EM_MIPS		 8	/* MIPS R3000 big-endian */


/** section header tables */
#define	SHN_UNDEF		0			//Undefined, missing, irrelevant
#define SHN_LORESERVE	0xff00		//The lower bound of the range of reserved indexes
#define SHN_LOPROC		0xff00		//Reserved for processor-specific semantics
#define SHN_HIPROC		0xff1f		//Reserved for processor-specific semantics
#define SHN_ABS			0xfff1		//This value specifies absolute values for the corresponding reference
#define SHN_COMMON		0xfff2		//Symbols defined relative to this section are common symbols
#define SHN_HIRESERVE	0xffff		//This value specifies the upper bound of the range of reserved indexes

/* Legal values for sh_type (section type).  */
#define SHT_NULL	  0		/* Section header table entry unused */
#define SHT_PROGBITS	  1		/* Program data 程序定义的信息*/
#define SHT_SYMTAB	  2		/* Symbol table 	符号表*/
#define SHT_STRTAB	  3		/* String table 字符串表*/
#define SHT_RELA	  4		/* Relocation entries with addends 有确定的加数的重定位信息，如 ELF32_Rela*/
#define SHT_HASH	  5		/* Symbol hash table 符号哈希表*/
#define SHT_DYNAMIC	  6		/* Dynamic linking information 动态链接信息*/
#define SHT_NOTE	  7		/* Notes 文件标记信息*/
#define SHT_NOBITS	  8		/* Program space with no data (bss) 在文件中不占用空间，其他类似 SHT_PROGBITS 。尽管这个节不会占据空间，它也会有一个理论上的大小*/
#define SHT_REL		  9		/* Relocation entries, no addends 没有确定加数的重定位信息，如 ELF32_Rel*/
#define SHT_SHLIB	  10		/* Reserved 	保留*/
#define SHT_DYNSYM	  11		/* Dynamic linker symbol table 符号表*/
#define SHT_INIT_ARRAY	  14		/* Array of constructors */
#define SHT_FINI_ARRAY	  15		/* Array of destructors */
#define SHT_PREINIT_ARRAY 16		/* Array of pre-constructors */
#define SHT_GROUP	  17		/* Section group */
#define SHT_SYMTAB_SHNDX  18		/* Extended section indices */
#define	SHT_NUM		  19		/* Number of defined types.  */
#define SHT_LOOS	  0x60000000	/* Start OS-specific.  */
#define SHT_GNU_ATTRIBUTES 0x6  ffffff5	/* Object attributes.  */
#define SHT_GNU_HASH	  0x6ffffff6	/* GNU-style hash table.  */
#define SHT_GNU_LIBLIST	  0x6ffffff7	/* Prelink library list */
#define SHT_CHECKSUM	  0x6ffffff8	/* Checksum for DSO content.  */
#define SHT_LOSUNW	  0x6ffffffa	/* Sun-specific low bound.  */
#define SHT_SUNW_move	  0x6ffffffa
#define SHT_SUNW_COMDAT   0x6ffffffb
#define SHT_SUNW_syminfo  0x6ffffffc
#define SHT_GNU_verdef	  0x6ffffffd	/* Version definition section.  */
#define SHT_GNU_verneed	  0x6ffffffe	/* Version needs section.  */
#define SHT_GNU_versym	  0x6fffffff	/* Version symbol table.  */
#define SHT_HISUNW	  0x6fffffff	/* Sun-specific high bound.  */
#define SHT_HIOS	  0x6fffffff	/* End OS-specific type */
#define SHT_LOPROC	  0x70000000	/* Start of processor-specific */
#define SHT_HIPROC	  0x7fffffff	/* End of processor-specific */
#define SHT_LOUSER	  0x80000000	/* Start of application-specific 为程序保留的地址的上界*/
#define SHT_HIUSER	  0x8fffffff	/* End of application-specific 为程序保留的地址的上界*/

/** sh_flags */
#define SHF_WRITE	     (1 << 0)	/* Writable 节中存放了在进程执行时可写的数据.*/
#define SHF_ALLOC	     (1 << 1)	/* Occupies memory during execution 标识了这个节在程序运行时是否会占据内存。有一些控制节是不会驻留在内存镜像中的，这些节的这个属性就会被关闭.*/
#define SHF_EXECINSTR	     (1 << 2)	/* Executable 标识了这个节含有可执行的机器指令.*/
#define SHF_MERGE	     (1 << 4)	/* Might be merged */
#define SHF_STRINGS	     (1 << 5)	/* Contains nul-terminated strings */
#define SHF_INFO_LINK	     (1 << 6)	/* `sh_info' contains SHT index */
#define SHF_LINK_ORDER	     (1 << 7)	/* Preserve order after combining */
#define SHF_OS_NONCONFORMING (1 << 8)	/* Non-standard OS specific handling
					   required */
#define SHF_GROUP	     (1 << 9)	/* Section is member of a group.  */
#define SHF_TLS		     (1 << 10)	/* Section hold thread-local data.  */
#define SHF_COMPRESSED	     (1 << 11)	/* Section with compressed data. */
#define SHF_MASKOS	     0x0ff00000	/* OS-specific.  */
#define SHF_MASKPROC	     0xf0000000	/* Processor-specific 保留给处理器*/
#define SHF_GNU_RETAIN	     (1 << 21)  /* Not to be GCed by linker.  */
#define SHF_ORDERED	     (1 << 30)	/* Special ordering requirement
					   (Solaris).  */
#define SHF_EXCLUDE	     (1U << 31)	/* Section is excluded unless
					   referenced or allocated (Solaris).*/

/* Possible values for si_boundto.  */
#define SYMINFO_BT_SELF		0xffff	/* Symbol bound to self */
#define SYMINFO_BT_PARENT	0xfffe	/* Symbol bound to parent */
#define SYMINFO_BT_LOWRESERVE	0xff00	/* Beginning of reserved entries */

/* Possible bitmasks for si_flags.  */
#define SYMINFO_FLG_DIRECT	0x0001	/* Direct bound symbol */
#define SYMINFO_FLG_PASSTHRU	0x0002	/* Pass-thru symbol for translator */
#define SYMINFO_FLG_COPY	0x0004	/* Symbol is a copy-reloc */
#define SYMINFO_FLG_LAZYLOAD	0x0008	/* Symbol bound to object to be lazy
					   loaded */
/* Syminfo version values.  */
#define SYMINFO_NONE		0
#define SYMINFO_CURRENT		1
#define SYMINFO_NUM		2


/* How to extract and insert information held in the st_info field.  */

#define ELF32_ST_BIND(val)		(((unsigned char) (val)) >> 4)
#define ELF32_ST_TYPE(val)		((val) & 0xf)
#define ELF32_ST_INFO(bind, type)	(((bind) << 4) + ((type) & 0xf))

/* Both Elf32_Sym and Elf64_Sym use the same one-byte st_info field.  */
#define ELF64_ST_BIND(val)		ELF32_ST_BIND (val)
#define ELF64_ST_TYPE(val)		ELF32_ST_TYPE (val)
#define ELF64_ST_INFO(bind, type)	ELF32_ST_INFO ((bind), (type))

/* Legal values for ST_BIND subfield of st_info (symbol binding).  */

#define STB_LOCAL	0		/* Local symbol */
#define STB_GLOBAL	1		/* Global symbol */
#define STB_WEAK	2		/* Weak symbol */
#define	STB_NUM		3		/* Number of defined types.  */
#define STB_LOOS	10		/* Start of OS-specific */
#define STB_GNU_UNIQUE	10		/* Unique symbol.  */
#define STB_HIOS	12		/* End of OS-specific */
#define STB_LOPROC	13		/* Start of processor-specific */
#define STB_HIPROC	15		/* End of processor-specific */

/* Symbol table indices are found in the hash buckets and chain table
   of a symbol hash table section.  This special index value indicates
   the end of a chain, meaning no further symbols are found in that bucket.  */

#define STN_UNDEF	0		/* End of a chain.  */


/* How to extract and insert information held in the st_other field.  */

#define ELF32_ST_VISIBILITY(o)	((o) & 0x03)

/* For ELF64 the definitions are the same.  */
#define ELF64_ST_VISIBILITY(o)	ELF32_ST_VISIBILITY (o)

/* Symbol visibility specification encoded in the st_other field.  */
#define STV_DEFAULT	0		/* Default symbol visibility rules */
#define STV_INTERNAL	1		/* Processor specific hidden class */
#define STV_HIDDEN	2		/* Sym unavailable in other modules */
#define STV_PROTECTED	3		/* Not preemptible, not exported */

/** stb bind type */
#define STT_NOTYPE	0		/* Symbol type is unspecified */
#define STT_OBJECT	1		/* Symbol is a data object */
#define STT_FUNC	2		/* Symbol is a code object */
#define STT_SECTION	3		/* Symbol associated with a section */
#define STT_FILE	4		/* Symbol's name is file name */
#define STT_COMMON	5		/* Symbol is a common data object */
#define STT_TLS		6		/* Symbol is thread-local data object*/
#define	STT_NUM		7		/* Number of defined types.  */
#define STT_LOOS	10		/* Start of OS-specific */
#define STT_GNU_IFUNC	10		/* Symbol is indirect code object */
#define STT_HIOS	12		/* End of OS-specific */
#define STT_LOPROC	13		/* Start of processor-specific */
#define STT_HIPROC	15		/* End of processor-specific */

/* Legal values for p_type (segment type).  */

#define	PT_NULL		0		/* Program header table entry unused */
#define PT_LOAD		1		/* Loadable program segment */
#define PT_DYNAMIC	2		/* Dynamic linking information */
#define PT_INTERP	3		/* Program interpreter */
#define PT_NOTE		4		/* Auxiliary information */
#define PT_SHLIB	5		/* Reserved */
#define PT_PHDR		6		/* Entry for header table itself */
#define PT_TLS		7		/* Thread-local storage segment */
#define	PT_NUM		8		/* Number of defined types */
#define PT_LOOS		0x60000000	/* Start of OS-specific */
#define PT_L4_STACK     	(PT_LOOS + 0x12)
#define PT_L4_KIP       		(PT_LOOS + 0x13)
#define PT_L4_AUX       		(PT_LOOS + 0x14)
#define PT_GNU_EH_FRAME	0x6474e550	/* GCC .eh_frame_hdr segment */
#define PT_GNU_STACK	0x6474e551	/* Indicates stack executability */
#define PT_GNU_RELRO	0x6474e552	/* Read-only after relocation */
#define PT_GNU_PROPERTY	0x6474e553	/* GNU property */
#define PT_LOSUNW	0x6ffffffa
#define PT_SUNWBSS	0x6ffffffa	/* Sun Specific segment */
#define PT_SUNWSTACK	0x6ffffffb	/* Stack segment */
#define PT_HISUNW	0x6fffffff
#define PT_HIOS		0x6fffffff	/* End of OS-specific */
#define PT_LOPROC	0x70000000	/* Start of processor-specific */
#define PT_HIPROC	0x7fffffff	/* End of processor-specific */

/* Legal values for p_flags (segment flags).  */

#define PF_X		(1 << 0)	/* Segment is executable */
#define PF_W		(1 << 1)	/* Segment is writable */
#define PF_R		(1 << 2)	/* Segment is readable */
#define PF_MASKOS	0x0ff00000	/* OS-specific */
#define PF_MASKPROC	0xf0000000	/* Processor-specific */

/** Dynamic Section */
#define DT_NULL		0		/* Marks end of dynamic section */
#define DT_NEEDED	1		/* Name of needed library */
#define DT_PLTRELSZ	2		/* Size in bytes of PLT relocs */
#define DT_PLTGOT	3		/* Processor defined value */
#define DT_HASH		4		/* Address of symbol hash table */
#define DT_STRTAB	5		/* Address of string table */
#define DT_SYMTAB	6		/* Address of symbol table */
#define DT_RELA		7		/* Address of Rela relocs */
#define DT_RELASZ	8		/* Total size of Rela relocs */
#define DT_RELAENT	9		/* Size of one Rela reloc */
#define DT_STRSZ	10		/* Size of string table */
#define DT_SYMENT	11		/* Size of one symbol table entry */
#define DT_INIT		12		/* Address of init function */
#define DT_FINI		13		/* Address of termination function */
#define DT_SONAME	14		/* Name of shared object */
#define DT_RPATH	15		/* Library search path (deprecated) */
#define DT_SYMBOLIC	16		/* Start symbol search here */
#define DT_REL		17		/* Address of Rel relocs */
#define DT_RELSZ	18		/* Total size of Rel relocs */
#define DT_RELENT	19		/* Size of one Rel reloc */
#define DT_PLTREL	20		/* Type of reloc in PLT */
#define DT_DEBUG	21		/* For debugging; unspecified */
#define DT_TEXTREL	22		/* Reloc might modify .text */
#define DT_JMPREL	23		/* Address of PLT relocs */
#define	DT_BIND_NOW	24		/* Process relocations of object */
#define	DT_INIT_ARRAY	25		/* Array with addresses of init fct */
#define	DT_FINI_ARRAY	26		/* Array with addresses of fini fct */
#define	DT_INIT_ARRAYSZ	27		/* Size in bytes of DT_INIT_ARRAY */
#define	DT_FINI_ARRAYSZ	28		/* Size in bytes of DT_FINI_ARRAY */
#define DT_RUNPATH	29		/* Library search path */
#define DT_FLAGS	30		/* Flags for the object being loaded */
#define DT_ENCODING	32		/* Start of encoded range */
#define DT_PREINIT_ARRAY 32		/* Array with addresses of preinit fct*/
#define DT_PREINIT_ARRAYSZ 33		/* size in bytes of DT_PREINIT_ARRAY */
#define DT_SYMTAB_SHNDX	34		/* Address of SYMTAB_SHNDX section */
#define	DT_NUM		35		/* Number used */
#define DT_LOOS		0x6000000d	/* Start of OS-specific */
#define DT_HIOS		0x6ffff000	/* End of OS-specific */
#define DT_LOPROC	0x70000000	/* Start of processor-specific */
#define DT_HIPROC	0x7fffffff	/* End of processor-specific */
#define	DT_PROCNUM	DT_MIPS_NUM	/* Most used by any processor */

/** Relocation Types */
#define R_386_NONE	   0		/* No reloc */
#define R_386_32	   1		/* Direct 32 bit  */
#define R_386_PC32	   2		/* PC relative 32 bit */
#define R_386_GOT32	   3		/* 32 bit GOT entry */
#define R_386_PLT32	   4		/* 32 bit PLT address */
#define R_386_COPY	   5		/* Copy symbol at runtime */
#define R_386_GLOB_DAT	   6		/* Create GOT entry */
#define R_386_JMP_SLOT	   7		/* Create PLT entry */
#define R_386_RELATIVE	   8		/* Adjust by program base */
#define R_386_GOTOFF	   9		/* 32 bit offset to GOT */
#define R_386_GOTPC	   10		/* 32 bit PC relative offset to GOT */


typedef unsigned long long 		elf64_Addr;
typedef	unsigned short 			elf64_Half;
typedef signed short				elf64_SHalf;
typedef unsigned long long		elf64_Off;
typedef signed int 				elf64_Sword;
typedef unsigned int 				elf64_Word;
typedef unsigned long long 		elf64_Xword;
typedef signed long long 			elf64_Sxword;
typedef uint16_t Elf64_Section;

#define EI_NIDENT   16
typedef struct {
    // magic number(4)+EI_CLASS(1)+EI_DATA(1)+EI_VERSION(1)+EI_OSABI(1)+EI_ABIVERSION(1)+EI_PAD(7)
    unsigned char e_ident[EI_NIDENT];

    elf64_Half e_type;      // 标识了对象文件的类型
    elf64_Half e_machine;   // 标识了文件对应的处理器架构
    elf64_Word e_version;   // 标识了对象文件的版本
    elf64_Addr e_entry;     // 进程开始的地址,如果程序没有入口点，那么这个参数的字段就是 0。
    elf64_Off  e_phoff;      // 程序头表在文件中的偏移量，如果文件中没有程序头表，那么这个字段的值就是 0。
    elf64_Off  e_shoff;      // 节头表在文件中的偏移量，如果文件中没有节头表，那么这个字段的值就是 0。
    elf64_Word e_flags;     // 与文件相关的处理器标志
    elf64_Half e_ehsize;    // ELF 头的大小，以字节为单位
    elf64_Half e_phentsize; // 文件的程序头表中一个条目的长度，以字节为单位。表中的每个条目的长度都是相等的。
    elf64_Half e_phnum;    // 标识了程序头表中的条目数。如果没有程序头表，那么这个字段值为 0。通过和 e_phentsize 字段相乘，可以计算出程序头表的大小。
    elf64_Half e_shentsize; // 文件的节头表中一个条目的长度
    elf64_Half e_shnum;     // 节头表中的条目数。如果没有节头表，那么这个字段的值为 0。
    elf64_Half e_shstrndx;  // 节头表中与节名称字符串表相关的表项的索引（包含了所有节的名称）。如果文件中没有节名称字符串表，那么这个字段的值就是 SHN_UNDEF。
}elf64_ehdr;

typedef struct
{
    elf64_Word	sh_name;		/* Section name (string tbl index) */
    elf64_Word	sh_type;		/* Section type */
    elf64_Xword	sh_flags;		/* Section flags */
    elf64_Addr	sh_addr;		/* Section virtual addr at execution 如果一个节在会出现在进程的内存镜像中，那么这个成员的值就是节中第一个字节在内存中的地址*/
    elf64_Off	    sh_offset;		/* Section file offset 从文件开始到这个节头对应的节的第一个字节的偏移量*/
    elf64_Xword	sh_size;		/* Section size in bytes 以字节为单位给出了节的大小。如果是不占据空间的 SHT_NOBITS 节，这个属性也可能不为 0，只是实际上这个节不占据空间罢*/
    elf64_Word	sh_link;		/* Link to another section 节头表的索引链接*/
    elf64_Word	sh_info;		/* Additional section information 节的额外信息*/
    elf64_Xword	sh_addralign;		/* Section alignment 对齐的单位， sh_addr 属性必须是这个值的整数倍*/
    elf64_Xword	sh_entsize;		/* Entry size if section holds table */
} elf64_shdr;

typedef struct {
    elf64_Word  p_type;    // 标识了段的类型
    elf64_Word  p_flags;   // 标识了与段相关的标志
    elf64_Off   p_offset;   // 段在文件中的偏移量
    elf64_Addr  p_vaddr;   // 段在内存中的虚拟地址
    elf64_Addr  p_paddr;   // 段在内存中的物理地址
    elf64_Xword p_filesz; // 段在文件中的长度
    elf64_Xword p_memsz;  // 段在内存中的长度
    elf64_Xword p_align;  // 段在内存中的对齐方式
}elf64_phdr;

/* Symbol table entry.  */
typedef struct
{
    elf64_Word	    st_name;		/* Symbol name (string tbl index) */
    unsigned char	st_info;		/* Symbol type and binding */
    unsigned char   st_other;		/* Symbol visibility */
    Elf64_Section	st_shndx;		/* Section index */
    elf64_Addr	    st_value;		/* Symbol value */
    elf64_Xword	    st_size;		/* Symbol size */
} elf64_sym;
