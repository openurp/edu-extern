[#ftl]
[@b.head/]
<div class="search-container">
  <div class="search-panel">
    [@b.form name="certificateCategorySearchForm" action="!search" target="certificateCategorylist" title="ui.searchForm" theme="search"]
      [@b.textfields names="certificateCategory.code;代码"/]
      [@b.textfields names="certificateCategory.name;名称"/]
      <input type="hidden" name="orderBy" value="certificateCategory.code"/>
    [/@]
  </div>
  <div class="search-list">
    [@b.div id="certificateCategorylist" href="!search?orderBy=certificateCategory.code"/]
  </div>
</div>
[@b.foot/]
