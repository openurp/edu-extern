[#ftl]
[@b.head/]
<div class="container">
  [@b.toolbar title="校外考试报名(第一步)"]
     bar.addBack("${b.text("action.back")}");
  [/@]
  [@b.messages slash="3"/]
  [@b.card class="card-info card-outline"]
    [@b.card_header]报名信息[/@]
    <table width="100%" style="background-color:#FEFFCF"  align="center">
         <tr>
            <td width="15%">
              <img width="80px" height="110px" src="${b.base}/avatar/my.action" alt="${(student.name)!}" title="${(student.name)!}"/>
            </td>
          <td width="85%">
            ${(student.name)!}同学(${(student.code)!})，您好<br>
            <font color='red'>
              你的身份证为:${(student.person.code)!'系统中暂时没有你的身份证号码'}<br>
              如没有显示或数据有误请及时联系学院，以免耽误你的报名。
            </font>
          </td>
        </tr>
      </table>
      [#if signUpList?size>0]
      [@b.grid items=signUpList var="signUp" sortable="false"]
        [@b.row]
          [@b.col title="学年学期" width="20%"]${(signUp.semester.schoolYear)!}学年 ${(signUp.semester.name)!}[/@]
          [@b.col property="subject.name" title="报名科目"/]
          [@b.col property="updatedAt" title="报名时间"]${(signUp.updatedAt?string("yyyy-MM-dd HH:mm:ss"))?if_exists}[/@]
          [@b.col title="操作"]<button class="btn btn-danger btn-sm" onclick="cancelSignUp('${signUp.id}')">取消报名</button>[/@]
        [/@]
      [/@]
      [/#if]
  [/@]

[#list configs as config]
[@b.card class="card-info card-outline"]
  [@b.card_header][#if config.prediction]<span class="badge badge-warning">预</span>[/#if]&nbsp;${config.name}&nbsp;(报名时间：${(config.beginAt?string("yyyy-MM-dd hh:mm"))!}~${(config.endAt?string("yyyy-MM-dd hh:mm"))!})[/@]
      <table class="gridtable" width="100%"  align="center">
      <thead class="gridhead">
        <tr class="darkColumn" align="center">
          <th>报名科目</th>
          <th>考试时间</th>
          <th>要求通过的科目</th>
          <th>点击即可报名</th>
        </tr>
      </thead>
      <tbody>
      [#list config.settings?sort_by(["subject","code"]) as setting]
        [#if setting_index % 2 == 0]
          [#assign lessonClass="griddata-even"/]
        [#else]
          [#assign lessonClass="griddata-odd"/]
        [/#if]
        <tr class="${lessonClass!}">
          <td>${(setting.subject.name)!}</td>
          <td>${(setting.examOn?string('yyyy-MM-dd'))!} [#if setting.examBeginAt.value>0] ${setting.examBeginAt}-${setting.examEndAt!}[/#if]</td>
          <td>${(setting.dependsOn.name)!'无'}</td>
          <td align="center">
            [#if (!signUpSubjects?? || !signUpSubjects?seq_contains(setting.subject))]
              <button class="btn btn-primary btn-sm" onclick="signUp('${setting.id}')">报名</button>
            [#else]
              已报名
            [/#if]
        </td>
        </tr>
      [/#list]
      </tbody>
      </table>
     [/@]
   [/#list]
  [@b.form name="actionForm" method="post" action="!notice"/]

  <script language="javascript">
    function cancelSignUp(signUpId){
      if(confirm("确定取消你选择的科目")){
        document.actionForm.action="${b.url('!cancel')}?signupId="+signUpId;
        bg.form.submit(document.actionForm);
      }
    }

    function signUp(settingId){
      [#if (student.person.code)??]
        if(confirm("确认你的身份证等各种信息数据都准确无误，继续报名请按确定")){
          bg.form.addInput(document.actionForm,"setting.id",settingId);
          bg.form.submit(document.actionForm);
        }
      [#else]
        alert("你的身份证信息缺失,请到教务主管部门进行填写。");
      [/#if]
    }
  </script>
</div>
[@b.foot/]
