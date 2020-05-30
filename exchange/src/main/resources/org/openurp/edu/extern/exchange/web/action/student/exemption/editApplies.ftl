[#ftl]
[@b.head/]
  [@b.toolbar title="校外学习冲抵本校课程"]
    bar.addBack();
  [/@]
[#include "../lib/step.ftl"/]
[@displayStep ['填写外校学习经历','填写学习成绩','填写冲抵免试的本校课程,提交审核'] 2/]
<div class="container" style="width:95%">
  <div class="panel panel-default">
    <div class="panel-body">
    [#assign creditLimitComment=""/]
    [#if exemptionCredit?? && exemptionCredit.maxValue>0]
       [#assign creditLimitComment= ",<span style='color:red'>冲抵认定后的学分和不超过"+(exemptionCredit.maxValue-exemptionCredit.exempted)+"分</span>" /]
    [/#if]
  [@b.form name="exchangeStudentForm" action="!saveApplies" theme="list"]
    [@b.field label="学号"]${(exchangeStudent.std.user.code)!} ${(exchangeStudent.std.user.name)!}[/@]
    [@b.field label="校外学校"]${exchangeStudent.school.name}(${exchangeStudent.beginOn?string("yyyy-MM")}~${exchangeStudent.endOn?string("yyyy-MM")})[/@]
    [@b.textfield name="comment" label="免试冲抵" disable="true" value="在下面每个成绩中选择一个我校课程，进行匹配冲抵。"  style="width:300px;border:0px" comment="外校课程学习成绩"+creditLimitComment/]
    [#list exchangeStudent.grades?sort_by("id") as m]
    [@b.select width="300px" label="课程"+(m_index+1) multiple="true" items=planCourses option=r"${item.code} ${item.name} ${item.credits}分" chosenMin="0" name="grade_${m.id}.courses" comment="${m.courseName} ${m.credits}学分 ${m.scoreText}"]
     [#list (m.courses)! as course]
      <option value="${(course.id)!}" selected>${(course.code)!} ${(course.name)!} ${course.credits}分</option>
     [/#list]
    [/@]
    [/#list]
    [@b.formfoot]
      <input type="hidden" name="exchangeStudent.id" value="${(exchangeStudent.id)!}"/>
      [@b.submit value="提交" class="btn btn-sm btn-default"/]
    [/@]
  [/@]
  </div>
 </div>
</div>