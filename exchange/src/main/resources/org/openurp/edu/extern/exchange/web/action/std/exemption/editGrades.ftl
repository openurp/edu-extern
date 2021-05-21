[#ftl]
[@b.head/]
  [@b.toolbar title="校外学习成绩添加/修改"]
    bar.addBack();
  [/@]
[#include "../lib/step.ftl"/]
[@displayStep ['填写外校学习经历','填写学习成绩','填写冲抵免试的本校课程,提交审核'] 1/]

<div class="container" style="width:95%">
  <div class="panel panel-default">
    <div class="panel-body">
  [@b.form name="exchangeStudentForm" action="!saveGrades" theme="list"]
    [@b.field label="学号"]${(exchangeStudent.std.user.code)!} ${(exchangeStudent.std.user.name)!}[/@]
    [@b.field label="校外学校"]${exchangeStudent.school.name}(${exchangeStudent.beginOn?string("yyyy-MM")}~${exchangeStudent.endOn?string("yyyy-MM")})[/@]
    [#list exchangeStudent.grades?sort_by("id") as m]
    [@b.field label="课程"+(m_index+1)]
          <input name="grade_${m_index+1}.id" type="hidden" value="${m.id}">
          <input name="grade_${m_index+1}.courseName" maxlength="100" style="width:300px" value="${m.courseName}">
          <input name="grade_${m_index+1}.credits" type="number" maxlength="2" style="width:40px"  value="${m.credits}">
          <input name="grade_${m_index+1}.scoreText"  maxlength="10"  style="width:60px"  value="${m.scoreText}">
          <input name="grade_${m_index+1}.acquiredOn" style="width:100px"  class="Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'${exchangeStudent.beginOn?string('yyyy-MM-dd')}',maxDate:'${exchangeStudent.endOn?string('yyyy-MM-dd')}'})" value="${m.acquiredOn?string('yyyy-MM-dd')}">
          <input name="grade_${m_index+1}.remark" style="width:200px" maxlength="50" value="${m.remark!}">
    [/@]
    [/#list]
    [#assign start=exchangeStudent.grades?size+1]
    [#assign maxLines= 10/]
    [#if exchangeStudent.grades?size > 9]
      [#assign maxLines = exchangeStudent.grades +1 /]
    [/#if]
    [#list start..maxLines as i]
    [@b.field label="课程"+i]
      <input name="grade_${i}.courseName" value="" maxlength="100" style="width:300px" placeholder="课程名称" title="课程名称">
      <input name="grade_${i}.credits" value="" type="number" maxlength="2"  style="width:40px" placeholder="学分" title="学分">
      <input name="grade_${i}.scoreText" maxlength="10" value="" style="width:60px" placeholder="分数或等第" title="成绩">
      <input name="grade_${i}.acquiredOn" value="" class="Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'${exchangeStudent.beginOn?string('yyyy-MM-dd')}',maxDate:'${exchangeStudent.endOn?string('yyyy-MM-dd')}'})" style="width:100px" placeholder="获得年月" title="获得年月">
      <input name="grade_${i}.remark" style="width:200px" maxlength="50" value="" placeholder="说明" title="说明">
    [/@]
    [/#list]
    [@b.formfoot]
      <input type="hidden" name="exchangeStudent.id" value="${(exchangeStudent.id)!}"/>
      [@b.submit value="保存,进入选择免试课程" class="btn btn-sm btn-default"/]
    [/@]
  [/@]
  </div>
 </div>
</div>
