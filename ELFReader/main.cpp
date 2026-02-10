#include <inaddr.h>
#include "elf.h"

#define assert_eq(lhs, rhs, fmt)	assert_op(lhs, rhs, fmt, ==)
#define assert_ne(lhs, rhs, fmt)	assert_op(lhs, rhs, fmt, !=)
#define assert_lt(lhs, rhs, fmt)	assert_op(lhs, rhs, fmt, <)
#define assert_ge(lhs, rhs, fmt)	assert_op(lhs, rhs, fmt, >=)

#define MAX_LEN		60*1024*1024
unsigned char buf[MAX_LEN];

const char software_name[] = "ELFReader";

// todo
void show_symbol(const unsigned char *data){
    unsigned char *sym_name;
    int len;
    elf64_shdr *section_header;

    elf64_ehdr *header = (elf64_ehdr *)data;
    elf64_sym *sym_header;

    int shnum = header->e_shnum;
    int e_shoff = header->e_shoff;
    section_header = (elf64_shdr*)(data + e_shoff +  sizeof(*section_header) * (header->e_shstrndx ));

    unsigned char *sec_name = (unsigned char *)(data + section_header->sh_offset);
    for (int i=0;i<shnum;++i){
        section_header = (elf64_shdr*)(data + i * sizeof(*section_header) + e_shoff);
        if (strcmp((const char *)(sec_name + section_header->sh_name), ".strtab") == 0) {
            sym_name = (unsigned char *)(data + section_header->sh_offset);
        }
    }

    printf("111 %x\n", section_header->sh_name );
    printf("222 %06x\t%06x\n",section_header->sh_addr ,section_header->sh_offset);
    printf("333 offset %x\n",section_header->sh_offset );

    for (int i=0;i<shnum;++i){
        section_header = (elf64_shdr *)(data + i * sizeof(*section_header) + e_shoff);
        if (strcmp((const char *)(sec_name + (uintptr_t)section_header->sh_name), ".dynsym") == 0) {
            len = (section_header->sh_size / section_header->sh_entsize);
            printf("Symbol table '.dynsym' contains %d entries:\n",len);
            printf("\t[Nr] \tValue\tSize\tBind\tType\tInfo\tName\n");
            for (int j = 0;j<len;++j){
                sym_header = (elf64_sym*)(data+section_header->sh_offset+j*(sizeof (*sym_header)));
                printf("\t%2d: ",j);
                printf(" %08x\t",sym_header->st_value );
                printf("%4d\t",sym_header->st_size );

                switch(ELF64_ST_TYPE(sym_header->st_info)){
                    case STT_NOTYPE:
                        printf("NOTYPE\t");
                        break;
                    case STT_OBJECT:
                        printf("OBJECT\t");
                        break;
                    case STT_FUNC:
                        printf("FUNC\t");
                        break;
                    case STT_SECTION:
                        printf("SECTION\t");
                        break;
                    case STT_FILE:
                        printf("FILE\t");
                        break;
                    case STT_COMMON:
                        printf("COMMON\t");
                        break;
                    case STT_TLS:
                        printf("TLS\t");
                        break;
                    case STT_NUM:
                        printf("NUM\t");
                        break;
                    case STT_LOOS:
                        printf("LOOS\t");
                        break;
                    case STT_HIOS:
                        printf("HIOS\t");
                        break;
                    case STT_LOPROC:
                        printf("LOPROC\t");
                        break;
                    case STT_HIPROC:
                        printf("HIPROC\t");
                        break;
                    default:
                        printf("Unknown\t");
                        break;
                }
                switch (ELF64_ST_BIND(sym_header->st_info))
                {
                    case STB_LOCAL:
                        printf("LOCAL\t");
                        break;
                    case STB_GLOBAL:
                        printf("GLOBAL\t");
                        break;
                    case STB_WEAK:
                        printf("WEAK\t");
                        break;
                    case STB_NUM:
                        printf("NUM\t");
                        break;
                    case STB_LOOS:
                        printf("LOOS\t");
                        break;
                    case STB_HIOS:
                        printf("HIOS\t");
                        break;
                    case STB_LOPROC:
                        printf("LOPROC\t");
                        break;
                    case STB_HIPROC:
                        printf("HIPROC\t");
                        break;
                    default:
                        printf("Unknown\t");
                        break;
                }
                printf("\t");
                char *testss = (char *)(sym_name + sym_header->st_name);
                printf(" %s\n",sym_name+sym_header->st_name);
                //symbol table .dynsym
            }
        }
    }
}

