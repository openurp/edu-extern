[#ftl]
[#assign std= exchangeStudent.std/]
<table class="infoTable">
    <tr>
      <td class="title" width="10%">学号：</td>
      <td width="23%">${(std.user.code)!}</td>
      <td class="title" width="10%">姓名：</td>
      <td width="23%">${std.user.name?html}</td>
      <td class="title" width="10%">修读专业：</td>
      <td>${(exchangeStudent.majorName?html)!}</td>
    </tr>
    <tr>
      <td class="title">培养层次：</td>
      <td>${exchangeStudent.level.name}</td>
      <td class="title">教学类别：</td>
      <td>${exchangeStudent.category.name}</td>
      <td class="title">填写时间：</td>
      <td>${(exchangeStudent.updatedAt?string("yyyy-MM-dd HH:mm"))!}</td>
    </tr>
    <tr>
      <td class="title">累计学分：</td>
      <td>${exchangeStudent.credits}分,冲抵${exchangeStudent.exemptionCredits}分</td>
      <td class="title">成绩材料：</td>
      <td>[#if transcriptPaths.get(exchangeStudent)??]
         <a href="${transcriptPaths.get(exchangeStudent)}" target="_blank"><span class="glyphicon glyphicon-download"></span>下载附件</a>
         [#else]--[/#if]
      </td>
      <td class="title">审核状态：</td>
      <td><span class="[#if exchangeStudent.state=="通过"]text-success[#else]text-danger[/#if]">${exchangeStudent.state}${exchangeStudent.auditOpinion!}</span></td>
    </tr>
  </table>
    [@b.grid items=exchangeStudent.grades sortable="false" var="grade" ]
        [@b.row]
            [@b.col title="序号" width="5%"]${grade_index+1}[/@]
            [@b.col property="courseName" title="课程名称" width="25%"/]
            [@b.col property="credits"  title="学分" width="5%"/]
            [@b.col property="scoreText" title="成绩" width="5%"/]
            [@b.col property="acquiredOn" title="获得年月" width="10%"]${grade.acquiredOn?string("yyyy-MM")}[/@]
            [@b.col title="免修冲抵" width="35%"]
               [#list grade.courses as c]
                 ${c.code} ${c.name} ${c.credits}分[#if c_has_next]<br>[/#if]
               [/#list]
            [/@]
            [@b.col property="remark" title="说明" width="15%"/]
        [/@]
    [/@]