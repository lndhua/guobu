## **安卓电动车国补图片上传应用操作文档**

### **1. 系统要求**

- 开发环境：
  - Android Studio 4.0 或更高版本
  - JDK 1.8 或更高版本
  - Gradle 7.0 或更高版本
- 运行环境：
  - Android 6.0（API 23）或更高版本
  - 支持相机和相册访问的安卓设备

------

### **2. 功能概述**

该应用允许用户输入姓名后，上传以下类型的图片：

1. 发票图片
2. 新车车牌号图片
3. 新车合格证图片
4. 新车车架号图片
5. 新车电池编号图片
6. 旧车权属证明图片
7. 旧车电池照片
8. 旧车整车图片
9. 旧车车牌号图片
10. 旧车车架号图片
11. 签购单小票图片
12. 身份证图片

**核心功能**：

- 按姓名分类存储图片到手机本地文件系统。
- 支持拍照或从相册选择图片。
- 图片可后续修改或补充。

------

### **3. 开发与部署流程**

#### **3.1 代码结构**

- **`MainActivity.java`**：主界面逻辑（输入姓名、图片选择）。
- **`ApiService.java`**：定义图片上传的 API 接口（可选，若需联网）。
- **`FileUtils.java`**：文件存储与管理工具类。
- **`AndroidManifest.xml`**：权限声明和配置。

#### **3.2 依赖库**

在 `build.gradle` 中添加以下依赖：

```
dependencies {
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'pub.devrel:easypermissions:3.0.0'
}
```

#### **3.3 权限声明**

在 `AndroidManifest.xml` 中添加权限：

```
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.INTERNET"/>
```

#### **3.4 文件存储路径**

- **默认存储目录**：
  `Environment.getExternalStorageDirectory().getPath() + "/ImageUploader/[姓名]"`
- **文件命名规则**：
  `类型_时间戳.jpg`（例如：`invoice_20250801_2059.jpg`）

------

### **4. 使用说明**

#### **4.1 启动应用**

1. 打开应用后，首先进入主界面。
2. 在 `EditText` 中输入姓名（例如：张三）。

#### **4.2 上传图片**

1. 点击对应的按钮（如“上传发票图片”）。
2. 弹出菜单选择 **拍照** 或 **相册选择**。
3. 选择图片后，自动保存到对应目录，文件名按规则生成。

#### **4.3 查看或修改图片**

1. 打开手机文件管理器，进入以下路径：
   `存储卡/Android/data/[包名]/files/ImageUploader/[姓名]`
2. 可删除、重命名或补充新图片。

------

### **5. 技术实现细节**

#### **5.1 图片选择逻辑**

- 调用系统相册：

  ```
  Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
  startActivityForResult(intent, PICK_IMAGE_REQUEST);
  ```

- 调用相机拍照：

  ```
  Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
  startActivityForResult(intent, TAKE_PICTURE_REQUEST);
  ```

#### **5.2 文件存储逻辑**

- 保存图片到本地：

  ```
  String dirPath = Environment.getExternalStorageDirectory().getPath() + "/ImageUploader/" + name;
  File dir = new File(dirPath);
  if (!dir.exists()) dir.mkdirs();
  String fileName = type + "_" + System.currentTimeMillis() + ".jpg";
  File file = new File(dir, fileName);
  // 将 Uri 转换为 Bitmap 并保存
  ```

#### **5.3 动态权限处理**

- 运行时权限请求：

  ```
  if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
      pickImage();
  } else {
      EasyPermissions.requestPermissions(this, "需要存储权限", RC_WRITE_EXTERNAL_STORAGE_PERM, Manifest.permission.READ_EXTERNAL_STORAGE);
  }
  ```

------

### **6. 常见问题与解决方案**

#### **6.1 权限问题**

- **现象**：无法选择相册或拍照。
- 解决：
  1. 手动开启权限：
     - **安卓 6.0+**：进入 **设置 > 应用管理 > [应用名称] > 权限**，开启 **存储** 和 **相机**。
  2. 代码中检查权限是否被拒绝。

#### **6.2 存储路径不可见**

- **现象**：文件未保存到预期目录。
- 解决：
  1. 确认应用有写入权限。
  2. 使用文件管理器检查路径：`/Android/data/[包名]/files/ImageUploader/`

#### **6.3 图片上传失败**

- **现象**：选择图片后无响应。

- 解决：

  1. 检查 `Uri` 是否有效。

  2. 添加日志输出调试：

     ```
     Log.d("ImageUploader", "Selected Image URI: " + uri.toString());
     ```

------

### **7. 维护与更新**

#### **7.1 添加新功能**

- 新增图片类型：
  1. 修改 `activity_main.xml`，添加新按钮。
  2. 在 `MainActivity.java` 中绑定按钮事件。
  3. 更新文件存储逻辑中的 `type` 参数。

#### **7.2 升级兼容性**

- 适配高版本安卓：
  1. 替换 `Environment.getExternalStorageDirectory()` 为 `Context.getExternalFilesDir()`。
  2. 使用 `MediaStore` API 替代传统文件操作（适配安卓 10+）。

#### **7.3 清理旧数据**

- 定期清理空目录：

  ```
  File dir = new File("/path/to/directory");
  if (dir.isDirectory() && dir.list().length == 0) {
      dir.delete();
  }
  ```

------

### **8. 示例代码片段**

#### **8.1 图片选择与存储**

```
private void pickImage(String type) {
    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    startActivityForResult(intent, PICK_IMAGE_REQUEST);
}

@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
        Uri uri = data.getData();
        String name = ((EditText) findViewById(R.id.et_name)).getText().toString();
        saveImageToStorage(uri, name, "invoice"); // 替换为具体类型
    }
}

private void saveImageToStorage(Uri uri, String name, String type) {
    try {
        String dirPath = Environment.getExternalStorageDirectory().getPath() + "/ImageUploader/" + name;
        File dir = new File(dirPath);
        if (!dir.exists()) dir.mkdirs();
        String fileName = type + "_" + System.currentTimeMillis() + ".jpg";
        File file = new File(dir, fileName);
        InputStream inputStream = getContentResolver().openInputStream(uri);
        FileOutputStream outputStream = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        inputStream.close();
        outputStream.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

------

### **9. 参考文档**

1. Android 官方权限管理文档
2. Retrofit 官方文档
3. Android 文件存储最佳实践