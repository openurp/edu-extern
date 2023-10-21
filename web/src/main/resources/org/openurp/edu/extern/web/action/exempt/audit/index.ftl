[#ftl]
[@b.head/]
<div class="search-container">
  <div class="search-panel">
      [@b.form name="applysearchForm" action="!search" title="ui.searchForm" target="applyList" theme="search"]
          <input type="hidden" name="orderBy" value="apply.updatedAt desc"/>
          [@b.textfield name="apply.std.code" label="学号"/]
          [@b.textfield name="apply.std.name" label="姓名"/]
          [@b.textfield name="apply.std.state.grade" label="年级"/]
          [@b.select name="apply.std.level.id" items=levels label="培养层次" empty="..."/]
          [@b.select name="apply.subject.id" items=subjects label="考试科目" empty="..."/]
          [@b.textfield name="exemptCourseName" label="免修课程" placeholder="课程名称"/]
          [@b.datepicker label="考试日期" name="apply.acquiredOn"/]
          [@b.select items=statuses label="状态" empty="..." name="apply.status"/]
      [/@]
  </div>
  <div class="search-list">
      [@b.div id="applyList" href="!search" /]
  </div>
</div>

[@b.foot/]
