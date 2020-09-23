[#ftl]
[@b.head/]
<div class="container" style="width:95%">
<nav class="navbar navbar-default" role="navigation">
  <div class="container-fluid">
    <div class="navbar-header">
        <a class="navbar-brand" href="#"><i class="fas fa-graduation-cap"></i>跨校交流学习经历</a>
    </div>
    <ul class="nav navbar-nav navbar-right">
        <li>
        [@b.form class="navbar-form navbar-left" role="search" action="!editNew"]
            [#list students as std]
            [@b.a class="btn btn-sm btn-info" href="!editNew?project.id="+std.project.id]<i class="fas fa-plus"></i>添加到${std.project.name}[/@]
            [/#list]
        [/@]
        </li>
    </ul>
    </div>
</nav>

  [#list exchangeStudents as exchangeStudent]
  [@b.form name="removeExternForm_"+exchangeStudent.id  action="!remove?id="+exchangeStudent.id + "&project.id="+exchangeStudent.std.project.id + "&_method=delete"][/@]
  [#assign title]
     <i class="fas fa-school"></i> &nbsp;${exchangeStudent.school.name}<span style="font-size:0.8em">(${exchangeStudent.beginOn?string("yyyy-MM")}~${exchangeStudent.endOn?string("yyyy-MM")})</span>
     [#if exchangeStudent.grades?size>0 && exchangeStudent.auditState =="通过"]审核通过
     [#else]
     <div class="btn-group">
     [@b.a href="!edit?id="+exchangeStudent.id class="btn btn-sm btn-info"]<i class="far fa-edit"></i>修改[/@]
     [@b.a href="!editApplies?id="+exchangeStudent.id class="btn btn-sm btn-info"]<i class="far fa-edit"></i>匹配冲抵[/@]
     </div>
       [@b.a href="!remove?id="+exchangeStudent.id + "&project.id=" + exchangeStudent.std.project.id  onclick="return removeExtern(${exchangeStudent.id});" class="btn btn-sm btn-warning"]<i class="fas fa-times"></i>删除[/@]
     [/#if]
  [/#assign]
  [@b.card class="card-info card-outline"]
     [@b.card_header]
      ${title}
     [/@]
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
