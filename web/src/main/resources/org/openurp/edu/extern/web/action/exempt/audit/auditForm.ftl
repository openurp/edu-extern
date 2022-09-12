[#ftl]
[@b.toolbar title="${apply.std.name}的免修申请"]
  bar.addBack();
[/@]
[#assign std= apply.std/]
[@b.card class="card-info card-outline"]
  [@b.card_header]
    <i class="fas fa-school"></i>&nbsp;${apply.subject.name}<span style="font-size:0.8em">(${apply.acquiredOn?string("yyyy-MM")})</span>
  [/@]

  <table class="infoTable">
    <tr>
      <td class="title">考试科目：</td>
      <td>${apply.subject.name}</td>
      <td class="title">成绩：</td>
      <td>${apply.scoreText!}</td>
      <td class="title">填写时间：</td>
      <td>${(std.updatedAt?string("yyyy-MM-dd HH:mm"))!}</td>
    </tr>
    <tr>
      <td class="title">获得日期：</td>
      <td>${apply.acquiredOn}</td>
      <td class="title">证书编号：</td>
      <td>${apply.certificate!}</td>
      <td class="title">免修课程：</td>
      <td>[#list apply.courses as course]${course.name}[#sep],[/#list]</td>
    </tr>
    <tr>
      <td class="title">成绩材料：</td>
      <td>
        <a href="${attachmentPath}" target="_blank"><i class="fa fa-paperclip"></i>下载附件</a>
      </td>
      <td class="title">审核部门：</td>
      <td>${apply.auditDepart.name}</td>
      <td class="title">审核状态：</td>
      <td><span class="[#if apply.status=="通过"]text-success[#else]text-danger[/#if]">${apply.status} ${apply.auditOpinion!}</span></td>
    </tr>
    <tr>
      <td class="title">申请理由：</td>
      <td colspan="5">${apply.reasons!}</td>
    </tr>
    [#if attachmentPath?contains(".jpg")||attachmentPath?contains(".png")]
    <tr>
      <td class="title">材料图片：</td>
      <td colspan="5"><image src="${attachmentPath}" style="min-width:400px"/></td>
    </tr>
    [/#if]
  </table>
  [#if editables?seq_contains(apply.status)]
  [@b.form name="applyForm" action="!audit" theme="list"]
    [@b.radios name="passed" value="1" label="是否同意" required="true"/]
    [@b.textarea name="auditOpinion" required="true" rows="4" style="width:80%" label="审核意见" value="同意免修"/]
    [@b.formfoot]
    <input name="id" value="${apply.id}" type="hidden"/>
    [@b.submit value="提交"/]
    [/@]
  [/@]
  [/#if]
[/@]

