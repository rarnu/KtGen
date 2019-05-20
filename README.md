# KtGen

官方项目向导的替代品，用于创建 Kotlin Web 项目。

当前可以建立以下项目类型:

1. 部署于 tomcat 的 ktor 项目
2. 部署于 nodejs 的 ktnode 项目
3. 纯前端的 ktjs 项目

![screenshot](https://raw.githubusercontent.com/rarnu/KtGen/master/screenshot/screenshot.png)

- - -

编译(需要 gradle 4.6 或以上):

```
$ git clone git@github.com:rarnu/KtGen.git
$ cd KtGen
$ gradle build
```

可以在 ```build/libs``` 下找到名为 ```ktgen.jar``` 的文件，以 jar 方式运行之即可:

```
$ java -jar ktgen.jar
```

