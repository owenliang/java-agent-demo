package cc.yuerblog.demoagent;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class Transformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        ClassPool cp = ClassPool.getDefault();

        try {
            String realClassName = className.replace("/", ".");
            CtClass cls = cp.get(realClassName);
            //System.out.println("加载到类" + cls.getName());

            // 搜索这个类有没有实现我们想要的接口
            CtClass[] interfaces = cls.getInterfaces();
            for (CtClass iface: interfaces) {
                // System.out.println("实现接口" + iface.getName() );
                if (iface.getName().equals("cc.yuerblog.helloworld.SomeInterface")) {
                    CtMethod method = cls.getDeclaredMethod("echo");
                    // 改掉某行代码
                    method.instrument(new ExprEditor() {
                        public void edit(MethodCall m) throws CannotCompileException {
                            System.out.println(m.getClassName() + "," + m.getMethodName());
                            if (m.getClassName().equals("java.io.PrintStream") && m.getMethodName().equals("println")) {
                                m.replace("System.out.println(\"你的内容被我捕获了:\" + $1);");
                                System.out.println("篡改代码");
                            }
                        }
                    });
                    // 插入到方法前
                    method.insertBefore("System.out.println(\"before echo\");");
                    // 插入到方法后
                    method.insertAfter("System.out.println(\"after echo\");");
                    System.out.println("字节码注入" + cls.getName());
                    return cls.toBytecode();
                }
            }
        } catch (NotFoundException e) {
            System.out.println("找不到类:" + e.getMessage());
        } catch (CannotCompileException e) {
            System.out.println("编译失败:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO错误:" + e.getMessage());
        }
        return classfileBuffer;
    }
}
