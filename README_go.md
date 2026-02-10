# Android 自动化脚本 (Go 版本)

## 文件说明

- `adb_utils.go` - 通用 ADB 工具函数（可被其他脚本复用）
- `rebuild_and_install.go` - 重新构建和安装应用的主程序
- `build.bat` - 编译脚本

## 使用方法

### 方法 1：直接运行（需要 Go 环境）

```bash
go run .
```

或

```bash
go run rebuild_and_install.go adb_utils.go
```

### 方法 2：编译成 exe（推荐，双击运行）

1. 运行 `build.bat` 或执行：
   ```bash
   go build -o rebuild_and_install.exe rebuild_and_install.go adb_utils.go
   ```

2. 双击 `rebuild_and_install.exe` 运行

## 功能特性

✅ **设备优先级自动选择**
- 自动连接网络设备
- 按优先级顺序选择设备：
  1. `2B16166D4AA2EB`
  2. `24191FDF600130`
  3. `127.0.0.1:10114`
- 如果都不在线，使用默认设备

✅ **完整的构建流程**
1. 停止目标应用
2. 卸载旧版本
3. 构建 APK
4. 安装新版本

## 通用函数复用

`adb_utils.go` 中的函数可以在其他 Go 脚本中直接使用（只要在同一文件夹且都是 `package main`）：

```go
// 在其他 .go 文件中直接调用
deviceID, adbCmd := SelectDevice()
RunADB(adbCmd, "shell", "ls", "/sdcard")
```

## 修改设备优先级

编辑 `adb_utils.go` 文件的第 10-14 行：

```go
var DevicePriorities = []string{
	"你的设备ID1",
	"你的设备ID2",
	"127.0.0.1:端口",
}
```

## 优势

相比 Batch 脚本：
- ✅ 可以复用函数（同文件夹下的 .go 文件可以互相调用）
- ✅ 更好的错误处理
- ✅ 跨平台（Windows/Linux/Mac）
- ✅ 不需要 go.mod 文件也能工作
- ✅ 编译成单个 exe，无依赖
