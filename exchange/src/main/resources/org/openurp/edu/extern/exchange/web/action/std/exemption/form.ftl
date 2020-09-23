[#ftl]
[@b.head/]
  [@b.toolbar title="校外学习经历添加/修改"]
    bar.addBack();
  [/@]
  [#include "../lib/step.ftl"/]
  [@displayStep ['填写外校学习经历','填写学习成绩','填写冲抵免试的本校课程,提交审核'] 0/]
<div class="container" style="width:95%">
  <div class="panel panel-default">
    <div class="panel-body">
  [@b.form name="exchangeStudentForm" action="!save" theme="list"  enctype="multipart/form-data"  onsubmit="checkAttachment"]
    [#assign elementSTYLE = "width: 200px"/]
    [@b.field label="学号"]${(exchangeStudent.std.user.code)!}[/@]
    [@b.field label="姓名"]${(exchangeStudent.std.user.name)!}[/@]
    [@b.field label="校外学校" ]
      [@b.select name="exchangeStudent.school.id" items=schools?sort_by("name") empty="...手工添加..." value=(exchangeStudent.school.id)! theme="html" /]
      或<input type="text" name="newSchool" maxlength="100" placeholder="列表中没有,手动添加学校" title="列表中没有,手动添加学校" style=elementSTYLE/]
    [/@]
    [@b.select label="培养层次" name="exchangeStudent.level.id" items=levels empty="..." required="true" value=(exchangeStudent.level.id)! style=elementSTYLE/]
    [@b.select label="教育类别" name="exchangeStudent.category.id" items=eduCategories empty="..." required="true" value=(exchangeStudent.category.id)! style=elementSTYLE/]
    [@b.textfield label="外校专业" name="exchangeStudent.majorName" value=(exchangeStudent.majorName)! required="true" maxlength="100" style=elementSTYLE/]
    [@b.startend label="就读时间" name="exchangeStudent.beginOn,exchangeStudent.endOn" start=(exchangeStudent.beginOn)! end=(exchangeStudent.endOn)! required="true"/]
    [@b.field label="成绩证明材料"]
     <input type="file" name="transcript" >
     [#if exchangeStudent.transcriptPath??]已上传[/#if]
    [/@]
    [@b.formfoot]
      <input type="hidden" name="exchangeStudent.id" value="${(exchangeStudent.id)!}"/>
      <input type="hidden" name="project.id" value="${(exchangeStudent.std.project.id)!}"/>
      [@b.submit value="保存,进入填写学习成绩" class="btn btn-sm btn-default"/]
    [/@]
  [/@]
  </div>
 </div>
</div>
<script>
   function checkAttachment(form){
    [#if !exchangeStudent.transcriptPath??]
    if("" == form['transcript'].value){
      alert("请上传成绩相关证明材料");
      return false;
    }
    [/#if]
    return true;
  }
</script>
[@b.foot/]
