zjm-starter-elasticsearch

# 工程信息：
    工程旨在制作一个starter 启动器，后来就变了。直接做 es 学习 demo 工程了。

## 工程技术
    spring boot 2.2.4.RELEASE
    spring web 
    google-guava 工具包
    logback 日志输出
    swagger api 
    logback -> logstash -> es  日志输出 logstash，到 es
    elasticsearch JAVA API 使用
    
## es 引入，client 使用


## swagger、swagger ui 引入
    
    swagger ui 地址：http://localhost:8090/swagger-ui.html


## 集成 logback 和 logstash ，日志记录入 es ，通过 kibana 查看
### logback 、logback-spring 区别：
logback和logback-spring.xml都可以用来配置logback，但是 2 者的加载顺序是不一样的。
加载顺序：logback.xml - application.properties - logback-spring.xml。
logback.xml加载早于application.properties，所以如果你在logback.xml使用了变量时，
而恰好这个变量是写在application.properties时，那么就会获取不到，只要改成logback-spring.xml就可以解决。

### es logstash kibana 版本一定要一致。
### es 启动：
cd /Users/zhangjiamei/Documents/github-site/github-zjmlove/Elastic/elasticsearch-7.5.1
./bin/elasticsearch

### es-head 启动
下载后，不用修改配置。

cd /Users/zhangjiamei/Documents/github-site/github-zjmlove/Elastic/elasticsearch-head
node_modules/grunt/bin/grunt server

### logstash 启动
创建配置文件 config -> logstash-es.conf
```
input {
    tcp {
    	host => "127.0.0.1" # 这个一定要是logstash本机ip，不然logstash 无法启动，也可以去除
        port => 10514  # 暴露出端口4567接受日志  
        codec => "json"
    }
}
output {
    elasticsearch {
        action => "index"
        hosts => ["localhost:9200"]  # es 服务的地址，可以配置多个
        index => "zjm-starter-elasticsearch"
    }
}
```

cd /Users/zhangjiamei/Documents/github-site/github-zjmlove/Elastic/logstash-7.5.1
./logstash -f /Users/zhangjiamei/Documents/github-site/github-zjmlove/Elastic/logstash-7.5.1/config/logstash-es.conf\

### kibana 启动
配置文件 config -> kibana.yml
```
server.host: "0.0.0.0" 
elasticsearch.hosts: ["http://localhost:9200"] 
```

cd /Users/zhangjiamei/Documents/github-site/github-zjmlove/Elastic/kibana-7.5.1-darwin-x86_64
./bin/kibana

### logback 配置， 并配置引入 logstash
详见代码：logback-spring.xml

