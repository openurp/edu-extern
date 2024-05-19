[#ftl/]
[@b.head/]
[@b.toolbar title="开关维护详细信息"]
    bar.addItem("${b.text("action.edit")}","bg.Go('config!edit.action','configList');");
    bar.addBack("${b.text("action.back")}");
[/@]
[#assign labInfo]${b.text("ui.building.info")}[/#assign]
<table class="infoTable" width="100%" >
     <tr>
          <td class="title" width="15%">学期</td>
          <td class="content" width="85%" colspan='3'>${config.semester.schoolYear}-${(config.semester.name)!}</td>
     </tr>
     <tr>
          <td class="title">开关代码</td>
        <td class="content">${(config.code)!}</td>
          <td class="title">开关名称</td>
          <td class="content">${(config.name)!}</td>
    </tr>
     <tr>
          <td class="title">开始日期</td>
          <td class="content">${(config.beginAt?string("yyyy-MM-dd HH:mm"))?if_exists}</td>
          <td class="title">结束日期</td>
          <td class="content">${(config.endAt?string("yyyy-MM-dd HH:mm"))?if_exists}</td>
     </tr>
     <tr>
          <td class="title" width="15%">承诺书</td>
          <td class="content" width="85%" colspan="3"><pre>${config.notice?default("")}</pre></td>
     </tr>
     <tr>
          <td class="title" width="15%">考试校区</td>
          <td class="content" colspan="3" width="85%">
          [#if config.campuses?exists]
              [#list config.campuses as ampus]${(ampus.name)!}&nbsp;[/#list]
          [/#if]
          </td>
     </tr>
     <tr>
          <td class="title" width="15%">备注</td>
          <td class="content" colspan="3" width="85%">${(config.remark?html)!}</td>
     </tr>
</table>
[@b.grid items=config.settings var="setting" sortable="false"]
    [@b.form name="configInfoForm" action="!search"]
        [@b.row]
            [@b.col property="certificate.name" title="报名科目"/]
            [@b.col property="certificate.code" title="科目代码"/]
            [@b.col property="feeOfSignup" title="要求报名费"/]
            [@b.col property="feeOfMaterial" title="要求材料费"/]
            [@b.col property="feeOfOutline" title="要求考纲费"/]
            [@b.col property="maxStd" title="最大学生数"/]
            [@b.col property="grade" title="年级"/]
            [@b.col property="dependsOn.name" title="必须通过的科目(条件)"/]
            [@b.col property="config.campuses.name" title="考试地点"]
               [#if setting.config.campuses?exists]
                  [#list setting.config.campuses as ampus]${(ampus.name)!}&nbsp;[/#list]
                 [/#if]
            [/@]
        [/@]
    [/@]
[/@]
[@b.foot/]
