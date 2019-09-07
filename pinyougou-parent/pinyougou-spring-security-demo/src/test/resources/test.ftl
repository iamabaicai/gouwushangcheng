<html>
<head>
<!--    这是一个模板-->
    <meta charset="utf-8">
    <title>freemarker入门小demo</title>
</head>
<body>
<#assign linkman="我">
<#assign info={"telephone":"112200223","address":"陕西省西安市草滩六路"}>
<#-- freemarker的注释不会有任何输出，不同于普通注释会输出只是不糊解析 -->
<#-- ${插值}会被数据模型中的变量对象代替 -->
${name},你好${message}
联系人：${linkman}
电话：${info.telephone}
地址：${info.address}
</body>
</html>