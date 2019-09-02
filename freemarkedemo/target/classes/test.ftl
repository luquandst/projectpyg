<html>
<head>
    <meta charset="utf-8">
    <title>Freemarker 入门小 DEMO </title>

</head>
<body>
<#--我只是一个注释，我不会有任何输出 -->
${name},你好。${message}<hr>
<#--定义变量-->
<#assign linkman="黎梦">
${linkman}><hr>
<#--定义对象类型-->
<#assign info={"name":"张三","addr":"深圳"}>
姓名：${info.name},
地址:${info.addr}
<#--引用外部的模板-->
<#include 'head.ftl'>

</body>
</html>