<html>
<head>
    <meta charset="utf-8">
    <title>Freemarker入门小Demo</title>
</head>
<body>

<#-- 1. 我只是一个注释，我不会有任何输出 -->
${name}，你好。${message}

<hr>
<#-- 2. assign定义变量 -->
<#-- 1)定义简单类型 -->
<#assign linkName="充钱使你变强!" />
${linkName}

<hr>
<#-- 2)定义对象类型 -->
<#assign teleAndAddress={'tele':'12345678912', 'address':'北极坐标255'} />
电话:${teleAndAddress.tele} <br>
地点:${teleAndAddress.address}

<#-- 3. include模板嵌套 -->
<#include "head.ftl">

<#-- 4. if/else -->
<#if success==true>
    你已进入极寒之地, 注意警戒!
    <#else>
    你未进入极寒之地, 小心黑洞!
</#if>

<hr>
<#-- 5. list循环 -->
<#list goodsList as goods>
    ${goods_index + 1} 商品名称:${goods.name} 价格:${goods.price}<br>
</#list>
<#-- 6. 内建函数 -->
<hr>
共${goodsList?size}条数据

<hr>
<#-- 7. 转换JSON字符串为对象 -->
<#assign text="{'bank':'工商银行','account':'18901920201920212'}" />
<#assign data=text?eval />
开户行:${data.bank} <br>
开户账号:${data.account}

<hr>
<#-- 8. 日期格式化 -->
当前日期:${today?date}<br>
当前时间:${today?time}<br>
当前日期+时间:${today?datetime}<br>
日期格式化:${today?string("yyyy年MM月dd日 HH:mm:ss")}

<hr>
<#-- 9. 数字转换为字符串 -->
累计金币:${money}<br>
累计金币:${money?c}

<hr>
<#-- 10. 空值 -->
<#if aaa??>
    aaa存在!
    <#else>
    aaa不存在
</#if>
<br>
<#-- 缺失变量默认值:“!”:
        当aaa为null时输出'-' -->
aaa:${aaa!'-'}

<hr>
<#-- 11. 逻辑运算符 -->
<#--
逻辑运算符有如下几个:
逻辑与:&&
逻辑或:||
逻辑非:!
逻辑运算符只能作用于布尔值,否则将产生错误
-->
<#-- 12. 比较运算符 -->
<#--
表达式中支持的比较运算符有如下几个:
1  =或者==:判断两个值是否相等.
2  !=:判断两个值是否不等.
3  >或者gt:判断左边值是否大于右边值
4  >=或者gte:判断左边值是否大于等于右边值
5  <或者lt:判断左边值是否小于右边值
6  <=或者lte:判断左边值是否小于等于右边值
注意:  =和!=可以用于字符串，数值，日期来比较是否相等，但=和!=两边必须是相同类型的值，否则会产生错误。而且FreeMarker是精确比较,"x"，"x "，"X"是不等的。
其它的比较运算符可以作用于数字和日期，但不能作用于字符串。
大部分的时候,使用gt等字母运算符代替>会有更好的效果。因为 FreeMarker会把>解释成FTL标签的结束字符。当然，也可以使用括号来避免这种情况，如:<#if (x>y)>
-->
<#if (3 > 2)>
    3 > 2
<#else>
    2 > 3
</#if>
<br>
<#if 3 gt 2>
    3 gt 2
    <#else>
    3 lt 2
</#if>


</body>
</html>