[#ftl]
[@b.head/]
  [@b.grid items=configs var="config"]
      [@b.gridbar title="免修设置维护"]
          bar.addItem("${b.text("action.new")}",action.add());
          bar.addItem("${b.text("action.edit")}",action.edit());
          bar.addItem("${b.text("action.delete")}",action.remove());
          bar.addItem("科目维护","editSetting()");
      [/@]
      [@b.row]
          [@b.boxcol/]
          [@b.col property="level.name" title="培养层次" width="10%"/]
          [@b.col property="eduType.name" title="培养类型" width="10%"/]
          [@b.col property="beginAt" title="开放时间"  width="25%"]${config.beginAt?string("yyyy-MM-dd HH:mm")}~${config.endAt?string("yyyy-MM-dd HH:mm")}[/@]
          [@b.col title="科目"][#list config.settings as s]${s.subject.name}[#if s_has_next]&nbsp;[/#if][/#list] [/@]
      [/@]
  [/@]
[@b.form name="configListForm" action="!search"][/@]
<script>
    function editSetting(){
        var configIds=bg.input.getCheckBoxValues('config.id');
        if(configIds==''||configIds.indexOf(',')!=-1){
            alert("请仅选择一条记录!");
            return false;
        }
        bg.form.addInput(document.configListForm, "setting.config.id",configIds);
        bg.form.submit(document.configListForm,"${b.url('setting!search')}");
    }
</script>
[@b.foot/]
