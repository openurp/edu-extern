[#ftl]
[@b.head/]
[@b.toolbar title="开关设置"]
[/@]
<div class="search-container">
  <div class="search-panel">
        [@b.form name="configIdxForm" action="!search" title="ui.searchForm" target="configList" theme="search"]
            [@b.textfield name="config.name" label="开关名称"/]
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
