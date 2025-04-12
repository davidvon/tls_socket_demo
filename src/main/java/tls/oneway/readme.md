# 单向模式
![img.png](img.png)

##1. 生成服务端 keystore（密码设为 zte123）
keytool -genkeypair -alias serverkey -keyalg RSA -keysize 2048 -keystore server-keystore.jks -dname "CN=localhost" -storepass zte123 -validity 365

##2. 导出服务端公钥证书
keytool -exportcert -alias serverkey -keystore server-keystore.jks -file server-cert.cer -storepass zte123

##3. 客户端导入证书到 truststore（信任服务端）
keytool -importcert -keystore client-truststore.jks -alias servercert -file server-cert.cer -storepass zte123 -noprompt
