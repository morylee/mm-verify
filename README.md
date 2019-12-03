# mm-verify

> 人机图形验证项目，支持点击和拖拽两种方式，访问[人机图形验证平台](https://verify.cloudcrowd.com.cn)申请账户使用。

## 部署

1）本地部署

根据`./src/main/resources/config/application.properties`中数据库的配置，新建数据库

然后，执行数据库脚本 `./src/main/resources/sql/update.sql`

最后将项目引入到IDEA或者Eclipse，运行`./src/main/java/org/mm/DomainApplication.java`即可

2）生产部署

首先，需要准备生产的配置文件，命名建议为application.properties，一下为详细配置，仅供参考，实际场景适度调整，其中数据库和数据库用户为错误示范，请自行调整

``` bash
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/prod_verify_db?serverTimezone=UTC&characterEncoding=UTF-8&noAccessToProcedureBodies=true
spring.datasource.username=prod_user
spring.datasource.password=123456
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.max-idle=10
spring.datasource.max-wait=10000
spring.datasource.min-idle=5
spring.datasource.initial-size=5

server.port=8888
server.session.timeout=10
server.tomcat.uri-encoding=UTF-8
server.tomcat.remote_ip_header=x-forwarded-for
server.tomcat.protocol_header=x-forwarded-proto
server.tomcat.port-header=X-Forwarded-Port
server.use-forward-headers=true

mybatis.mapperLocations=classpath:mappers/*.xml

mm.captcha.dir=D:/captcha_lib
mm.captcha.theme=theme
mm.captcha.background=background
mm.captcha.imgType=png
mm.captcha.expire=3600
mm.captcha.tokenExpire=300
mm.captcha.maxFailTimes=3
```

参数说明

| 配置参数 | 是否必填 | 缺省值 | 描述 |
| :------: | ------- | ------- | ------- |
| spring.datasource.url | 是 | - | 数据库地址 |
| spring.datasource.username | 是 | - | 数据库用户名 |
| spring.datasource.password | 是 | - | 数据库用户密码 |
| spring.datasource.driver-class-name | 是 | - | 数据库驱动类 |
| spring.datasource.max-idle | 是 | - | 数据库最大空闲时间 |
| spring.datasource.max-wait | 是 | - | 数据库最大等待时间 |
| spring.datasource.min-idle | 是 | - | 数据库最小空闲时间 |
| spring.datasource.initial-size | 是 | - | 数据库初始大小 |
| server.port | 否 | 8080 | 系统服务端口 |
| server.session.timeout | 否 | - | 系统session超时时间 |
| server.tomcat.uri-encoding | 是 | - | tomcat的编码 |
| server.tomcat.remote_ip_header | 是 | - | https配置 |
| server.tomcat.protocol_header | 是 | - | https配置 |
| server.tomcat.port-header | 是 | - | https配置 |
| server.use-forward-headers | 是 | - | https配置 |
| mybatis.mapperLocations | 是 | - | mapper位置 |
| mm.captcha.dir | 是 | - | 验证码图库位置 |
| mm.captcha.theme | 否 | theme | 验证码主题基础名称 |
| mm.captcha.background | 否 | background | 验证码图片基础名称 |
| mm.captcha.imgType | 否 | png | 验证码图片格式 |
| mm.captcha.expire | 否 | 3600s | 验证码超时时间 |
| mm.captcha.tokenExpire | 否 | 300s | 验证码token超时时间 |
| mm.captcha.maxFailTimes | 否 | 3 | 验证码最大失败次数 |

然后，需要搭建数据库，表及基础数据见脚本 `./src/main/resources/sql/update.sql`

``` bash
create database prod_verify_db;

create user prod_user IDENTIFIED by '123456';

grant select, insert, update, delete on prod_verify_db.* to prod_user@'%';

grant execute on prod_verify_db.* to prod_user@'%';

use prod_verify_db；

source `数据库脚本在操作系统的位置`
```

接下来，需要对程序进行打包，并运行

``` bash
git clone https://github.com/morylee/mm-verify.git

cd mm-verify

mvn clean package

nohup java -jar -Dfile.encoding=utf-8 target/mm-verify-0.0.1-SNAPSHOT.jar --spring.config.location=操作系统路径/application.properties > verify-log-发布日期.out &
```

最后，需要配置nginx，示例如下

nginx/conf/vhosts_conf/vhosts_verify.conf

``` bash
upstream verify.cloudcrowd.com.cn {
	server 127.0.0.1:8888 weight=1;
}

server {
	listen       80;
	server_name  127.0.0.1;
	rewrite ^(.*)$ https://$host$1 permanent;
}
server {
	listen       443;
	server_name  127.0.0.1;
	ssl          on;
	ssl_certificate 操作系统路径/server.crt;
	ssl_certificate_key 操作系统路径/server.key;
	ssl_session_timeout 5m;
	ssl_protocols TLSv1;
	ssl_ciphers  HIGH:!aNULL:!MD5;
	ssl_prefer_server_ciphers   on;
	location / {
		client_max_body_size    16m;
		client_body_buffer_size 128k;
		proxy_pass              http://verify.cloudcrowd.com.cn;
		proxy_set_header        Host $host;
		proxy_set_header        X-Real-IP $remote_addr;
		proxy_set_header        X-Forwarded-For $proxy_add_x_forwarded_for;
		proxy_set_header        X-Forwarded-Proto $scheme;
		proxy_set_header        X-Forwarded-Port $server_port;
		proxy_next_upstream     off;
		proxy_connect_timeout   30;
		proxy_read_timeout      300;
		proxy_send_timeout      300;
	}
}
```

nginx/conf/nginx.conf

``` bash
http {
	...
	
	include vhosts_conf/vhosts_verify_dev.conf;
	
	...
}
```

## 接入说明

网站接入需要申请webKey和apiKey，且配置一个白名单域名，webKey用于前端生成图形验证码，apiKey用于后端校验验证码的Token。

白名单域名配置规则，支持精确配置与模糊配置，精确配置示例：www.cloudcrowd.com.cn，模糊配置示例：*.cloudcrowd.com.cn。

1）VUE接入

npm install vue-graphic-verify --save

[使用说明](https://www.npmjs.com/package/vue-graphic-verify)

## 接口说明
| 接口名 | 数据类型 | 入参 | 出参 | 描述 |
| :------: | ------- | ------- | ------- | ------- |
| POST:/verify/param | JSON | {webKey} | {httpCode, msg, width, height} | 获取验证码基础参数 |
| POST:/verify/init | JSON | {webKey} | {httpCode, msg, success, md, k, rpk...} | 初始化验证码 |
| POST:/verify/verify | JSON | {key, clientPositions} | {httpCode, msg, success, expired, result} | 校验验证码 |
| POST:/verify/verifyToken | JSON | {apiKey, token} | {httpCode, msg, success} | 校验验证码Token |