void show_section_header(const unsigned char *data){
    elf64_shdr *section_header;

    elf64_ehdr *header = (elf64_ehdr *)data;
    int shnum =  header->e_shnum;
    int s_off = header->e_shoff;
    section_header = (elf64_shdr*)(data + s_off +  sizeof(*section_header) * (header->e_shstrndx));
    unsigned char *name = (unsigned char *)(data + section_header->sh_offset);

    printf("Total %d section header, start from address 0x%x:\n\n", shnum,s_off);
    printf("Section header:\n");
    printf(" [Nr] %-19s%-9s%-9s%-7s%-5s%-5s%-3s%-4s%-4s%-3s\n","Name","Type","Addr","Off","Size","Flag","Lk","Inf","Ali","Es");

    for(int i = 0;i < shnum; ++i){
        section_header = (elf64_shdr*)(data + i * sizeof(*section_header) + s_off);
        printf("[%2d] %-19s", i,name + section_header->sh_name );
        switch(section_header->sh_type){
            case SHT_NULL:
                printf("%-9s","NULL");
                break;
            case SHT_PROGBITS:
                printf("%-9s","PROGBITS");
                break;
            case SHT_SYMTAB:
                printf("%-9s","SYMTAB");
                break;
            case SHT_STRTAB:
                printf("%-9s","STRTAB");
                break;
            case SHT_RELA:
                printf("%-9s","RELA");
                break;
            case SHT_HASH:
                printf("%-9s","HASH");
                break;
            case SHT_DYNAMIC:
                printf("%-9s","DYNAMIC");
                break;
            case SHT_NOTE:
                printf("%-9s","NOTE");
                break;
            case SHT_NOBITS:
                printf("%-9s","NOBITS");
                break;
            case SHT_REL:
                printf("%-9s","REL");
                break;
            case SHT_SHLIB:
                printf("%-9s","SHLIB");
                break;
            case SHT_DYNSYM:
                printf("%-9s","DYNSYM");
                break;
            case SHT_INIT_ARRAY:
                printf("%-9s","INIT_ARRAY");
                break;
            case SHT_FINI_ARRAY:
                printf("%-9s","FINI_ARRAY");
                break;
            case SHT_PREINIT_ARRAY:
                printf("%-9s","PREINIT_ARRAY");
                break;
            case SHT_GROUP:
                printf("%-9s", "GROUP");
                break;
            case SHT_SYMTAB_SHNDX:
                printf("%-9s", "SYMTAB_SHNDX");
                break;
            case SHT_NUM:
                printf("%-9s", "NUM");
                break;
            case SHT_LOOS:
                printf("%-9s", "LOOS");
                break;
            case SHT_GNU_HASH:
                printf("%-9s", "GNU_HASH");
                break;
            case SHT_GNU_LIBLIST:
                printf("%-9s", "GNU_LIBLIST");
                break;
            case SHT_CHECKSUM:
                printf("%-9s", "CHECKSUM");
                break;
            case SHT_LOSUNW:
                printf("%-9s", "LOSUNW");
                break;
            case SHT_SUNW_COMDAT:
                printf("%-9s", "SUNW_COMDAT");
                break;
            case SHT_SUNW_syminfo:
                printf("%-9s", "SUNW_syminfo");
                break;
            case SHT_GNU_verdef:
                printf("%-9s", "GNU_verdef");
                break;
            case SHT_GNU_verneed:
                printf("%-9s", "GNU_verneed");
                break;
            case SHT_GNU_versym:
                printf("%-9s", "GNU_versym");
                break;
            case SHT_LOPROC:
                printf("%-9s","LOPROC");
                break;
            case SHT_HIPROC:
                printf("%-9s","HIPROC");
                break;
            case SHT_LOUSER:
                printf("%-9s","LOUSER");
                break;
            case SHT_HIUSER:
                printf("%-9s","HIUSER");
                break;
            default:
                printf("%-9s","Unknown");
                break;
        }
        printf("%08llx %06llx ",section_header->sh_addr ,section_header->sh_offset);
        printf("%06llx ", section_header->sh_size);
        switch(section_header->sh_flags){
            case SHF_WRITE:
                printf("%-4s"," W ");
                break;
            case SHF_ALLOC:
                printf("%-4s"," A ");
                break;
            case SHF_EXECINSTR:
                printf("%-4s"," E ");
                break;
            case SHF_MASKPROC:
                printf("%-4s"," M ");
                break;
            case SHF_WRITE | SHF_ALLOC:
                printf("%-4s"," WA " );
                break;
            case SHF_WRITE | SHF_EXECINSTR:
                printf("%-4s"," WE");
                break;
            case SHF_ALLOC | SHF_EXECINSTR:
                printf("%-4s"," AE");
                break;
            case SHF_WRITE | SHF_ALLOC | SHF_EXECINSTR:
                printf("%-4s"," WAE");
                break;
            default:
                printf("%-4s"," U ");
                break;
        }
        printf("%2d ", section_header->sh_link);
        printf("%3xd", section_header->sh_info);
        printf("%3llx ", section_header->sh_addralign);
        printf("%2llx\n", section_header->sh_entsize);
    }
}

