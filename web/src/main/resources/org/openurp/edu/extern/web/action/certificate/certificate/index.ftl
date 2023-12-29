[#ftl]
[@b.head/]
[#include "../nav.ftl"/]
<div class="search-container">
  <div class="search-panel">
    [@b.form name="certificateSearchForm" action="!search" target="certificatelist" title="ui.searchForm" theme="search"]
      [@b.textfields names="certificate.code;代码"/]
      [@b.textfields names="certificate.name;名称"/]
      <input type="hidden" name="orderBy" value="certificate.code"/>
    [/@]
  </div>
  <div class="search-list">
    [@b.div id="certificatelist" href="!search?orderBy=certificate.code"/]
  </div>
</div>
[@b.foot/]
