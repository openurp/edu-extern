[#ftl]
[@b.head/]
[#macro panel title]
<div class="panel panel-default">
  <div class="panel-heading">
    <h3 class="panel-title">${title}</h3>
  </div>
  [#nested/]
</div>
[/#macro]

<div class="container" style="width:95%">

<nav class="navbar navbar-default" role="navigation">
  <div class="container-fluid">
    <div class="navbar-header">
        <a class="navbar-brand" href="#"><span class="glyphicon glyphicon-book"></span>跨校交流学习经历</a>
    </div>
    <ul class="nav navbar-nav navbar-right">
        <li>
        [@b.form class="navbar-form navbar-left" role="search" action="!editNew"]
            [@b.a class="btn btn-sm btn-info" href="!editNew"]<span class='glyphicon glyphicon-plus'></span>添加[/@]
        [/@]
        </li>
    </ul>
    </div>
</nav>

  [#list exchangeStudents as exchangeStudent]
  [@b.form name="removeExternForm_"+exchangeStudent.id  action="!remove?id="+exchangeStudent.id+"&_method=delete"][/@]
  [#assign title]
     <span class="glyphicon glyphicon-bookmark"></span>${exchangeStudent.school.name}<span style="font-size:0.8em">(${exchangeStudent.beginOn?string("yyyy-MM")}~${exchangeStudent.endOn?string("yyyy-MM")})</span>
     [#if exchangeStudent.grades?size>0 && exchangeStudent.state =="通过"]审核通过
     [#else]
     <div class="btn-group">
     [@b.a href="!edit?id="+exchangeStudent.id class="btn btn-sm btn-info"]<span class="glyphicon glyphicon-edit"></span>修改[/@]
     [@b.a href="!editApplies?id="+exchangeStudent.id class="btn btn-sm btn-info"]<span class="glyphicon glyphicon-edit"></span>匹配冲抵[/@]
     </div>
       [@b.a href="!remove?id="+exchangeStudent.id onclick="return removeExtern(${exchangeStudent.id});" class="btn btn-sm btn-warning"]<span class="glyphicon glyphicon-remove"></span>删除[/@]
     [/#if]
  [/#assign]
  [@panel title=title]
    [#include "extern_student.ftl"/]
  [/@]
  [/#list]
</div>
<script>
   function removeExtern(id){
       if(confirm("确定删除?")){
         return bg.form.submit(document.getElementById("removeExternForm_"+id));
       }else{
         return false;
       }
   }
</script>
[@b.foot/]