[#ftl]
[@b.toolbar title="${externStudent.std.name}的跨校交流"]
  bar.addBack();
[/@]
[@b.card class="card-info card-outline"]
  [@b.card_header]
     <i class="fas fa-school"></i>&nbsp;${externStudent.school.name}<span style="font-size:0.8em">(${externStudent.beginOn?string("yyyy-MM")}~${externStudent.endOn?string("yyyy-MM")})</span>
  [/@]
[#assign std= externStudent.std/]
<table class="infoTable">
    <tr>
      <td class="title" width="10%">学号：</td>
      <td width="23%">${(std.code)!}</td>
      <td class="title" width="10%">姓名：</td>
      <td width="23%">${std.name?html}</td>
      <td class="title" width="10%">修读专业：</td>
      <td>${(externStudent.majorName?html)!}</td>
    </tr>
    <tr>
      <td class="title">培养层次：</td>
      <td>${externStudent.level.name}</td>
      <td class="title">教学类别：</td>
      <td>${externStudent.category.name}</td>
      <td class="title">填写时间：</td>
      <td>${(externStudent.updatedAt?string("yyyy-MM-dd HH:mm"))!}</td>
    </tr>
    <tr>
      <td class="title">累计学分：</td>
      <td>${externStudent.credits}分,冲抵${externStudent.exemptionCredits}分</td>
      <td class="title">成绩材料：</td>
      <td>[#if transcriptPath??]
         <a href="${transcriptPath}" target="_blank"><i class="fa fa-paperclip"></i>下载附件</a>
         [#else]--[/#if]
      </td>
      <td class="title">审核状态：</td>
      <td><span class="[#if externStudent.auditState=="审核通过"]text-success[#else]text-danger[/#if]">${externStudent.auditState}${externStudent.auditOpinion!}</span></td>
    </tr>
  </table>
    [@b.grid items=externStudent.grades sortable="false" var="grade" ]
        [@b.row]
            [@b.col title="序号" width="5%"]${grade_index+1}[/@]
            [@b.col property="courseName" title="课程名称" width="25%"/]
            [@b.col property="credits"  title="学分" width="5%"/]
            [@b.col property="scoreText" title="成绩" width="5%"/]
            [@b.col property="acquiredIn" title="获得年月" width="10%"]${grade.acquiredIn?string("yyyy-MM")}[/@]
            [@b.col title="免修冲抵" width="30%"]
               [#list grade.exempts as c]
                 ${c.code} ${c.name} ${c.defaultCredits}分[#if c_has_next]<br>[/#if]
               [/#list]
            [/@]
            [@b.col property="remark" title="说明" width="20%"/]
        [/@]
    [/@]
[/@]
  [@b.form name="externStudentAuditForm" action="!audit"]
    <input name="passed" value="" type="hidden"/>
    <input name="id" value="" type="hidden"/>
  [/@]
<script>
   function audit(id,passed){
       var msg="确定审核通过?"
       if(passed=="0"){
          msg="确定审核不通过?"
       }
       if(confirm(msg)){
         var form=document.externStudentAuditForm;
         form['id'].value=id;
         form['passed'].value=passed;
         bg.form.submit(form);
         return false;
       }else{
         return false;
       }
   }
</script>