void show_program_header(const unsigned char *data)
{
    elf64_ehdr *header = (elf64_ehdr *)data;
    // 不能信任header的e_phoff
    // elf64_phdr *program_header = (elf64_phdr *)(data + header->e_phoff);
    elf64_phdr *program_header = (elf64_phdr *)(data + sizeof(*header));

    int num = header->e_phnum;
    printf("Program header number: \t%d,",num);
    printf("Program header:\n");
    printf("\tType\t\tOffset\t VAddr\t    PAddr\tFSize\t MemSize   Flag  Align\n");

    for (int i=0;i < num; ++i){
        printf("\t");
        switch(program_header->p_type)
        {
            case PT_NULL:
                printf("Invalid\t");
                break;
            case PT_LOAD:
                printf("LOAD \t");
                break;
            case PT_DYNAMIC:
                printf("DYNAMIC\t");
                break;
            case PT_INTERP:
                printf("INTERP\t");
                break;
            case PT_NOTE:
                printf("NOTE \t");
                break;
            case PT_SHLIB:
                printf("SHLIB\t");
                break;
            case PT_PHDR:
                printf("PHDR\t");
                break;
            case PT_TLS:
                printf("TLS\t");
                break;
            case PT_NUM:
                printf("NUM\t");
                break;
            case PT_LOOS:
                printf("LOOS\t");
                break;
            case PT_L4_STACK:
                printf("L4_STACK  ");
                break;
            case PT_L4_KIP:
                printf("L4_KIP  ");
                break;
            case PT_L4_AUX:
                printf("L4_AUX  ");
                break;
            case PT_GNU_EH_FRAME:
                printf("GNU_EH_FRAME  ");
                break;
            case PT_GNU_STACK:
                printf("GNU_STACK  ");
                break;
            case PT_GNU_RELRO:
                printf("GNU_RELRO  ");
                break;
            case PT_GNU_PROPERTY:
                printf("GNU_PROPERTY  ");
                break;
            case PT_SUNWBSS:
                printf("SUNWBSS  ");
                break;
            case PT_SUNWSTACK:
                printf("SUNWSTACK  ");
                break;
            case PT_HIOS:
                printf("HIOS\t");
                break;
            case PT_LOPROC:
                printf("LOPROC\t");
                break;
            case PT_HIPROC:
                printf("HIPROC\t");
                break;
            default:
                printf("Unknown  ");
                break;
        }

        char flag[4] ={' ',' ',' ',' '};
        switch(program_header->p_flags)
        {
            case 0x01:
                flag[2]='X';
                break;
            case 0x02:
                flag[1]='W';
                break;
            case 0x03:
                flag[1]='W';
                flag[2]='X';
                break;
            case 0x04:
                flag[0]='R';
                break;
            case 0x05:
                flag[0]='R';
                flag[2]='X';
                break;
            case 0x06:
                flag[0]='R';
                flag[1]='W';
                break;
            case 0x07:
                flag[0]='R';
                flag[1]='W';
                flag[2]='X';
                break;
        }
        flag[3]='\0';
        printf("\t%08llx\t%08llx\t%08llx\t%08llx\t%08llx\t%08x\t%s\t%08llx\n",program_header->p_offset,program_header->p_vaddr,program_header->p_paddr,program_header->p_filesz,program_header->p_memsz,program_header->p_flags,flag,program_header->p_align);
        printf("  %s",flag);
        printf("  0x%llx\n",program_header->p_align);
        if(program_header->p_type == PT_INTERP){
            printf("\t \t[Requesting program interpreter: /lib/ld-linux.so.2]\n");
        }
        program_header = (elf64_phdr*)(data + sizeof(*header)+ (i+1) * sizeof(*program_header));
        for(int j = 0; j < 4;++j)
        {
            flag[j]=' ';
        }
    }
}

