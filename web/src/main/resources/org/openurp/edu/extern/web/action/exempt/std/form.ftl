[#ftl]
[@b.head/]
  [@b.toolbar title="校外考试成绩添加/修改"]
    bar.addBack();
  [/@]
<div class="container" style="width:95%">
  [@b.card]
    [@b.card_body]
      [@b.form name="exemptionForm" action="!save" theme="list"  onsubmit="checkAttachment" enctype="multipart/form-data"]
        [@b.field label="学号"]${(apply.std.code)!}[/@]
        [@b.field label="姓名"]${(apply.std.name)!}[/@]
        [@b.field label="考试科目"]
          ${apply.subject.name} [#if setting.minScore??]分数不低于${setting.minScore}分[/#if] [#if setting.validMonths??]${(setting.validMonths/12.0)}年有效期[/#if]
          &nbsp;${setting.remark!}
        [/@]
        [@b.field label="审核部门"]${setting.auditDepart.name}[/@]
        [@b.select label="免修课程" multiple="true" items=courses values=apply.courses required="true" name="courseId" chosenMin="1"/]
        [@b.select name="apply.gradingMode.id" items=gradingModes label="记录方式" empty="..." required="true" value=apply.gradingMode! style="width:150px" /]
        [@b.textfield name="apply.scoreText" maxlength="5" label="成绩" check="match('number')" required="true"  value=apply.scoreText! style="width:150px" comment="须数字"/]
        [@b.datepicker label="获得日期" name="apply.acquiredOn" value=(apply.acquiredOn)! required="true"/]
        [@b.textfield name="apply.certificate" label="证书编号" value=(apply.certificate)! maxlength="100" /]
        [@b.field label="成绩证明材料" required="true"]
         <input type="file" name="attachment">
         [#if apply.attachmentPath??]已上传[/#if]
        [/@]
        [@b.textarea name="apply.reasons" label="申请理由" value=apply.reasons maxlength="500" rows="5" style="width:70%" required="true"/]
        [@b.formfoot]
          <input type="hidden" name="apply.id" value="${apply.id!}"/>
          <input type="hidden" name="projectId" value="${(apply.std.project.id)!}"/>
          <input type="hidden" name="settingId" value="${setting.id}"/>
          [@b.submit value="提交申请" /]
        [/@]
        <script>
          function checkAttachment(form){
            [#if !apply.attachmentPath??]
            if("" == form['attachment'].value){
              alert("缺少成绩证明材料");
              return false;
            }
            [/#if]
            return true;
          }
        </script>
      [/@]
    [/@]
  [/@]
</div>
[@b.foot/]
