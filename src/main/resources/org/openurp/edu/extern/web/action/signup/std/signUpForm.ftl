[#ftl]
[@b.head/]
<div class="container">
  [#assign toolBarTitle]${((setting.config.notice!)=="")?string("二","三")}[/#assign]
  [@b.toolbar title="校外考试报名(第"+toolBarTitle+"步)"]
    bar.addBack("${b.text("action.back")}");
  [/@]
  [@b.card class="card-info card-outline"]
    [@b.card_header]报名确认[/@]
    [@b.form name="actionForm" action="!save"]
      <table class="formTable" width="100%" align="center">
          <input type="hidden" name="setting.id" value="${setting.id}"/>
          <tr class="darkColumn">
            <td colspan="4">个人信息确认</td>
          </tr>
          <tr>
            <td class="title">学号:</td>
            <td>${student.code?if_exists}</td>
            <td class="title">姓名:</td>
            <td>${student.name?if_exists}</td>
          </tr>
          <tr>
            <td class="title" width="15%">年级</td>
            <td> ${student.state.grade?if_exists}</td>
            <td class="title" width="15%">出生日期</td>
            <td>[#if (student.person.birthday)??]${student.person.birthday?string("yyyy-MM-dd")}[/#if]</td>
          </tr>
          <tr>
            <td class="title" width="15%">所属院系</td>
            <td> ${student.state.department.name?if_exists}</td>
            <td class="title" width="15%">专业</td>
            <td> ${student.state.major.name?if_exists}</td>
           </tr>
          <tr>
            <td class="title">身份证号:</td>
            <td colspan='3'>${(student.person.code)?if_exists}</td>
          </tr>

          <tr class="darkColumn" style="border-top-width:1;border-color:#006CB2;">
            <td colspan="4">报名信息确认</td>
          </tr>
        <tr>
          <td class="title">报名科目:</td>
          <td colspan="3">${setting.certificate.name}</td>
        </tr>
        <tr>
          <td colspan="4" align="center">
            [@b.submit class="btn btn-primary btn-sm" value="确认信息,提交报名"/]
        </tr>
      </table>
    [/@]
  [/@]
</div>
[@b.foot/]
