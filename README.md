# KtGen

官方项目向导的替代品，用于创建 Kotlin Web 项目。

当前可以建立以下项目类型:

1. 部署于 tomcat 的 ktor 项目
2. 部署于 nodejs 的 ktnode 项目
3. 纯前端的 ktjs 项目
4. Ktor + KtReact 整合前后端的项目

![screenshot](https://raw.githubusercontent.com/rarnu/KtGen/master/screenshot/screenshot.png)

- - -

编译(需要 gradle 5.4 或以上):

```shell
$ git clone git@github.com:rarnu/KtGen.git
$ cd KtGen
$ ./build.sh
```

可以在 ```release``` 目录下编译结果，编译后包含 ```ktgen.jar``` 和 ```ktgen```。

```ktgen.jar``` 是可视化程序，使用以下方法启动之:

```shell
$ java -jar ktgen.jar
```

```ktgen``` 是命令行程序，使用以下命令来创建项目:

```shell
$ ktgen ktor --package <package> --name <name> --output <output>
$ ktgen ktnode --package <package> --name <name> --output <output>
$ ktgen kjs --name <name> --output <output>
$ ktgen react --package <package> --name <name> --output <output>
```

