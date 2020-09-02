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
        <a class="navbar-brand" href="#">校外学习经历</a>
    </div>
    <ul class="nav navbar-nav navbar-right">
        <li>
        [@b.form class="navbar-form navbar-left" role="search" action="!editNew"]
            [@b.a class="btn btn-sm btn-info" href="!editNew"]<i class="fas fa-plus"></i>新建[/@]
        [/@]
        </li>
    </ul>
    </div>
</nav>

  [#list exchangeStudents as exchangeStudent]
  [#assign title]
     ${exchangeStudent.school.name}<span style="font-size:0.8em">(${exchangeStudent.beginOn?string("yyyy-MM")}~${exchangeStudent.endOn?string("yyyy-MM")})</span>
     [#if exchangeStudent.grades?size>0 && exchangeStudent.auditState =="通过"]审核通过
     [#else]
     <div class="btn-group">
     [@b.a href="!edit?id="+exchangeStudent.id class="btn btn-sm btn-info"]<i class="far fa-edit"></i>修改[/@]
     [@b.a href="!editApplies?id="+exchangeStudent.id class="btn btn-sm btn-info"]<i class="far fa-edit"></i>匹配冲抵[/@]
     </div>
     [@b.a href="!edit?id="+exchangeStudent.id class="btn btn-sm btn-warning"]<i class="fas fa-times"></i>删除[/@]
     [/#if]
  [/#assign]
  [@panel title=title]
    [#include "extern_student.ftl"/]
  [/@]
  [/#list]
</div>
[@b.foot/]