void show_header(const unsigned char *data){
    elf64_ehdr *header = (elf64_ehdr *)data;
    printf("ELF Header:\n");

    // magic number
    printf("\tMagic:\t\t\t\t");
    for(int i = 0; i < EI_NIDENT; i++){
        printf("%02x ", header->e_ident[i]);
    }
    printf("\n");

    if (memcmp(header->e_ident, ELFMAG, SELFMAG) != 0) {
        printf("ELF magic values do not match.\n");
    }
    if (header->e_ident[EI_MAG0] != ELFMAG0) {
        printf("Value at EI_MAG0 does not match: 0x%x\n", header->e_ident[EI_MAG0]);
    }
    if (header->e_ident[EI_MAG1] != ELFMAG1) {
        printf("Value at EI_MAG1 does not match: 0x%x\n", header->e_ident[EI_MAG1]);
    }
    if (header->e_ident[EI_MAG2] != ELFMAG2) {
        printf("Value at EI_MAG2 does not match: 0x%x\n", header->e_ident[EI_MAG2]);
    }
    if (header->e_ident[EI_MAG3] != ELFMAG3) {
        printf("Value at EI_MAG3 does not match: 0x%x\n", header->e_ident[EI_MAG3]);
    }

    // 输出magic number
    printf("\tMagic number:\t\t\t");
    for(int i = 0; i < SELFMAG; i++){
        printf("%02x ", header->e_ident[i]);
    }
    printf("\n");

    // class (64/32)
    printf("\tClass:\t\t\t\t");
    switch(header->e_ident[EI_CLASS]){
        case ELFCLASSNONE:
            printf("Unknown file type\n");
            break;
        case ELFCLASS32:
            printf("elf64\n");
            break;
        case ELFCLASS64:
            printf("ELF64\n");
            break;
        default:
            printf("Invalid class\n");
            break;
    }

    // data (little/big endian)
    printf("\tData:\t\t\t\t");
    switch(header->e_ident[EI_DATA]){
        case ELFDATANONE:
            printf("Unknown data format\n");
            break;
        case ELFDATA2LSB:
            printf("2's complement, little endian\n");
            break;
        case ELFDATA2MSB:
            printf("2's complement, big endian\n");
            break;
        default:
            printf("Invalid data format\n");
            break;
    }

    // version
    printf("\tVersion:\t\t\t");
    switch(header->e_version){
        case EV_NONE:
            printf("Invalid version\n");
            break;
        case EV_CURRENT:
            printf("1 (current)\n");
            break;
        default:
            printf("Invalid version\n");
            break;
    }

    // OS/ABI
    printf("\tOS/ABI:\t\t\t\t");
    switch(header->e_ident[EI_OSABI]){
        case ELFOSABI_NONE:
            printf("UNIX System V ABI\n");
            break;
        case ELFOSABI_HPUX:
            printf("HP-UX\n");
            break;
        case ELFOSABI_NETBSD:
            printf("NetBSD\n");
            break;
        case ELFOSABI_LINUX:
            printf("Linux\n");
            break;
        default:
            printf("Invalid OS/ABI\n");
            break;
    }

    // type
    printf("\tType:\t\t\t\t");
    switch (header->e_type)
    {
        case ET_NONE:
            printf("No file type\n");
            break;
        case ET_REL:
            printf("Relocatable file\n");
            break;
        case ET_EXEC:
            printf("Executable file\n");
            break;
        case ET_DYN:
            printf("Shared object file\n");
            break;
        case ET_CORE:
            printf("Core file\n");
            break;
        default:
            printf("Invalid file type\n");
            break;
    }

    // machine
    printf("\tMachine:\t\t\t");
    switch(header->e_machine){
        case EM_NONE:
            printf("No machine\n");
            break;
        case EM_M32:
            printf("AT&T WE 32100\n");
            break;
        case EM_SPARC:
            printf("SPARC\n");
            break;
        case EM_386:
            printf("Intel 80386\n");
            break;
        case EM_68K:
            printf("Motorola 68000\n");
            break;
        case EM_88K:
            printf("Motorola 88000\n");
            break;
        case EM_860:
            printf("Intel 80860\n");
            break;
        case EM_MIPS:
            printf("MIPS RS3000\n");
            break;
            // 额外加的，系统自带的elf文件里没有这个型号
        case 0x3e:
            printf("Advanced Micro Devices X86-64\n");
            break;
        default:
            printf("Invalid machine\n");
            break;
    }

    // version
    printf("\tVersion:\t\t\t");
    switch(header->e_version){
        case EV_NONE:
            printf("Invalid version\n");
            break;
        case EV_CURRENT:
            printf("1 (current)\n");
            break;
        default:
            printf("Invalid version\n");
            break;

    }

    // 系统转交控制的虚拟地址，也就是进程开始的地址。如果程序没有入口点，那么这个参数的字段就是 0。
    printf("\tEntry:\t\t\t\t0x%llx\n", header->e_entry);
    // 程序头表位置的偏移量。如果没有程序头表，那么这个字段值为 0。
    printf("\tProgram Header:\t\t\t%lld (bytes into file)\n", header->e_phoff);
    // 节头表位置的偏移量。如果没有节头表，那么这个字段值为 0。
    printf("\tsection Header:\t\t\t%lld (bytes into file)\n", header->e_shoff);
    // 文件有关的处理器特定的标识。
    printf("\tFlag:\t\t\t\t0x%x\n", header->e_flags);
    // ELF 头的长度（以字节为单位）
    printf("\tHeader size:\t\t\t%d (bytes)\n", header->e_ehsize);
    // 文件的程序头表中一个条目的长度，以字节为单位。表中的每个条目的长度都是相等的。
    printf("\tProgram header size:\t\t%d (bytes)\n", header->e_phentsize);
    // 标识了程序头表中的条目数。如果没有程序头表，那么这个字段值为 0。通过和 e_phentsize 字段相乘，可以计算出程序头表的大小。
    printf("\tProgram header number:\t\t%d\n", header->e_phnum);
    // 文件的节头表中一个条目的长度
    printf("\tSection header size:\t\t%d\n", header->e_shentsize);
    // 节头表中的条目数
    printf("\tSection header number:\t\t%d\n", header->e_shnum);
    // 未知
    printf("\tString index number:\t\t%d\n", header->e_shstrndx);
}

