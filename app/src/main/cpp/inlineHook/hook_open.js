
let bytes_count = 32
let address = Module.getExportByName("libc.so","open")

let before = ptr(address)
console.log("")
console.log("[*] before hook: ")
console.log(hexdump(before, {
   offset: 0,
   length: bytes_count,
   header: true,
   ansi: true
}));

let isOutput = false

Interceptor.attach(address, {
onEnter:function(args){
       if(isOutput) return;
let after = ptr(address)
       console.log("")
console.log("[*] after hook: ")
       console.log(hexdump(after, {
           offset: 0,
           length: bytes_count,
           header: true,
           ansi: true
      }))
       isOutput = true
},
onLeave:function(retv){
}
});

// 或者frida-trace -U -i "open" -f thouger.study