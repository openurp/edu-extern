[#ftl]
[@b.head/]
[@b.toolbar title="开关设置"]
[/@]
<div class="search-container">
  <div class="search-panel">
        [@b.form name="configIdxForm" action="!search" title="ui.searchForm" target="configList" theme="search"]
            [@b.select name="config.category.id" label="考试类型" items=categories empty="..." /]
            [@b.textfield name="config.name" label="开关名称"/]
            [@b.select name="config.opened" label="是否开放" items={"1":"是","0":"否"} empty="..." /]
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