void show_usage()
{
    printf("Usage: ./%s [options] <elf file>\n\n", software_name);

    printf("Available options:\n"
           "  -H : show the usage information\n\n"
           "  -h <elf file> : display the header information\n\n"
           "  -a <elf file> : display all the information\n\n"
           "  -S <elf file> : display the section  header information\n\n"
           "  -s <elf file>  : display symbol table\n\n"
           "  -v : Displaye the version number of readELF\n\n"
    );
}

void show_all(const unsigned char *data)
{
    // show_header(data);
    // printf("\n\n");
    // show_program_header(data);
    // printf("\n\n");
    // show_section_header(data);
    // printf("\n\n");
    // show_symbol(data);
}

void run(char* argv[]){
    const char *arg = argv[1];
    const char *filename = argv[2];
    if(strcmp(arg, "-H") == 0){
        show_usage();
        return;
    }

    // 读取elf文件
    printf("file name %s\n",filename);
    FILE *elf = fopen(filename, "rb+") ;
    if(elf == NULL){
        printf("Open error\n");
        printf("File %s cannot be opened\n", filename);
        perror(strerror(errno));
        exit(-1);
    }
    // buf不能在这里定义
    // unsigned char buf[MAX_LEN];
    int len = fread(buf,1,MAX_LEN,elf);
    buf[len] = '\0';


    switch (arg[1])
    {
        case 'h':
            show_header(buf);
            break;
        case 'p':
            show_program_header(buf);
            break;
        case 's':
            show_section_header(buf);
            break;
        case 'S':
//			show_symbol(buf);
            show_symbol(buf);
            break;
        case 'a':
            show_all(buf);
            break;
        default:
            printf("Invalid option\n");
            break;
    }

    fclose(elf);
}

int main(int argc, char* argv[]) {
    if(argc == 1){
        show_usage();
        return 0;
    }else if (argc == 3){
        run(argv);
    }else if (argc > 3) {
        printf("Too many arguments supplied.\n");
        return -1;
    }
    return 0;
}