# guerlab-sms

基于Spring boot的短信服务支持，通过引用不同的Starter启用不同的短信通道支持，支持多通道下的负载均衡，支持同步/异步方式发送。
目前支持类型：阿里云短信、百度云短信、华为云短信、京东云短信、网易云信短信、腾讯云短信、七牛云短信、云片网短信、又拍云短信、移动云模板短信

[![gitee](https://gitee.com/guerlab_net/guerlab-sms/badge/star.svg)](https://gitee.com/guerlab_net/guerlab-sms)
![](https://img.shields.io/maven-central/v/net.guerlab.sms/guerlab-sms-server-starter.svg)
[![Build Status](https://travis-ci.org/guerlab-net/guerlab-sms.svg?branch=master)](https://travis-ci.org/guerlab-net/guerlab-sms)
![](https://img.shields.io/badge/LICENSE-LGPL--3.0-brightgreen.svg)

## maven配置

```
<dependency>
	<groupId>net.guerlab.sms</groupId>
	<artifactId>guerlab-sms-server-starter</artifactId>
</dependency>
```

## 子项目列表

| 子项目                                     | 说明                   |
|:----------------------------------------|----------------------|
| guerlab-sms-core                        | 核心包                  |
| guerlab-sms-server                      | 短信服务实现               |
| guerlab-sms-server-starter              | 短信服务启动包              |
| guerlab-sms-redis-repository-starter    | 基于redis的repository实现 |
| guerlab-sms-server-webmvc-starter       | web端点实现              |
| guerlab-sms-template-support            | 模板支持                 |
| guerlab-sms-template-freemarker-support | freemarker模板引擎支持     |

## 支持通道

- [X]  阿里云 guerlab-sms-aliyun-starter
- [X]  百度云 guerlab-sms-baiducloud-starter
- [X]  移动云 guerlab-sms-chinamobile-starter
- [X]  华为云 guerlab-sms-huaweicloud-starter
- [X]  京东云 guerlab-sms-jdcloud-starter
- [X]  极光短信 guerlab-sms-jpush-starter
- [X]  网易云信 guerlab-sms-netease-starter
- [X]  腾讯云 guerlab-sms-qcloud-starter
- [X]  腾讯云V3 guerlab-sms-qcloud-v3-starter
- [X]  七牛云 guerlab-sms-qiniu-starter
- [X]  又拍云 guerlab-sms-upyun-starter
- [X]  云片网 guerlab-sms-yunpian-starter

## wiki

- [Gitee](https://gitee.com/guerlab_net/guerlab-sms/wikis/pages)

## changelog

- [Gitee](https://gitee.com/guerlab_net/guerlab-sms/wikis/pages)

## demo

- [Gitee](https://gitee.com/guerlab_net/guerlab-sms-demo)
