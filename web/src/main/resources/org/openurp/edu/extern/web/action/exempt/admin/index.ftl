[#ftl]
[@b.head/]
<div class="search-container">
  <div class="search-panel">
      [@b.form name="applysearchForm" action="!search" title="ui.searchForm" target="applyList" theme="search"]
          <input type="hidden" name="orderBy" value="apply.updatedAt desc"/>
          [@b.textfield name="apply.std.code" label="学号"/]
          [@b.textfield name="apply.std.name" label="姓名"/]
          [@b.textfield name="apply.std.state.grade" label="年级"/]
          [@b.datepicker label="考试日期" name="apply.acquiredOn"/]
          [@b.select items=statuses label="状态" empty="..." name="apply.status"/]
      [/@]

      [@b.form name="importForm" action="!importForm" target="applyList"/]
  </div>
  <div class="search-list">
      [@b.div id="applyList" href="!search" /]
  </div>
</div>

[@b.foot/]
