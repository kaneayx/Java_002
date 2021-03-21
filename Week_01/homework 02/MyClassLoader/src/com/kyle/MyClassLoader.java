package com.kyle;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

/**
 * @author kyle
 */
public class MyClassLoader extends ClassLoader {
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            byte[] bytes = readFile("Hello.xlass");
            if (bytes == null || bytes.length <= 0) {
                throw new IOException("file is empty");
            }

            // 解码
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) (255 - bytes[i]);
            }
            return defineClass(name, bytes, 0, bytes.length);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ClassNotFoundException();
        }
    }

    /**
     * 读取xlass文件内容
     */
    public byte[] readFile(String path) throws IOException {
        try (InputStream stream = new FileInputStream(path)) {
            return stream.readAllBytes();
        }
    }

    public static void main(String[] args) {
        try {
            // 通过ClassLoader加载Hello类调用hello方法
            Object obj = new MyClassLoader().findClass("Hello").getDeclaredConstructor().newInstance();
            Method method = obj.getClass().getDeclaredMethod("hello");
            method.invoke(obj);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
}
