[#ftl/]
[@b.head/]
[@b.toolbar title="报名管理 "/]
<div class="search-container">
  <div class="search-panel">
      [@b.form theme="search" name="signupsearchForm" action="!search" title="ui.searchForm" target="signupList"]
          [@base.semester value=currentSemester name="signup.semester.id" label="学年学期"/]
          [@b.textfield name="signup.std.code" label="学号"/]
          [@b.textfield name="signup.std.name" label="姓名"/]
          [@b.textfield name="signup.std.state.grade" label="年级"/]
          [@b.select name="signup.std.level.id" id="levelTypeId" label="培养层次" items=levels empty="..."/]
          [@b.select name="signup.std.state.department.id" label="院系" items=departments empty="..." /]
          [@b.select name="signup.subject.category.id" label="考试类型" items=categories empty="..."/]
          [@b.select name="signup.subject.id" label="报名科目" items=subjects empty="..." /]
          [@b.textfield name="signup.std.squad.name" label="班级名称"/]
          <input type="hidden" name="orderBy" value="signup.updatedAt desc"/>
      [/@]
  </div>
  <div class="search-list">
      [@b.div id="signupList" href="!search?orderBy=signup.updatedAt desc&signup.semester.id="+(currentSemester.id)!/]
  </div>
</div>
[@b.foot/]
