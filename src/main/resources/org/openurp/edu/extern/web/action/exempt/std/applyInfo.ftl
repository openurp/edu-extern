[#ftl]
[#assign std= apply.std/]
<table class="infoTable">
    <tr>
      <td class="title" style="width:12%">考试科目：</td>
      <td>${apply.certificate.name}</td>
      <td class="title" style="width:12%">成绩：</td>
      <td>${apply.scoreText!}</td>
      <td class="title" style="width:12%">填写时间：</td>
      <td>${(std.updatedAt?string("yyyy-MM-dd HH:mm"))!}</td>
    </tr>
    <tr>
      <td class="title">获得年月：</td>
      <td>${apply.acquiredIn}</td>
      <td class="title">证书编号：</td>
      <td>${apply.certificateNo!}</td>
      <td class="title">免修课程：</td>
      <td>[#list apply.courses as course]${course.name}[#sep],[/#list]</td>
    </tr>
    <tr>
      <td class="title">成绩材料：</td>
      <td>
        [#if attachmentPaths.get(apply)??]
        <a href="${attachmentPaths.get(apply)}" target="_blank"><i class="fa fa-paperclip"></i>下载附件</a>
        [#else]--[/#if]
      </td>
      <td class="title">审核部门：</td>
      <td>${apply.auditDepart.name}</td>
      <td class="title">审核状态：</td>
      <td><span class="[#if apply.status.id=100]text-success[#else]text-danger[/#if]">${apply.status} ${apply.auditOpinion!}</span></td>
    </tr>
    <tr>
      <td class="title">申请理由：</td>
      <td colspan="5">${apply.reasons!}</td>
    </tr>
  </table>
