[#ftl]
[@b.head/]
  [@b.grid items=configs var="config"]
    [@b.gridbar title="校外考试开关维护"]
      bar.addItem("${b.text("action.new")}",action.add());
      bar.addItem("${b.text("action.edit")}",action.edit());
      bar.addItem("${b.text("action.delete")}",action.remove());
      bar.addItem("科目维护","editSetting()");
      bar.addItem("学生范围","editScope()");
    [/@]
    [@b.row]
      [@b.boxcol/]
      [@b.col property="semester.id" title="学年学期" width="10%"]${config.semester.schoolYear}-${config.semester.name}[/@]
      [@b.col property="name" title="开关名称" width="15%"]
          [@b.a title="查看详细信息" href="config!info?id=${(config.id)!}"] [#if config.prediction]<span class="badge badge-warning">预</span>[/#if] ${(config.name)!}[/@]
      [/@]
      [@b.col property="beginAt" title="开放时间"  width="25%"]${config.beginAt?string("yyyy-MM-dd HH:mm")}~${config.endAt?string("yyyy-MM-dd HH:mm")}[/@]
      [@b.col title="证书列表"][#list config.certificates as s]${s.name}[#if s_has_next]&nbsp;[/#if][/#list] [/@]
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
        bg.form.addInput(document.configListForm, "config.id",configIds);
        bg.form.submit(document.configListForm,"${b.url('setting!search')}");
    }
    function editScope(){
        var configIds=bg.input.getCheckBoxValues('config.id');
        if(configIds==''||configIds.indexOf(',')!=-1){
            alert("请仅选择一条记录!");
            return false;
        }
        bg.form.addInput(document.configListForm, "config.id",configIds);
        bg.form.submit(document.configListForm,"${b.url('scope!search')}");
    }
</script>
[@b.foot/]
