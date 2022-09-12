[#ftl]
[@b.head/]
[@b.toolbar title="免修设置"]
[/@]
<div class="search-container">
  <div class="search-panel">
    [@b.form name="configIdxForm" action="!search" title="ui.searchForm" target="configList" theme="search"]
      [@urp_base.semester value=currentSemester label="学年学期"/]
    [/@]
  </div>
  <div class="search-list">
    [@b.div id="configList"/]
  </div>
</div>
<script>
    jQuery(function(){
        bg.form.submit(document.configIdxForm);
    });
</script>
[@b.foot/]
