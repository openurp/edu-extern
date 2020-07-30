[#ftl]
[@b.head/]
<div class="search-container">
  <div class="search-panel">
    [@b.form name="certificateSubjectSearchForm" action="!search" target="certificateSubjectlist" title="ui.searchForm" theme="search"]
      [@b.textfields names="certificateSubject.code;代码"/]
      [@b.textfields names="certificateSubject.name;名称"/]
      <input type="hidden" name="orderBy" value="certificateSubject.code"/>
    [/@]
  </div>
  <div class="search-list">
    [@b.div id="certificateSubjectlist" href="!search?orderBy=certificateSubject.code"/]
  </div>
</div>
[@b.foot/]
