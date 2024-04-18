package awd.agent;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.*;

public class LogWaf {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("in agent");
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

                if(className.equals("org/apache/tomcat/util/net/NioEndpoint$NioSocketWrapper")){
                    try{
                        ClassPool pool = new ClassPool(true);
                        pool.appendClassPath(new LoaderClassPath(loader));
                        CtClass clazz = pool.getCtClass("org.apache.tomcat.util.net.NioEndpoint$NioSocketWrapper");
                        clazz.defrost();
                        System.out.println(className);

                        CtMethod method01 = CtMethod.make(("public static void logInFile(String content){\n" +
                                "String fileName = \"filepath\";\n" +
                                "java.io.FileWriter writer = new java.io.FileWriter(fileName,true);\n" +
                                "writer.write('\\n'+content+'\\n');\n" +
                                "writer.close();\n" +
                                "}").replaceAll("filepath",agentArgs), clazz);
                        clazz.addMethod(method01);

                        CtMethod method = clazz.getDeclaredMethod("read",new CtClass[]{CtClass.booleanType,pool.getCtClass("java.nio.ByteBuffer")});
//                        String code1 = """
//                        if(nRead>0){
//                            System.out.println("in log requests");
//                            java.lang.reflect.Field hbField = java.nio.ByteBuffer.class.getDeclaredField("hb");
//                            hbField.setAccessible(true);
//                            logInFile(new String((byte[])hbField.get(to),0,nRead).trim());
//                        }
//                        """;
//                        String code2 = """
//            {
//            int nRead = $0.populateReadBuffer($2);
//            if (nRead > 0) {
//                if(nRead>0){
//                     System.out.println("in log requests");
//                     java.lang.reflect.Field hbField = java.nio.ByteBuffer.class.getDeclaredField("hb");
//                     hbField.setAccessible(true);
//                     logInFile(new String((byte[])hbField.get($2),0,nRead).trim());
//                }
//                return nRead;
//            } else {
//                int limit = $0.socketBufferHandler.getReadBuffer().capacity();
//                if ($2.remaining() >= limit) {
//                    $2.limit($2.position() + limit);
//                    nRead = $0.fillReadBuffer($1, $2);
//
//                    $0.updateLastRead();
//                } else {
//                    nRead = $0.fillReadBuffer($1);
//
//                    $0.updateLastRead();
//                    if (nRead > 0) {
//                        nRead = $0.populateReadBuffer($2);
//                    }
//                }
//                if(nRead>0){
//                     System.out.println("in log requests");
//                     java.lang.reflect.Field hbField = java.nio.ByteBuffer.class.getDeclaredField("hb");
//                     hbField.setAccessible(true);
//                     logInFile(new String((byte[])hbField.get($2),0,nRead).trim());
//                }
//                return nRead;
//            }
//                        }""";

                        String code2 ="{\n" +
                                "            int nRead = $0.populateReadBuffer($2);\n" +
                                "            if (nRead > 0) {\n" +
                                "                if(nRead>0){\n" +
                                "                     System.out.println(\"in log requests\");                                                 \n" +
                                "                     java.lang.reflect.Field hbField = java.nio.ByteBuffer.class.getDeclaredField(\"hb\");\n" +
                                "                     hbField.setAccessible(true);\n" +
                                "                     logInFile(new String((byte[])hbField.get($2),0,nRead).trim());\n" +
                                "                }\n" +
                                "                return nRead;\n" +
                                "            } else {\n" +
                                "                int limit = $0.socketBufferHandler.getReadBuffer().capacity();\n" +
                                "                if ($2.remaining() >= limit) {\n" +
                                "                    $2.limit($2.position() + limit);\n" +
                                "                    nRead = $0.fillReadBuffer($1, $2);\n" +
                                "\n" +
                                "                    $0.updateLastRead();\n" +
                                "                } else {\n" +
                                "                    nRead = $0.fillReadBuffer($1);\n" +
                                "\n" +
                                "                    $0.updateLastRead();\n" +
                                "                    if (nRead > 0) {\n" +
                                "                        nRead = $0.populateReadBuffer($2);\n" +
                                "                    }\n" +
                                "                }\n" +
                                "                if(nRead>0){\n" +
                                "                     System.out.println(\"in log requests\");                                                 \n" +
                                "                     java.lang.reflect.Field hbField = java.nio.ByteBuffer.class.getDeclaredField(\"hb\");\n" +
                                "                     hbField.setAccessible(true);\n" +
                                "                     logInFile(new String((byte[])hbField.get($2),0,nRead).trim());\n" +
                                "                }\n" +
                                "                return nRead;\n" +
                                "            }\n" +
                                "                        }";
                        method.setBody(code2);

                        CtMethod method2 = clazz.getDeclaredMethod("doWrite",new CtClass[]{CtClass.booleanType,pool.getCtClass("java.nio.ByteBuffer")});

                        method2.insertAfter("System.out.println(\"in log requests2\");\n" +
                                "java.lang.reflect.Field hbField = java.nio.ByteBuffer.class.getDeclaredField(\"hb\");\n" +
                                "hbField.setAccessible(true);\n" +
//                                "logInFile(new String((byte[])hbField.get(buffer)).trim());",true);
                                "logInFile(new String((byte[])hbField.get($2)).trim());",true);

                        return clazz.toBytecode();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                return new byte[0];
            }
        });

    }



}