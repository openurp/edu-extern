[#ftl]
[@b.head/]
<div class="container" style="width:95%">
  <nav class="navbar navbar-default" role="navigation">
    <div class="container-fluid">
      <div class="navbar-header">
        <a class="navbar-brand" href="#"><i class="fas fa-graduation-cap"></i>校外考试成绩免修申请</a>
      </div>
    </div>
  </nav>
  [@b.messages slash="4"/]
  [#list configs as config]
    <div style="background-color: #e9ecef;border-radius: .3rem;padding: 2rem 2rem;margin-bottom: 2rem;">
      <h4>${config.semester.schoolYear}学年${config.semester.name}学期校外考试成绩免修</h4>
      <pre style="border-bottom: 1px solid rgba(0,0,0,.125);white-space: pre-wrap;">${config.notice!}</pre>
      <p>
        [#list config.settings as setting]
        [@b.a class="btn btn-info" title="用"+setting.subject.name+"申请免修" role="button" href="!edit?projectId="+project.id+"&settingId="+setting.id]<i class="fas fa-plus"></i>${setting.subject.name}...[/@]
        [/#list]
      </p>
    </div>
  [/#list]

[#if applies?size>0]
  [#list applies as apply]
  [#assign settingId=0/]
  [#list configs as config]
    [#list config.settings as setting]
      [#if setting.subject=apply.subject]
        [#assign settingId=setting.id/]
        [#break/]
      [/#if]
    [/#list]
  [/#list]
  [#assign title]
     <i class="fas fa-school"></i> &nbsp;${apply.subject.name}<span style="font-size:0.8em">(${apply.acquiredOn?string("yyyy-MM")})</span>
     [#if apply.status == "通过"]审核通过[#else]
       [#if settingId>0][@b.a href="!edit?apply.id="+apply.id+"&settingId="+settingId class="btn btn-sm btn-info"]<i class="far fa-edit"></i>修改[/@][/#if]
       [@b.a href="!remove?apply.id="+apply.id+"&projectId="+student.project.id onclick="return removeApply(this);" class="btn btn-sm btn-warning"]<i class="fas fa-times"></i>删除申请[/@]
     [/#if]
  [/#assign]
  [@b.card class="card-info card-outline"]
     [@b.card_header]
      ${title}
     [/@]
     [#include "info.ftl"/]
  [/@]
  [/#list]
[#else]

[/#if]
</div>
<script>
   function removeApply(elem){
       if(confirm("确定删除?")){
         return bg.Go(elem,null)
       }else{
         return false;
       }
   }
</script>
[@b.foot/]
